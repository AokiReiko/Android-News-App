package test.newsbulletin;

import android.app.Activity;
import android.content.res.Resources;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Application;

import test.newsbulletin.file.FileIO;
import test.newsbulletin.model.Data;
import test.newsbulletin.model.DetailContent;
import test.newsbulletin.speech.SpeechGenerator;

import android.util.Log;
/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.g
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting. */
    View rootView;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    setUI();

                    break;
                default:;
            }
        }
    };
    ImageView imageview;
    public DetailContent mDetail;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    FileIO io;
    Thread loadDetailThread, loadImageThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = this.getActivity();
        imageview=(ImageView)activity.findViewById(R.id.news_image);


        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mDetail = new DetailContent(getArguments().getString(ARG_ITEM_ID));

            loadDetail();

            // zps: 这里的多线程是否有问题？
            loadImageThread=new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        loadDetailThread.join();
                        if(mDetail.bitmap != null)
                            return;
                        Log.d("func", "disconnect load: " + mDetail.detailItem.Picture);
                        String str = mDetail.detailItem.Picture.get(0);
                        if(str == "disconnect")
                        {
                            Resources res = getResources();
                            mDetail.bitmap = BitmapFactory.decodeResource(res, R.drawable.disconnect);
                        }
                        else {
                            URL url = new URL(str);
                            InputStream is = url.openStream();
                            mDetail.bitmap = BitmapFactory.decodeStream(is);
                            is.close();
                        }
                        Message msg = new Message();
                        msg.what = 1;

                        mHandler.sendMessage(msg);

                    } catch (Exception e) {e.printStackTrace();}
                }
            });
            loadImageThread.start();

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("分类");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);


        // Show the dummy content as text in a TextView.
        try {
            loadImageThread.join();
        }
        catch(Exception e){}
        setUI();
        Log.v("layout","!!!");
        return rootView;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }
    private void loadDetail() {

        loadDetailThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (mDetail.loadMore() == true)
                {
                    Message msg = new Message();
                    msg.what = 1;

                    mHandler.sendMessage(msg);
                }

            }
        });
        loadDetailThread.start();
    }
    private void setUI() {
        if (mDetail.detailItem != null) {

            Log.d("debug", mDetail.toString());
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mDetail.detailItem.Content);
            ((TextView) rootView.findViewById(R.id.item_author)).setText(mDetail.detailItem.Author);
            ((TextView) rootView.findViewById(R.id.item_title)).setText(mDetail.detailItem.Title);
            int screenWidth = this.getActivity().getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.LayoutParams lp = ((ImageView) rootView.findViewById(R.id.news_image)).getLayoutParams();
            lp.width = screenWidth;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            ((ImageView) rootView.findViewById(R.id.news_image)).setLayoutParams(lp);
            ((ImageView) rootView.findViewById(R.id.news_image)).setMaxWidth(screenWidth);
            ((ImageView) rootView.findViewById(R.id.news_image)).setMaxHeight(screenWidth * 5);
            ((ImageView) rootView.findViewById(R.id.news_image)).setMinimumHeight(screenWidth * 0);
            if (mDetail.bitmap != null && !mDetail.bitmap.isRecycled() && Data.if_pic) {
                ((ImageView) rootView.findViewById(R.id.news_image)).setImageBitmap(mDetail.bitmap);
            }
        }
    }
}
