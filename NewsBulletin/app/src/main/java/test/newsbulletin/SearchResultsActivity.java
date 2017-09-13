package test.newsbulletin;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.logging.LogRecord;

import test.newsbulletin.dummy.DummyContent;
import test.newsbulletin.model.NewsList;
import test.newsbulletin.model.SearchResults;

import static java.security.AccessController.getContext;

public class SearchResultsActivity extends AppCompatActivity {


    //只有从主页跳转过来的时候set一次，然后将这个传入SearchResults，再次在搜索页面搜索时不用此变量。
    public static final String QUERY_KEYWORD = "";
    static private int lastOffset = 0;
    static private int lastPosition = 0;
    View recyclerView;
    Handler mHandler;
    SearchResultsActivity.SimpleItemRecyclerViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String SearchContent = getIntent().getStringExtra(SearchResultsActivity.QUERY_KEYWORD);
        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupHandler();

        mAdapter = new SearchResultsActivity.SimpleItemRecyclerViewAdapter(new SearchResults(SearchContent));
        mAdapter.loadMore();

        recyclerView = findViewById(R.id.search_item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);


        final SearchView mSearchView = (SearchView) findViewById(R.id.search_searchView);
        setupSearchView(mSearchView);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("func", "submit text" + query);

                //mSearchView.setIconified(true);
                mAdapter.set(query);
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                mAdapter.loadMore();

                Log.d("func","text listener: "+((RecyclerView) recyclerView).getAdapter().toString());

                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollToPosition();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
        Log.d("func", "new intent");
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
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
    private void setupSearchView(SearchView mSearchView) {

        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.clearFocus();

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
            extends RecyclerView.Adapter<SearchResultsActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final SearchResults msearchResults;

        public SimpleItemRecyclerViewAdapter(SearchResults results) {
            //搜索跳转的时候搜索一次
            msearchResults = results;
            loadMore();
        }
        public void set(String key) { msearchResults.setKeyWord(key); }

        public void clear() { msearchResults.clear(); }

        public void loadMore() {
            // This is the caller for the url connection. New thread:
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (msearchResults.search() == true)
                    {
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                }
            });
            thread.start();
        }

        @Override
        public SearchResultsActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new SearchResultsActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SearchResultsActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = msearchResults.get(position);

            //holder.mIdView.setText(mnewsList.get(position).id);
            if(!holder.mItem.picture_id.isEmpty())
            {
                Glide.with(getApplicationContext()).load(holder.mItem.picture_id.get(0)).placeholder(R.drawable.ic_launcher).into(holder.mImageView);
            }
            else holder.mImageView.setImageResource(R.drawable.ic_launcher);
            holder.mContentView.setText(msearchResults.get(position).content);
            Log.d("func", holder.toString()+"-"+position + "-" + holder.mContentView.getText());
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
            return msearchResults.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public final ImageView mImageView;
            public SearchResults.SearchResultItem mItem;

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
    }

}