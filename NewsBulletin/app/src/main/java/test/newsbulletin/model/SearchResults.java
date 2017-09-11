package test.newsbulletin.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by aokireiko on 9/10/17.
 */

public class SearchResults {

    private String key_word = "";
    private int pageNumber = 1;
    private static final String search_base_url = "http://166.111.68.66:2042/news/action/query/search";
    ArrayList<SearchResultItem> results = new ArrayList<>();

    public SearchResults() {}
    public SearchResults(String key) { key_word = key; }
    public void setKeyWord(String key) {
        key_word = key;
        pageNumber = 1;
    }
    public boolean search() {
        //url connection. Start a thread in its caller.
        try {
            int resCode = -1;
            String required_url = getUrl();
                    /* Open the url */
            URL url = new URL(required_url);
            Log.d("func", required_url);
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
            } else Log.d("func", resCode + "not ok");

            JSONObject js_obj = new JSONObject(html);
            JSONArray news_list = js_obj.getJSONArray("list");
            for (int i = 0; i < news_list.length(); i++) {
                JSONObject obj = news_list.getJSONObject(i);
                addItem(new SearchResultItem(String.valueOf(results.size()),obj.getString("news_Title"),obj.getString("news_ID"), obj.getString("news_Pictures")));
            }

            if (news_list.length() != 0) {
                pageNumber += 1;
            }
            return true;

        } catch (MalformedURLException eurl){
            Log.d("func","url error");
            return false;

        } catch (IOException eio) {
            eio.printStackTrace();
            Log.d("func","io error "+eio.getMessage());
            return false;

        } catch (JSONException ejson) {
            Log.d("func","json error "+ ejson.getMessage());
            return false;

        } catch (Exception eso) {
            Log.d("func", eso.getMessage());
            return false;
        }

    }

    public void clear() { results = new ArrayList<>(); }
    public SearchResultItem get(int i) {
        return results.get(i);
    }
    public int size() { return results.size(); }
    private String getUrl() {
        String url = search_base_url + "?keyword=" + key_word + "&pageNo=" + pageNumber;
        return url;
    }
    private void addItem(SearchResultItem item) {
        results.add(item);
    }

    public static class SearchResultItem {
        public final String id;
        public final String content;
        public final String news_id;
        public final Bitmap news_picture;
        public boolean isRead = false;

        public SearchResultItem(String id, String content, String news_id, String url) {
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
