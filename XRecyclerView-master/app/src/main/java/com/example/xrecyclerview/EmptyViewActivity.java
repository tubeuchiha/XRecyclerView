package com.example.xrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yanxuwen.xrecyclerview.MyBaseAdapter;
import com.yanxuwen.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

public class EmptyViewActivity extends AppCompatActivity {
    private XRecyclerView mRecyclerView;
    private MyBaseAdapter mAdapter;
    private ArrayList<String> listData;
    private View mEmptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        mRecyclerView = (XRecyclerView)this.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mEmptyView = findViewById(R.id.text_empty);

        mRecyclerView.setEmptyView(mEmptyView);

        //没有数据，触发emptyView
        listData = new  ArrayList<String>();
        int headerViewsCount = mRecyclerView
                .getHeaderViewsCount();// 得到header的总数量
        mAdapter = new MyBaseAdapter(this,listData,headerViewsCount);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
