package test.newsbulletin;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.logging.LogRecord;

import test.newsbulletin.file.FileIO;
import test.newsbulletin.model.NewsList;

import static java.security.AccessController.getContext;

public class CollectionActivity extends AppCompatActivity {


    static private int lastOffset = 0;
    static private int lastPosition = 0;
    View recyclerView;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    mAdapter.notifyDataSetChanged();
                    Log.d("func","handle"+mAdapter.getItemCount()+" "+recyclerView.toString());

                    break;
                default:;
            }
        }
    };
    CollectionActivity.SimpleItemRecyclerViewAdapter mAdapter;
    FileIO io;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        io = new FileIO();

        setContentView(R.layout.activity_collection_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.collect_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupHandler();


        mAdapter = new CollectionActivity.SimpleItemRecyclerViewAdapter(new NewsList());
        mAdapter.load();

        recyclerView = findViewById(R.id.collect_item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }



    private void setupHandler() {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        mAdapter.notifyDataSetChanged();
                        break;
                    default:;
                }
            }
        };
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int lastItem = 0;
            private int firstItem = 0;
            private int itemSize = 0;
            private boolean isLoad = false;


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                itemSize = recyclerView.getAdapter().getItemCount();recyclerView.getLayoutManager().getItemCount();
                lastItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                firstItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("func","scroll state changed");
                if (lastItem == recyclerView.getAdapter().getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    isLoad = true;
                    ((SearchResultsActivity.SimpleItemRecyclerViewAdapter)recyclerView.getAdapter()).loadMore();
                }
                // To update the pre-layout list.
                Log.d("search", "1:"+recyclerView);
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d("search", " 2:"+recyclerView);
                if(recyclerView.getLayoutManager() != null) {
                    Log.d("search", "3 " +
                            ":"+recyclerView);
                    getPositionAndOffset();
                }

            }
        });
        Log.d("func","set scroll listener");
    }
    private void scrollToPosition() {
        if(((RecyclerView)recyclerView).getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) ((RecyclerView)recyclerView).getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }
    private void getPositionAndOffset() {
        Log.d("search","get posi and off "+recyclerView);
        LinearLayoutManager layoutManager = (LinearLayoutManager) ((RecyclerView) recyclerView).getLayoutManager();
        //获取可视的第一个view
        Log.d("search", "get layout");
        int topIndex = layoutManager.findFirstVisibleItemPosition();
        View topView = layoutManager.findViewByPosition(topIndex);
        if (topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = topIndex;
        }
    }
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<CollectionActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final NewsList mList;

        public SimpleItemRecyclerViewAdapter(NewsList results) {
            //load more的连接请求由caller处理
            mList = results;
        }

        @Override
        public CollectionActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new CollectionActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CollectionActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mList.get(position);

            //holder.mIdView.setText(mnewsList.get(position).id);
            holder.mView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    menu.add(0, 0, 0, "删除").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            return false;
                        }
                    });
                }
            });
            if(!holder.mItem.picture_id.isEmpty())
            {
                Glide.with(getApplicationContext()).load(holder.mItem.picture_id.get(0)).placeholder(R.drawable.ic_launcher).into(holder.mImageView);
            }
            else holder.mImageView.setImageResource(R.drawable.ic_launcher);
            holder.mContentView.setText(mList.get(position).content);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.news_id);

                    context.startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public final ImageView mImageView;
            public NewsList.NewsListItem mItem;

            public ViewHolder(View view) {
                super(view);
                Log.d("func","view holder construct");
                mView = view;
                //mIdView = (TextView) view.findViewById(R.id.id);
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
        public void load() {
            //todo：是否合法的file
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (true)
                    {
                        io.getSavedNewsList(mList);
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                }
            });
            thread.start();
        }
    }

}