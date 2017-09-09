package test.newsbulletin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import test.newsbulletin.dummy.DummyContent;
import test.newsbulletin.model.NewsList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    static private int lastOffset = 0;
    static private int lastPosition = 0;
    View recyclerView;
    // TODO: Rename and change types of parameters



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("func", "oncreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("func", "oncreate view");
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.item_list, container, false);
        setupRecyclerView(rv);
        recyclerView = rv;
        return rv;
    }
    @Override
    public void onResume() {
        super.onResume();
        scrollToPosition();
    }

    private void scrollToPosition() {
        if(((RecyclerView)recyclerView).getLayoutManager() != null && lastPosition >= 0) {
            ((LinearLayoutManager) ((RecyclerView)recyclerView).getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
        }
    }
    private void getPositionAndOffset() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) ((RecyclerView)recyclerView).getLayoutManager();
        //获取可视的第一个view
        int topIndex = layoutManager.findFirstVisibleItemPosition();
        View topView = layoutManager.findViewByPosition(topIndex);
        if(topView != null) {
            //获取与该view的顶部的偏移量
            lastOffset = topView.getTop();
            //得到该View的数组位置
            lastPosition = topIndex;
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(new NewsList()));
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
                if (lastItem == recyclerView.getAdapter().getItemCount() - 1 && newState == RecyclerView.SCROLL_STATE_IDLE ) {
                    isLoad = true;
                    ((SimpleItemRecyclerViewAdapter)recyclerView.getAdapter()).loadMore();
                }
                // To update the pre-layout list.
                recyclerView.getAdapter().notifyDataSetChanged();
                if(recyclerView.getLayoutManager() != null) {
                    getPositionAndOffset();
                }

            }
        });
    }
    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final NewsList mnewsList;

        public SimpleItemRecyclerViewAdapter(NewsList newsList) {
            mnewsList = newsList;
        }

        public void loadMore() { mnewsList.loadMore(); }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mnewsList.get(position);
            //holder.mIdView.setText(mnewsList.get(position).id);
            holder.mImageView.setImageResource(R.drawable.ic_launcher);
            holder.mContentView.setText(mnewsList.get(position).content);
            //Log.d("func", holder.toString()+"-"+position + "-" + holder.mContentView.getText());
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
            return mnewsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            //public final TextView mIdView;
            public final TextView mContentView;
            public final ImageView mImageView;
            public NewsList.NewsListItem mItem;

            public ViewHolder(View view) {
                super(view);
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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
