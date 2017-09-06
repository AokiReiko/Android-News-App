package test.newsbulletin.dummy;
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
/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of news items and their initialization.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static int pageNumber = 1;
    public static final String traverse_base_url = "http://166.111.68.66:2042/news/action/query/latest";
    static {
        loadMore();
    }

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static String getSpecificPageUrl(int pageNo) {
        return traverse_base_url + "?pageNo=" + pageNo;
    }
    public static synchronized void loadMore(){
        /* Not do this in our main thread. */
        Log.d("func", "loading page " + pageNumber + ". " + "Size: " + ITEMS.size());
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
                        addItem(new DummyItem(String.valueOf(ITEMS.size()),obj.getString("news_Title"),obj.getString("news_Intro")));
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
    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
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
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
