package test.newsbulletin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Menu;
import android.support.v7.widget.ShareActionProvider;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.io.File;

import test.newsbulletin.model.Data;
import test.newsbulletin.model.DetailContent;
import test.newsbulletin.model.NewsList;
import test.newsbulletin.model.PictureParser;
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

                    /*DetailContent.NewsDetailItem item = fragment.mDetail.detailItem;
                    String str_read = item.Title + "。作者：" + item.Author + "。" + item.Content;
                    Log.d("speech","speak 1:" + str_read);

                    generator = new SpeechGenerator(str_read, this_activity);
                    Log.d("speech","speak 1.25:" + generator);
                    generator.start();
                    Log.d("speech","speak 1.5:" + str_read + this_activity);*/
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
            int id = menuItem.getItemId();

            if (id == android.R.id.home) {
                Log.d("func","click home!!");
                finish();
                return true;
            } else if (id == R.id.share_other){
                Log.d("func","click other!!");
                return true;
                //
            } else {
                DetailContent.NewsDetailItem item;
                if (fragment != null && fragment.mDetail != null){
                    item = fragment.mDetail.detailItem;
                } else return true;

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = item.news_url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = item.Title;
                msg.description = item.Content.substring(0,40);
                if (fragment.mDetail.bitmap != null) {
                    Bitmap bmp = PictureParser.imageZoom(fragment.mDetail.bitmap);
                    msg.setThumbImage(bmp);

                }
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = "newsBulletin";
                req.message = msg;
                switch (id) {
                    case R.id.share_wx_friend:
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                        break;
                    case R.id.share_wx_line:
                        req.scene = SendMessageToWX.Req.WXSceneTimeline;
                        break;
                    default:
                        req.scene = SendMessageToWX.Req.WXSceneSession;
                }
                Data d = (Data) getApplication();
                d.api.sendReq(req);



                return true;
            }
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
    private void setShareIntent(Intent shareIntent) {
        if (mShareUtil != null) {
            mShareUtil.setShareIntent(shareIntent);
        }
    }
}
