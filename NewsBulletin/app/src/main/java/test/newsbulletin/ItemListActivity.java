package test.newsbulletin;

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
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
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

import test.newsbulletin.dummy.DummyContent;
import test.newsbulletin.model.Data;

import java.util.ArrayList;
import java.util.List;

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
    Data find_day = new Data();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        setupTabLayout(tabLayout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

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

        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.clearFocus();
    }
    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        mAdapter adapter = new mAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new ItemListFragment(), "Category 1");
        adapter.addFragment(new Fragment(), "Category 2");
        adapter.addFragment(new Fragment(), "Category 3");
        viewPager.setAdapter(adapter);
    }
    private void setupTabLayout(@NonNull TabLayout tabLayout) {
        Log.d("func", "set up tabhost");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(info);

        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
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


    public class mAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public mAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
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

}
