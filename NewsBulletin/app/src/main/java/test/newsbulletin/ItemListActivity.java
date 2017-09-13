package test.newsbulletin;

import android.app.ActivityManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.app.Application;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.widget.Toast;

import test.newsbulletin.dummy.DummyContent;
import test.newsbulletin.file.FileIO;
import test.newsbulletin.model.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity
{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private DrawerLayout mDrawerLayout;
    private SearchView mSearchView;
    private ViewPager viewPager;
    TabLayout tabLayout;
    private ArrayList<String> tabList = new ArrayList<>();
    private ArrayList<String> unusedTabList = new ArrayList<>();
    Data data;
    FileIO io;
    {
        Log.d("func","cons");
    }
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("func","main oncreate");
        io = new FileIO();
        boolean is_loaded = io.loadConfig();
        if(!is_loaded)
        {
            Log.d("func","first time loading config");
            //find_day.buildTabList();
        }
        setContentView(R.layout.main_activity);
        data = (Data) getApplication();
        data.setTabChanged(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.action_edit, R.string.action_edit);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        buildTabList();
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        setupTabLayout(tabLayout);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout.setupWithViewPager(viewPager);
        Context c;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                getDelegate().setLocalNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

                recreate();

            }
        });




        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        //搜索框
        mSearchView = (SearchView) findViewById(R.id.searchView);
        setupSearchView(mSearchView);

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d("func","resume");
        setupViewPager(viewPager);
    }

    @Override
    public void onDestroy()
    {
        io.saveConfig();
        Log.d("func", "destroy");
        super.onDestroy();
    }

    private void setupViewPager(ViewPager viewPager) {
        if (!data.tabChanged) {
            Log.d("debug","tab not change");
            return;
        }
        data.setTabChanged(false);
        final mAdapter adapter = new mAdapter(this.getSupportFragmentManager());

        Map<String, Fragment> map = data.savedFragments;
        for (String tab_name: tabList) {
            Log.d("func","saved f:" + map.size());

            if (map.containsKey(tab_name)) {
                adapter.addFragment(map.get(tab_name), tab_name);
            } else {
                Bundle bundle = new Bundle();
                ItemListFragment fragment = new ItemListFragment();
                bundle.putString("classTag",tab_name);
                fragment.setArguments(bundle);
                adapter.addFragment(fragment, tab_name);
                Log.d("func","add frag" + fragment);
                map.put(tab_name,fragment);
            }


        }

        viewPager.setAdapter(adapter);
    }

    private void setupSearchView(SearchView mSearchView) {
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.clearFocus();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("func", "submit text" + query);
                Intent intent = new Intent(ItemListActivity.this, SearchResultsActivity.class);
                intent.putExtra(SearchResultsActivity.QUERY_KEYWORD, query);
                startActivity(intent);

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.nav_tag:
                                Intent intent_tag = new Intent(mDrawerLayout.getContext(), DragTabActivity.class);
                                startActivity(intent_tag);
                                Log.d("func", "nav_tag" +
                                        "");
                                break;
                            case R.id.nav_collect:
                                Intent intent_collec = new Intent(mDrawerLayout.getContext(), CollectionActivity.class);
                                startActivity(intent_collec);
                                break;
                            case R.id.pic_yes:
                                data.if_pic = true;
                                Log.d("func", "nav_tag" +
                                        "");
                                break;
                            case R.id.pic_np:
                                data.if_pic = false;
                                break;
                            default:
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;

                    }
                });
    }
    private void setupTabLayout(@NonNull final TabLayout tabLayout) {
        Log.d("func", "set up tabhost" );
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("func", "tab Id: " + tab.getPosition());

                if (tab.getPosition() != 0) {
                }
            }

            @Override

            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override

            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("func", ""+item.getTitle());

        switch (item.getItemId()) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.nav_discussion:
                Log.d("func", "discuss");
                break;
            case R.id.nav_friends:
                Log.d("func", "friends");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


/*    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.v("NO","gotin");
        switch(item.getItemId())
        {
            case R.id.daylight:
                getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                Log.v("NO","gotin");
                break;
            case R.id.nightlight:
                getDelegate().setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                Log.v("NO","gotin");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }DrawerLayout Menu 的点击事件**/


    public class mAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public mAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public List<Fragment> getmFragments() { return mFragments; }
        @Override
        public Fragment getItem(int position) {
            Log.d("func", "getitem" + position + mFragmentTitles.get(position)+mFragmentTitles.toString());

            return mFragments.get(position);
        }


        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
    private void  buildTabList() {
        // ToDo(zps):if there is config file, read it.

        Data mAppData = (Data) getApplication();
        unusedTabList = mAppData.getUnusedTabList();
        tabList = mAppData.getTabList();
    }

}
