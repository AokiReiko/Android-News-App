package test.newsbulletin.model;
import android.app.Application;

import java.util.ArrayList;

/**
 * Created by 32928 on 2017/9/9.
 */

public class Data extends Application {
    public static boolean if_pic = true;
    public static boolean which_inter = true;
    private ArrayList<String> tabList = new ArrayList<>();
    private ArrayList<String> unusedTabList = new ArrayList<>();

    public ArrayList<String> getTabList() { return tabList; }
    public ArrayList<String> getUnusedTabList() { return unusedTabList; }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        buildTabList();
    }
    private void  buildTabList() {
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
    }
}


