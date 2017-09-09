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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;

import test.newsbulletin.model.NewsList;

/**
 * Created by 32928 on 2017/9/9.
 */

public class DetailList {

    public NewsListItem newsList;
    public Map<String, NewsListItem> newsMap = new HashMap<String, NewsListItem>();

    private static final String traverse_base_url = "http://166.111.68.66:2042/news/action/query/detail";
    public String pageID;
    static String getSpecificPageUrl(String detailID) {return traverse_base_url + "?newsID=" + detailID;}
    public DetailList(String pageid) {
        pageID = pageid;
        loadMore();
    }
    private synchronized void loadMore(){
        /* Not do this in our main thread. */
        Log.d("func", "loading page " + pageID );
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int resCode = -1;
                String required_url = getSpecificPageUrl(pageID);
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
                    JSONArray news_list = js_obj.getJSONArray("list");

                    for (int i = 0; i < news_list.length(); i++) {
                        JSONObject obj = news_list.getJSONObject(i);
                        //Log.d("func", "add " + i);
                        addItem(new NewsListItem(pageID,obj.getString("news_Title"),obj.getString("news_Author"),obj.getString("news_Content")));
                    }


                } catch (MalformedURLException eurl){
                    Log.d("func","url error");

                } catch (IOException eio) {
                    eio.printStackTrace();
                    Log.d("func","io error "+eio.getMessage());

                } catch (JSONException ejson) {
                    Log.d("func","json error "+ ejson.getMessage());

                } catch (Exception eso) {
                    Log.d("func", eso.getMessage());
                }
            }
        });
        thread.start();

        Log.d("func", "http finished");
        // Add some sample items.


    }
    private void addItem(NewsListItem item) {
        newsList = item;
        newsMap.put(item.id, item);
    }

    public static class NewsListItem {
        public final String id;
        public final String Title;
        public final String Author;
        public final String Content;

        public NewsListItem(String id, String title, String author, String content) {
            this.id = id;
            this.Title = title;
            this.Author = author;
            this.Content = content;
        }

        @Override
        public String toString() {
            return Content;
        }
    }
}

