package test.newsbulletin.model;
import android.app.Notification;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.net.HttpURLConnection;

public class NewsList {

    /**
     * An array of news items and their initialization.
     */
    private List<NewsListItem> newsList = new ArrayList<NewsListItem>();
    private int pageNumber = 1;
    private String classTag = "最新";
    private static final String traverse_base_url = "http://166.111.68.66:2042/news/action/query/latest";
    private static final Map<String, Integer> tagMap = new HashMap<String , Integer>(){{
        put("科技", 1);
        put("教育", 2);
        put("军事", 3);
        put("国内", 4);
        put("社会", 5);
        put("文化", 6);
        put("汽车", 7);
        put("国际", 8);
        put("体育", 9);
        put("财经", 10);
        put("健康", 11);
        put("娱乐", 12);


    }};
    /**
     * A map of sample (dummy) items, by ID.
     */
    public Map<String, NewsListItem> newsMap = new HashMap<String, NewsListItem>();

    private static final int COUNT = 25;

    static String getSpecificPageUrl(int pageNo, String classTag) {
        Log.d("func", classTag+classTag.equals("最新")+tagMap.containsKey(classTag));
        String url = traverse_base_url + "?pageNo=" + pageNo;
        if (!classTag.equals("最新") && tagMap.containsKey(classTag)) {
            url += "&category=" + tagMap.get(classTag);
        }
        return url;
    }

    public NewsList() {
        loadMore();
    }
    public NewsList(String classTag) {
        Log.d("func", classTag);
        this.classTag = classTag;
        loadMore();
    }
    public NewsListItem get(int i) {
        if (i < newsList.size()) return newsList.get(i);
        return null;
    }
    public int size() {
        return newsList.size();
    }
    public synchronized void loadMore(){
        /* Not do this in our main thread. */
        Log.d("func", "loading page " + pageNumber + ". " + "Size: " + newsList.size());
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d("func","begin thread" +
                        "");
                int resCode = -1;
                String required_url = getSpecificPageUrl(pageNumber, classTag);
                Log.d("func",required_url);
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
                    Log.v("!!!", String.valueOf(news_list.length()));
                    for (int i = 0; i < news_list.length(); i++) {
                        JSONObject obj = news_list.getJSONObject(i);
                        String tmp = obj.getString("news_Pictures");
                        String[] mm = tmp.split("[ ;]");
                        List<String> picture_url = new ArrayList<String>();
                        for (int j=0; j<mm.length; j++)
                        {
                            Log.d("check",mm[j] );
                            picture_url.add(mm[j]);
                        }
                        Log.d("check","????" );
                        addItem(new NewsListItem(String.valueOf(newsList.size()),obj.getString("news_Title"),obj.getString("news_ID"),picture_url));
                    }

                    if (news_list.length() != 0) {
                        pageNumber += 1;
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
        newsList.add(item);
        newsMap.put(item.id, item);
    }


    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class NewsListItem {
        public final String id;
        public final String content;
        public final String news_id;
        public final List<String> picture_id;
        public boolean isRead = false;

        public NewsListItem(String id, String content, String news_id,List<String> pic) {
            this.id = id;
            this.content = content;
            this.news_id = news_id;
            this.picture_id = pic;
        }

        @Override
        public String toString() {
            return content;
        }
        public void markRead() {
            isRead = true;
        }

    }
}
