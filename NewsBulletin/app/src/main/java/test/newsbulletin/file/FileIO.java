package test.newsbulletin.file;

import android.app.Activity;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import test.newsbulletin.model.Data;
import test.newsbulletin.model.DetailList;
import test.newsbulletin.model.NewsList;

/**
 * Created by pushi on 2017/9/10.
 */

public class FileIO
{
    Activity activity;

    public FileIO(Activity _activity)
    {
        activity = _activity;
    }
    public void saveConfig() // 存储配置文件
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Config");
        if(!dir.isDirectory()) {
            dir.mkdir();
        }
        String filename = "Config/main.config";
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream object_out = new ObjectOutputStream(byte_out);
            Data data = (Data) activity.getApplication();
            object_out.writeObject(data.tabList);
            object_out.writeObject(data.unusedTabList);
            bytes = byte_out.toByteArray();
            byte_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "config save failed");
            return;
        }
        writeFile(bytes, filename);
        Log.d("func", "config save finished");
    }
    public boolean loadConfig() // 读取配置文件
    {
        String filename = "Config/main.config";
        byte[] bytes = readFile(filename);
        Object object_1, object_2;
        Data data;
        try {
            ByteArrayInputStream byte_in = new ByteArrayInputStream(bytes);
            ObjectInputStream object_in = new ObjectInputStream(byte_in);
            data = (Data) activity.getApplication();
            object_1 = (Object) object_in.readObject();
            object_2 = (Object) object_in.readObject();
        }
        catch(Exception e)
        {
            Log.d("func", "config load failed");
            return false;
        }
        data.tabList = (ArrayList<String>)object_1;
        data.unusedTabList = (ArrayList<String>)object_2;
        Log.d("func", "config load finished");
        return true;
    }

    public void saveDetail(DetailList list) // 收藏新闻时调用
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Detail");
        if(!dir.isDirectory())
            dir.mkdir();

        String id = list.pageID;
        String filename = "Detail/" + id + ".detail";
        byte[] bytes = null;
        try {

            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream object_out = new ObjectOutputStream(byte_out);
            object_out.writeObject(list.newsList);
            bytes = byte_out.toByteArray();
            byte_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "detail save failed");
            return;
        }
        writeFile(bytes, filename);
        Log.d("func", "detail save finished");
    }

    public void eraseDetail(DetailList list) // 取消收藏时调用
    {
        String id = list.pageID;
        File path = activity.getFilesDir();
        File dir = new File(path, "Details/");
        if(dir.isFile())
            dir.delete();
    }

    public boolean loadDetail(DetailList list) // 当某条收藏新闻被加载时调用（离线时调用）
    {
        String id = list.pageID;
        String filename = "Detail/" + id + ".detail";
        byte[] bytes = readFile(filename);
        try {
            ByteArrayInputStream byte_in = new ByteArrayInputStream(bytes);
            ObjectInputStream object_in = new ObjectInputStream(byte_in);
            list.newsList = (DetailList.NewsListItem) object_in.readObject();
        }
        catch(Exception e)
        {
            Log.d("func", "detail load failed");
            return false;
        }
        return true;
    }
    public void saveNewsList(NewsList list) // 存储新闻列表
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "NewsList");
        if(!dir.isDirectory())
            dir.mkdir();

        String filename = "NewsList/" + list.classTag;
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream object_out = new ObjectOutputStream(byte_out);
            object_out.writeObject(list.newsList);
            bytes = byte_out.toByteArray();
            byte_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "newslist save failed");
            return;
        }
        writeFile(bytes, filename);
        Log.d("func", "newslist save finished");
    }
    public boolean loadNewsList(NewsList list) // 需要设置好classTag, 之后读取该类别的新闻列表（离线时调用）
    {
        String catagory = list.classTag;
        String filename = "NewsList/" + catagory;
        byte[] bytes = readFile(filename);
        Object object;
        try {
            ByteArrayInputStream byte_in = new ByteArrayInputStream(bytes);
            ObjectInputStream object_in = new ObjectInputStream(byte_in);
            object = (Object) object_in.readObject();
        }
        catch(Exception e)
        {
            Log.d("func", "config load failed");
            return false;
        }
        list.newsList = (List<NewsList.NewsListItem>)object;
        Log.d("func", "newslist load finished");
        return true;
    }
    public void getSavedNewsList(NewsList list) // 获得已收藏新闻列表
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Details");
        int num = 0;
        list.newsList.clear();
        list.newsMap.clear();
        for(File file : dir.listFiles())
        {
            String id = file.getName();
            Log.d("func", "saved detail: " + id);
            DetailList detail = new DetailList(id);
            loadDetail(detail);
            // detail 转 NewsList.NewsListItem
            String str_num = String.valueOf(num);
            String title = detail.newsList.Title;
            String news_id = id;
            String url = detail.newsList.Picture.get(0);
            NewsList.NewsListItem item = new NewsList.NewsListItem(str_num, title, news_id, url);
            list.newsList.add(item);
            list.newsMap.put(item.id, item);
            num++;
        }
    }

    private byte[] readFile(String filename)
    {
        byte[] buffer = null;

        try {
            File dir = activity.getFilesDir();
            File sub_dir = new File(dir, filename);
            Log.d("func", sub_dir.toString());
            FileInputStream in = new FileInputStream(sub_dir);
            int length = in.available();
            buffer = new byte[length];
            in.read(buffer);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        Log.d("func", "read " + filename + " finished");
        return buffer;
    }
    private void writeFile(byte[] bytes, String filename)
    {
        try {
            File dir = activity.getFilesDir();
            File sub_dir = new File(dir, filename);
            Log.d("func", sub_dir.toString());
            FileOutputStream out = new FileOutputStream(sub_dir);
            out.write(bytes);
        }
        catch(Exception e){
            return;
        }
        Log.d("func", "write " + filename + " finished");
    }
}
