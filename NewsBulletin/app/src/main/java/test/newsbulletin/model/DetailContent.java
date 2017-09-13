package test.newsbulletin.model;
import android.graphics.Bitmap;
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

public class DetailContent {

    public NewsDetailItem detailItem;
    public Map<String, NewsDetailItem> newsMap = new HashMap<String, NewsDetailItem>();

    private static final String detail_base_url = "http://166.111.68.66:2042/news/action/query/detail";
    public String detailID;
    static String getSpecificPageUrl(String detailID) {return detail_base_url + "?newsId=" + detailID;}
    public DetailContent(String detail_id) {
        detailID = detail_id;
    }
    FileIO io = new FileIO();

    public synchronized boolean loadMore() {
        /* Not do this in our main thread. */
        // 在调用此函数的caller里面开启线程
        int resCode = -1;
        String required_url = getSpecificPageUrl(detailID);
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
            String[] pics = js_obj.getString("news_Pictures").split("[ ;]");
            List<String> picture_url = new ArrayList<String>();
            for (int i = 0; i < pics.length; i++)
            {
                Log.d("check",pics[i] );
                picture_url.add(pics[i]);
            }
            detailItem = (new NewsDetailItem(detailID, js_obj.getString("news_Title"), js_obj.getString("news_Author"), js_obj.getString("news_Content"), picture_url));
            return true;

        } catch (Exception eso) {
            if(io.loadDetail(this))
                return true;
            else
            {
                List<String> list = new ArrayList<>();
                list.add("disconnect");
                detailItem = new NewsDetailItem("-1", "连接错误", "", "网络未连接，且文件未缓存", list);
                return true;
            }
        }

        // Add some sample items.


    }
    public Bitmap bitmap = null;

    public static class NewsDetailItem implements Serializable{
        public final String id;
        public final String Title;
        public final String Author;
        public final String Content;
        public final List<String> Picture;

        public NewsDetailItem(String id, String title, String author, String content, List<String> picture) {
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

