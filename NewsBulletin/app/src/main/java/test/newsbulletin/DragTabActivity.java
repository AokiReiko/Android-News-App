package test.newsbulletin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import test.newsbulletin.model.Data;
import z.sye.space.library.DragRecyclerView;
import z.sye.space.library.UnsignedRecyclerView;
import z.sye.space.library.interfaces.OnItemClickListener;
import z.sye.space.library.interfaces.OnItemRemovedListener;
import z.sye.space.library.interfaces.OnLongPressListener;

public class DragTabActivity extends Activity {

    private Button mQuitBtn;
    private UnsignedRecyclerView mUnsignedView;
    private DragRecyclerView mDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("func","drag create");

        setContentView(R.layout.drag_activity);
        Log.d("func","0");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Log.d("func","1");
        final List<String> dragList;
        Data mAppData = (Data) getApplication();
        //dragList = getIntent().getStringArrayListExtra("TAB_LIST");
        dragList = mAppData.getTabList();
        Log.d("list",dragList.toString());
        Toast.makeText(DragTabActivity.this,
                dragList.toString(), Toast.LENGTH_SHORT).show();

        final List<String> unsignedList;
        //unsignedList = getIntent().getStringArrayListExtra("UNUSED_TAB_LIST");
        unsignedList = mAppData.getUnusedTabList();
        Log.d("list",unsignedList.toString());


        mDragView = (DragRecyclerView) findViewById(R.id.dragView);
        Log.d("func","2");
        mDragView.datas(dragList)
                .onItemClick(new OnItemClickListener() {
                    @Override
                    public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                        Toast.makeText(DragTabActivity.this,
                                "position" + position + "has been clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .onLongPress(new OnLongPressListener() {
                    @Override
                    public void onLongPress() {
                        mQuitBtn.setVisibility(View.VISIBLE);
                    }
                })
                .onItemRemoved(new OnItemRemovedListener<String>() {
                    @Override
                    public void onItemRemoved(int position, String removedItem) {
                        Toast.makeText(DragTabActivity.this,
                                "position" + position + "has been removed", Toast.LENGTH_SHORT).show();
                        mUnsignedView.addItem(removedItem);
                        unsignedList.add(dragList.get(position));

                        dragList.remove(position);
                        Toast.makeText(DragTabActivity.this,
                                "draglist:"+dragList.hashCode(), Toast.LENGTH_SHORT).show();
                    }
                })
                .keepItemCount(2)
                .build();

        mQuitBtn = (Button) findViewById(R.id.btn_quit);
        mQuitBtn.setVisibility(View.GONE);
        Log.d("func","3");
        mQuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragView.quitLongPressMode();
                mQuitBtn.setVisibility(View.GONE);
            }
        });

        mUnsignedView = (UnsignedRecyclerView) findViewById(R.id.unsignedView);

        mUnsignedView.hanZiDatas(unsignedList)
                .onItemRemoved(new OnItemRemovedListener<String>() {
                    @Override
                    public void onItemRemoved(int position, String removedItem) {
                        mDragView.addItem(removedItem);
                        dragList.add(unsignedList.get(position));
                        unsignedList.remove(position);
                    }
                })
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drag_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}