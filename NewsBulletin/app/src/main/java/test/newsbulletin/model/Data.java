package test.newsbulletin.model;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.support.v4.app.Fragment;

import java.util.Map;

import android.util.Log;
import test.newsbulletin.R;
import test.newsbulletin.file.FileIO;

/**
 * Created by 32928 on 2017/9/9.
 */


public class Data extends Application {

    public static final String APP_ID = "wx37d748ca3aa35402";
    public static final String APP_SECRET = "5ff552fa0d93a1fd9e60913a1c737a2f";

    public IWXAPI api;//这个对象是专门用来向微信发送数据的一个重要接口,使用强引用持有,所有的信息发送都是基于这个对象的


    public  boolean if_pic = true;
    public static boolean which_inter = true;
    public boolean if_day = true;
    // zps: 文件读写操作必须要在activity环境已知的时候才能进行，所以这里只能改成public了
    public ArrayList<String> tabList = new ArrayList<>();
    public ArrayList<String> unusedTabList = new ArrayList<>();

    public ArrayList<String> getTabList() { return tabList; }
    public ArrayList<String> getUnusedTabList() { return unusedTabList; }
    public boolean tabChanged = true;

    public Map<String, Fragment> savedFragments = new HashMap<>();

    boolean isSpeechEnable = true;


    @Override
    public void onCreate() {

        // TODO Auto-generated method stub
        super.onCreate();
        FileIO.application = this;
        buildTabList();
        Log.v("checkit",unusedTabList.toString());
        if(isSpeechEnable) {
            SpeechUtility.createUtility(this, "appid=" + getString(R.string.app_id));
        }
    }
    public void registerWeChat(Context context) {   //向微信注册app
        api = WXAPIFactory.createWXAPI(context, APP_ID, true);
        api.registerApp(APP_ID);
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
    public void setTabChanged(boolean state) { tabChanged = state; }

}


