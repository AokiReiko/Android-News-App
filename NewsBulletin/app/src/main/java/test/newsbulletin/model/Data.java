package test.newsbulletin.model;
import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;

import java.util.ArrayList;
import android.util.Log;
import test.newsbulletin.R;
import test.newsbulletin.file.FileIO;

/**
 * Created by 32928 on 2017/9/9.
 */

public class Data extends Application {
    public static boolean if_pic = true;
    public static boolean which_inter = true;
    public boolean if_day = true;
    // zps: 文件读写操作必须要在activity环境已知的时候才能进行，所以这里只能改成public了
    public ArrayList<String> tabList = new ArrayList<>();
    public ArrayList<String> unusedTabList = new ArrayList<>();


    public ArrayList<String> getTabList() { return tabList; }
    public ArrayList<String> getUnusedTabList() { return unusedTabList; }

    boolean isSpeechEnable = true;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        buildTabList();
        Log.v("checkit",unusedTabList.toString());
        if(isSpeechEnable) {
            SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        }
    }
    private void buildTabList() {
        // ToDo(zps):if there is config file, read it.
        tabList.add("最新");
        tabList.add("国内");
        tabList.add("科技");
        tabList.add("财经");
        tabList.add("娱乐");
        tabList.add("体育");

        unusedTabList.add("军事");
        unusedTabList.add("汽车");
        unusedTabList.add("国际");
        unusedTabList.add("社会");
        unusedTabList.add("文化");
        unusedTabList.add("教育");
        unusedTabList.add("健康");
        Log.d("list","build tab list");
    }

}


