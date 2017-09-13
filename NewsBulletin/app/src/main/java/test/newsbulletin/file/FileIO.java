package test.newsbulletin.file;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import test.newsbulletin.R;
import test.newsbulletin.model.Data;
import test.newsbulletin.model.DetailContent;
import test.newsbulletin.model.NewsList;

/**
 * Created by pushi on 2017/9/10.
 */

public class FileIO
{
    public static Application application;

    public FileIO() {}

    public void saveConfig() // 存储配置文件
    {
        File path = application.getFilesDir();
        File dir = new File(path, "Config");
        if(!dir.isDirectory()) {
            dir.mkdir();
        }
        String filename = "Config/main.config";
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream object_out = new ObjectOutputStream(byte_out);
            Data data = (Data) application;
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
            data = (Data) application;
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

    public boolean saveDetail(DetailContent list) // 收藏新闻时调用
    {
        File path = application.getFilesDir();
        File dir = new File(path, "Detail");
        if(!dir.isDirectory())
            dir.mkdir();

        String id = list.detailID;
        String filename = "Detail/" + id + ".detail";
        byte[] bytes = null;
        try {

            ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
            ObjectOutputStream object_out = new ObjectOutputStream(byte_out);
            object_out.writeObject(list.detailItem);
            list.bitmap.compress(Bitmap.CompressFormat.PNG, 100, byte_out);
            bytes = byte_out.toByteArray();
            byte_out.close();
            object_out.close();
        }
        catch(Exception e)
        {
            Log.d("func", "detail save failed");
            e.printStackTrace();
            return false;
        }
        writeFile(bytes, filename);
        Log.d("func", "detail save finished");
        return true;
    }

    public void eraseDetail(String newsId) // 取消收藏时调用
    {
        File path = application.getFilesDir();

        File dir = new File(path, "Detail/" + newsId + ".detail");
        if(dir.isFile())
            dir.delete();
    }

    public void eraseDetail(DetailContent list) // 取消收藏时调用
    {
        eraseDetail(list.detailID);
    }

    public boolean isDetailSaved(DetailContent list) // 是否收藏了某条新闻
    {
        String id = list.detailID;
        File path = application.getFilesDir();
        File dir = new File(path, "Detail/" + id + ".detail");
        return dir.isFile();
    }

    public boolean loadDetail(DetailContent list) // 当某条收藏新闻被加载时调用（离线时调用）
    {
        String id = list.detailID;
        String filename = "Detail/" + id + ".detail";
        byte[] bytes = readFile(filename);
        try {
            ByteArrayInputStream byte_in = new ByteArrayInputStream(bytes);
            ObjectInputStream object_in = new ObjectInputStream(byte_in);
            list.detailItem = (DetailContent.NewsDetailItem) object_in.readObject();
            list.bitmap = BitmapFactory.decodeStream(byte_in);
        }
        catch(Exception e)
        {
            Log.d("func", "detail load failed");
            return false;
        }
        Log.d("func", "detail load finished");
        return true;
    }
    public void saveNewsList(NewsList list) // 存储新闻列表
    {
        File path = application.getFilesDir();
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
        File path = application.getFilesDir();
        File dir = new File(path, "Detail");
        int num = 0;
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        Log.d("func", "file list"+dir.listFiles().length + " ");
        list.newsList.clear();
        list.newsMap.clear();
        for(File file : dir.listFiles())
        {
            String id = file.getName();
            id = id.substring(0, id.length() - 7);
            Log.d("func", "saved detail: " + id);
            DetailContent detail = new DetailContent(id);
            loadDetail(detail);
            // detail 转 NewsList.NewsListItem
            String str_num = String.valueOf(num);
            String title = detail.detailItem.Title;
            String news_id = id;
            String url = detail.detailItem.Picture.get(0);
            //TODO:暂时这么写？
            NewsList.NewsListItem item = new NewsList.NewsListItem(str_num, title, news_id, detail.detailItem.Picture);
            list.newsList.add(item);
            list.newsMap.put(item.id, item);
            num++;
        }
    }

    private byte[] readFile(String filename)
    {
        byte[] buffer = null;

        try {
            File dir = application.getFilesDir();
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
            File dir = application.getFilesDir();
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