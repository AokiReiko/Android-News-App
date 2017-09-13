package test.newsbulletin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Menu;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;

import test.newsbulletin.model.DetailContent;
import test.newsbulletin.speech.SpeechGenerator;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
    boolean isSpeechActive = false, isSpeechStart = false;
    SpeechGenerator generator = null;
    ItemDetailFragment fragment = null;
    private ShareActionProvider mShareUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Log.d("speech","fab");
        final Activity this_activity = this;


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSpeechActive)
                {
                    isSpeechActive = isSpeechStart = true;

                    DetailContent.NewsDetailItem item = fragment.mDetail.detailItem;
                    String str_read = item.Title + "。作者：" + item.Author + "。" + item.Content;
                    Log.d("speech","speak 1:" + str_read);

                    generator = new SpeechGenerator(str_read, this_activity);
                    Log.d("speech","speak 1.25:" + generator);
                    generator.start();
                    Log.d("speech","speak 1.5:" + str_read + this_activity);
                }
                else if(!isSpeechStart)
                {
                    isSpeechStart = true;
                    Log.d("speech","speak 2");
                    generator.resume();
                }
                else
                {
                    isSpeechStart = false;
                    Log.d("speech","speak 3");
                    generator.pause();
                }
            }
        });
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.share);
        mShareUtil = (ShareActionProvider) MenuItemCompat.getActionProvider((MenuItem) item);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");intent.getComponent();
        //intent.setComponent(new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TITLE,"Title");
        intent.putExtra(Intent.EXTRA_TEXT, "I have successfully share my message through my app");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        setShareIntent(intent);
        return true;
    }

    @Override
    public void onDestroy()
    {
        if(generator != null)
            generator.end();
        super.onDestroy();
    }
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
    @Override
  public boolean onMenuItemClick(MenuItem menuItem) {
    String msg = "";
    switch (menuItem.getItemId()) {

      case R.id.share:
        msg += "Click share";
        break;

    }
    return true;
  }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareUtil != null) {
            mShareUtil.setShareIntent(shareIntent);
        }
    }
}
