package com.example.xrecyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanxuwen.swipelibrary.SwipeLayout;
import com.yanxuwen.xrecyclerview.MyBaseAdapter;

import java.util.List;

import butterknife.Bind;

/**
 * 作者：严旭文 on 2016/5/11 14:36
 * 邮箱：420255048@qq.com
 */
public class MyChildAdapter extends MyBaseAdapter {
    private List<String> mDataSet;
    private Context mContext;

    public MyChildAdapter(Context context, List<String> dataSet, int headerViewsCount) {
        super(context, dataSet, headerViewsCount);
        this.mDataSet = dataSet;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        addSwipe(R.layout.swipe_default, SwipeLayout.ShowMode.LayDown, SwipeLayout.DragEdge.Right, true);
        addExpand(R.layout.expand_default);
        return new ViewHolder(setLayout(R.layout.item, parent));
    }

    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ViewHolder mViewHolder = (ViewHolder) holder;
        mViewHolder.text.setText(mDataSet.get(position));
        super.onBindViewHolder(holder, position);

    }

    class ViewHolder extends BaseViewHolder implements View.OnClickListener{
        private ViewHolder mViewHolder;
        @Bind(R.id.text)
        TextView text;
        @Bind(R.id.v_expand)
        View v_expand;
        @Bind(R.id.swipe_delete)
        ImageView swipeDelete;
        public ViewHolder(View itemView) {
            super(itemView);
            mViewHolder=this;
            setExpandView(v_expand);
            swipeDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.swipe_delete:
                    delete(mViewHolder.getAdapterPosition());
                    break;

            }
        }
    }

    public void remove(int position) {
        super.remove(position);
    }

    public void add(String text, int position) {
        mDataSet.add(position, text);
        super.add(position);
    }
}
