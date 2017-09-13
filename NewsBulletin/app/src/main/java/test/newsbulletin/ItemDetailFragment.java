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
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
    private List<ImageView>  imageList = new ArrayList<ImageView>();
    private boolean check_out = true;
    private  boolean tmp_if = true;
    Data data;
    public static final String ARG_ITEM_ID = "item_id";
    List<Bitmap> bitmap = new ArrayList<Bitmap>();
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
    ImageView view_col ;
    public DetailContent mDetail;
    ImageView imageView_scroll;
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
                        tmp_if = false;
                        Log.d("func", "disconnect load: " + mDetail.detailItem.Picture);
                        if(mDetail.detailItem.Picture.size()!=0)
                        {
                            String str = mDetail.detailItem.Picture.get(0);
                            Log.v("layout",str+" 77");
                            if(str != "disconnect"&&str.indexOf("http")==-1)
                            {
                                check_out = false;
                                Resources res = getResources();
                                bitmap.add(BitmapFactory.decodeResource(res, R.drawable.ic_launcher));
                                Log.v("layout",bitmap.get(0)+" 98");
                            }
                            else if(str == "disconnect")
                            {

                                Resources res = getResources();

                                bitmap.add(BitmapFactory.decodeResource(res, R.drawable.disconnect)) ;
                                Log.v("layout",bitmap.get(0)+" 99");
                            }
                            else {

                                for(int i=1; i<=mDetail.detailItem.Picture.size(); i++)
                                {
                                    URL url = new URL(mDetail.detailItem.Picture.get(i-1));
                                    InputStream is = url.openStream();
                                    bitmap.add(BitmapFactory.decodeStream(is));
                                    Log.v("layout",bitmap.get(0)+" 88");
                                    is.close();
                                }
                            }
                        }
                        else
                        {
                            check_out = false;
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
                appBarLayout.setTitle(" ");
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.item_detail, container, false);


        // Show the dummy content as text in a TextView.

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
          // if (bitmap != null && !bitmap.isRecycled() && Data.if_pic) {
                Log.v("bitmap_in","bitmap_in");
          //      ((ImageView) rootView.findViewById(R.id.imageview_bg)).setImageBitmap(bitmap);
          //  }
            boolean if_pic = true;
            if(getActivity() != null)
            {
                data = (Data) getActivity().getApplication();
                if_pic = data.if_pic;
            }
            if (!bitmap.isEmpty()&&bitmap.size()!=0&&if_pic) {
                imageview = (ImageView)getActivity().findViewById(R.id.imageview_bg);
                Log.v("layout","bitmap_in"+bitmap.size());
                imageview.setImageResource(R.drawable.timg);
                for (int i = 1; i <= bitmap.size(); i++) {
                    ImageView imageView2 = new ImageView(getContext());
                    imageView2.setImageBitmap(bitmap.get(i - 1));
                    imageList.add(imageView2);

                }
            }
            PagerAdapter pagerAdapter = new PagerAdapter() {

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    // TODO Auto-generated method stub
                    return arg0 == arg1;
                }

                @Override
                public int getCount() {
                    // TODO Auto-generated method stub
                    return imageList.size();
                }

                @Override
                public void destroyItem(ViewGroup container, int position,
                                        Object object) {
                    // TODO Auto-generated method stub
                    container.removeView(imageList.get(position));
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    // TODO Auto-generated method stub
                    container.addView(imageList.get(position));


                    return imageList.get(position);
                }
            };

            ((ViewPager) rootView.findViewById(R.id.viewPager)).setAdapter(pagerAdapter);



        }

    }
}
