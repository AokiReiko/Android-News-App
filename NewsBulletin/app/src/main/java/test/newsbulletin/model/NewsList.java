package test.newsbulletin.model;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class NewsList {

    /**
     * An array of news items and their initialization.
     */
    private List<NewsListItem> newsList = new ArrayList<NewsListItem>();
    private int pageNumber = 1;
    private static final String traverse_base_url = "http://166.111.68.66:2042/news/action/query/latest";

    /**
     * A map of sample (dummy) items, by ID.
     */
    public Map<String, NewsListItem> newsMap = new HashMap<String, NewsListItem>();

    private static final int COUNT = 25;

    static String getSpecificPageUrl(int pageNo) {
        return traverse_base_url + "?pageNo=" + pageNo;
    }

    public NewsList() {
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
                int resCode = -1;
                String required_url = getSpecificPageUrl(pageNumber);
                try {
                    /* Open the url */
                    URL url = new URL(required_url);
                    Log.d("func",required_url);
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
                        addItem(new NewsListItem(String.valueOf(newsList.size()),obj.getString("news_Title"),obj.getString("news_ID"), obj.getString("news_Pictures")));
                    }

                    pageNumber += 1;

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
        public final Bitmap news_picture;
        public boolean isRead = false;

        public NewsListItem(String id, String content, String news_id, String url) {
            this.id = id;
            this.content = content;
            this.news_id = news_id;
            news_picture = null;//getHttpBitmap(url);
        }

        @Override
        public String toString() {
            return content;
        }
        public void markRead() {
            isRead = true;
        }
        public static Bitmap getHttpBitmap(String url) {
            URL myFileUrl = null;
            Bitmap bitmap = null;
            try {
                Log.d("func", url);
                myFileUrl = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setConnectTimeout(0);
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

    }
}
