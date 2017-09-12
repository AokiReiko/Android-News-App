package test.newsbulletin.model;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;

import test.newsbulletin.file.FileIO;
import test.newsbulletin.model.NewsList;

/**
 * Created by 32928 on 2017/9/9.
 */

public class DetailList {

    public NewsListItem newsList;
    public Map<String, NewsListItem> newsMap = new HashMap<String, NewsListItem>();

    private static final String traverse_base_url = "http://166.111.68.66:2042/news/action/query/detail";
    public String pageID;
    static String getSpecificPageUrl(String detailID) {return traverse_base_url + "?newsId=" + detailID;}
    public DetailList(String paged) {
        pageID = paged;
        try {
            loadMore();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    FileIO io = new FileIO();
    private synchronized void loadMore() throws InterruptedException {
        /* Not do this in our main thread. */
        Log.d("func", "ID !!! " + pageID );
        final DetailList detailListThis = this;
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int resCode = -1;
                String required_url = getSpecificPageUrl(pageID);
                Log.d("func", required_url);
                try {
                    /* Open the url */
                    URL url = new URL(required_url);
                    HttpURLConnection cnt = (HttpURLConnection)url.openConnection();
                    cnt.setRequestMethod("GET");
                    cnt.setConnectTimeout(5*1000);
                    InputStream in = cnt.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    /* Get all the content of the html */
                    String html = "";
                    {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            html += line;
                        }
                    }
                    //ToDo(aokireiko):judge when the connection fails.
                    resCode = cnt.getResponseCode();

                    if (resCode == HttpURLConnection.HTTP_OK) {
                        Log.d("func","" + resCode);
                    }

                    JSONObject js_obj = new JSONObject(html);
                    String tmp = js_obj.getString("news_Pictures");
                    String[] mm = tmp.split("[ ;]");
                    List<String> picture_url = new ArrayList<String>();
                    for (int i=0; i<mm.length; i++)
                    {
                        Log.d("check",mm[i] );
                        picture_url.add(mm[i]);
                    }
                    addItem(new NewsListItem(pageID,js_obj.getString("news_Title"),js_obj.getString("news_Author"),js_obj.getString("news_Content"),picture_url));


                }
                catch (Exception eso) {
                    if(!io.loadDetail(detailListThis)) {
                        List<String> list = new ArrayList<String>();
                        list.add("disconnect");
                        addItem(new NewsListItem("-1", "连接失败", "", "无法连接网络且本新闻未缓存", list));
                    }
                    Log.d("func", eso.getMessage());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Add some sample items.


    }
    private void addItem(NewsListItem item) {
        newsList = item;
        newsMap.put(item.id, item);
    }

    public static class NewsListItem implements Serializable{
        public final String id;
        public final String Title;
        public final String Author;
        public final String Content;
        public final List<String> Picture;

        public NewsListItem(String id, String title, String author, String content, List<String> picture) {
            this.id = id;
            this.Title = title;
            this.Author = author;
            this.Content = content;
            this.Picture = picture;

        }

        @Override
        public String toString() {
            return Content;
        }
    }
}

