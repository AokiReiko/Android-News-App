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
    public void saveConfig()
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Config");
        if(!dir.isDirectory()) {
            dir.mkdir();
        }

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
        }
        writeFile(bytes, "Config/main.config");
    }
    public boolean loadConfig()
    {
        byte[] bytes = readFile("Config/main.config");
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

    public void saveDetail(DetailList list, String id) // 新闻id
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "Details");
        if(!dir.isDirectory())
            dir.mkdir();

    }

    public DetailList loadDetail(String id)
    {
        return null;
    }
    public void saveNewsList(NewsList list)
    {
        File path = activity.getFilesDir();
        File dir = new File(path, "NewsList");
        if(!dir.isDirectory())
            dir.mkdir();
    }
    public NewsList loadNewsList()
    {
        return null;
    }
    public NewsList getSavedNewsList()
    {
        return null;
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
            buffer = null;
            Log.d("func", "load failed");
        }
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
            Log.d("func", "config save failed");
            return;
        }
        Log.d("func", "config save finished");
    }
}
