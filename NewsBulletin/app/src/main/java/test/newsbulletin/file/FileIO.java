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
        try {
            FileOutputStream file_out = new FileOutputStream(filename);
            ObjectOutputStream object_out = new ObjectOutputStream(file_out);
            Data data = (Data) activity.getApplication();
            object_out.writeObject(data.tabList);
            object_out.writeObject(data.unusedTabList);
            file_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "config save failed");
            return;
        }
        Log.d("func", "config save finished");
    }
    public boolean loadConfig() // 读取配置文件
    {
        String filename = "Config/main.config";

        Object object_1, object_2;
        Data data;
        try {
            FileInputStream file_in = new FileInputStream(filename);
            ObjectInputStream object_in = new ObjectInputStream(file_in);
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
        File dir = new File(path, "Details");
        if(!dir.isDirectory())
            dir.mkdir();

        String id = list.pageID;
        String filename = "Detail/" + id;
        try {
            FileOutputStream file_out = new FileOutputStream(filename);
            ObjectOutputStream object_out = new ObjectOutputStream(file_out);
            object_out.writeObject(list.newsList);
            file_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "detail save failed");
            return;
        }
        Log.d("func", "detail save finished");
    }

    public void eraseDetail(DetailList list) // 取消收藏时调用
    {
        String id = list.pageID;
        File path = activity.getFilesDir();
        File dir = new File(path, "Details/" + id);
        if(dir.isFile())
            dir.delete();
    }

    public boolean loadDetail(DetailList list) // 当某条收藏新闻被加载时调用（离线时调用）
    {
        String id = list.pageID;
        String filename = "Detail/" + id;

        Object object;
        try {
            FileInputStream file_in = new FileInputStream(filename);
            ObjectInputStream object_in = new ObjectInputStream(file_in);
            object = (Object) object_in.readObject();
        }
        catch(Exception e)
        {
            Log.d("func", "detail load failed");
            return false;
        }
        list.newsList = (DetailList.NewsListItem)object;
        return true;
    }
    public void saveNewsList(NewsList list) // 存储新闻列表
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "NewsList");
        if(!dir.isDirectory())
            dir.mkdir();

        String filename = "NewsList" + list.classTag;
        try {
            FileOutputStream file_out = new FileOutputStream(filename);
            ObjectOutputStream object_out = new ObjectOutputStream(file_out);
            object_out.writeObject(list.newsList);
            file_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "newslist save failed");
            return;
        }
        Log.d("func", "newslist save finished");
    }
    public boolean loadNewsList(NewsList list) // 需要设置好classTag, 之后读取该类别的新闻列表（离线时调用，若classTag为收藏则一定调用）
    {
        String catagory = list.classTag;
        String filename = "NewsList/" + catagory;
        if(catagory == "收藏")
            getSavedNewsList(list);

        Object object;
        try {
            FileInputStream file_in = new FileInputStream(filename);
            ObjectInputStream object_in = new ObjectInputStream(file_in);
            object = (Object) object_in.readObject();
        }
        catch(Exception e)
        {
            Log.d("func", "config load failed");
            return false;
        }
        list.newsList = (List<NewsList.NewsListItem>)object;
        return true;
    }
    private void getSavedNewsList(NewsList list) // 类别标签为"收藏"，获得已收藏新闻列表
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Details");
        int num = 0;
        for(File file : dir.listFiles())
        {
            String id = file.getName();
            Log.d("func", "saved detail: " + id);
            DetailList detail = new DetailList(id);
            // detail 转 NewsList.NewsListItem
            // String id = String.valueOf(num);

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
