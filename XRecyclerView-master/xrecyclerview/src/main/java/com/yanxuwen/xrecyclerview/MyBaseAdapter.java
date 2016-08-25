package com.yanxuwen.xrecyclerview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yanxuwen.expandable.ExpandableLinearLayout;
import com.yanxuwen.xrecyclerview.animators.internal.ViewHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import com.yanxuwen.swipelibrary.SwipeLayout;

/**
 * Created by yanxuwen on 2016/05/4.
 */
public class MyBaseAdapter extends RecyclerView.Adapter<MyBaseAdapter.BaseViewHolder> {
    public static int NOOPENID=-1;
    private  Context mContext;
    private List<?> mDataSet;
    public BaseViewHolder swipe_holder;
    public BaseViewHolder expand_holder;
    /**
     * 头部数量
     */
    private int headerViewsCount;

    private int mDuration = 3000;//只适合透明度才3秒，如果是移动或者之类的，时间最好1秒之类
    private Interpolator mInterpolator = new LinearInterpolator();
    private int mLastPosition = -1;
    private boolean isFirstOnly = true;
    //下面为滑动菜单所用到的
    private int swipe_layout;//滑动菜单的试图
    public SwipeLayout.DragEdge mDragEdge= SwipeLayout.DragEdge.Right;
    public SwipeLayout.ShowMode mShowMode= SwipeLayout.ShowMode.LayDown;
    private boolean isBounces=false;
   //下面为展开列表所用到的
    private int expand_layout;
    public MyBaseAdapter(Context context, List<?> dataSet, int headerViewsCount) {
        mContext = context;
        mDataSet = dataSet;
        this.headerViewsCount = headerViewsCount;
    }

    /**
     * 设置item布局
     */
   public View setLayout(int layout,ViewGroup parent ){
       View v = LayoutInflater.from(mContext).inflate(layout, parent, false);
       ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.listview_base, parent, false);
       //添加滑动菜单
       ViewGroup layout_swipe=(ViewGroup)viewGroup.findViewById(R.id.layout_swipelayout);
       View view_swipe=null;
       if(swipe_layout!=0){
            view_swipe = (View) LayoutInflater.from(mContext).inflate(swipe_layout, parent, false);
       }else{
           view_swipe = (View) LayoutInflater.from(mContext).inflate(R.layout.swipe_default, parent, false);
       }
       layout_swipe.addView(view_swipe);
       layout_swipe.addView(v);
       //添加展开列表
       if(expand_layout!=0){
           RelativeLayout viewgroup_expand=(RelativeLayout)viewGroup.findViewById(R.id.layout_expandable);
           View view_expand = (ViewGroup) LayoutInflater.from(mContext).inflate(expand_layout, parent, false);
           viewgroup_expand.addView(view_expand);
       }
       return viewGroup;
   }
    /**
     * 添加可滑动菜单
     * swipe_layout 为滑动菜单布局
     * mode 拖动模式
     * bounces 是否开起弹簧
     */
    public void addSwipe(int swipe_layout,SwipeLayout.ShowMode mode,SwipeLayout.DragEdge DragEdge,boolean bounces){
        this.swipe_layout=swipe_layout;
        mShowMode=mode;
        mDragEdge=DragEdge;
        isBounces=bounces;

    }
    public void addExpand(int expand_layout){
        this.expand_layout=expand_layout;
    }




    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        if(holder.swipe!=null) {
            if(swipe_layout!=0) {
                holder.swipe.addSwipeListener(new SwipeLayout.SwipeListener() {

                    @Override
                    public void onClose(SwipeLayout swipeLayout) {
                    }

                    @Override
                    public void onUpdate(SwipeLayout swipeLayout, int i, int i1) {

                    }

                    @Override
                    public void onOpen(SwipeLayout swipeLayout) {
                        swipe_holder = holder;
                    }

                    @Override
                    public void onHandRelease(SwipeLayout swipeLayout, float v, float v1) {
                        swipe_holder = holder;
                    }
                });
            }
        }
        //显示动画
         int adapterPosition = holder.getAdapterPosition();
        if (!isFirstOnly || adapterPosition > mLastPosition) {
            for (Animator anim : getAnimators(holder.itemView)) {
                anim.setDuration(mDuration).start();
                anim.setInterpolator(new OvershootInterpolator(.5f));
            }
            mLastPosition = adapterPosition;
        } else {
            ViewHelper.clear(holder.itemView);
        }
    }

    /**
     * 刷新的时候记得重置
     */
    public void setFirstOnly(boolean firstOnly) {
        isFirstOnly = firstOnly;
        if (isFirstOnly) {
            mLastPosition = -1;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    /**
     * 注意：
     * 用于外部调用，则需要加上头部
     */
    public void remove( int position) {
        if(swipe_holder!=null) {
            swipe_holder.swipe.setIsSwipe(true);
            mDataSet.remove(position);
        }
        //移除的的时候一定要算上头部【由于RecyclerView是没有头部的，所以自定义的时候加上去的，，所以会导致notifyItemInserted失效少了2个】
        notifyItemRemoved(position + headerViewsCount);
    }
    /**
     * 注意：
     * 用于onBindViewHolder调用，防止错乱，传递为 holder.getAdapterPosition();
     */
    public void removeHolder(int adapterposition){
        if(swipe_holder!=null) {
            swipe_holder.swipe.setIsSwipe(true);
            swipe_holder = null;
        }
        mDataSet.remove(adapterposition-headerViewsCount);
        notifyItemRemoved(adapterposition);
    }

    /**
     * ，传递为 holder.getAdapterPosition();
     */
    public void delete(int adapterposition){
        removeHolder(adapterposition);
    }

    /**
     *  注意：
     * 用于外部调用，则需要加上头部
     */
    public void add(int position) {
        //添加的时候一定要算上头部【由于RecyclerView是没有头部的，所以自定义的时候加上去的，，所以会导致notifyItemInserted失效少了2个】
        notifyItemInserted(position + headerViewsCount);
//        notifyItemRangeChanged(position, getItemCount());
    }


    /**
     *注意：
     * 用于onBindViewHolder调用，防止错乱，传递为 holder.getAdapterPosition();
     */
    public void addHolder(int adapterposition){
        notifyItemInserted(adapterposition);
    }

    public  class BaseViewHolder extends RecyclerView.ViewHolder implements  View.OnFocusChangeListener{ //由于是lib工程，所以ButterKnife在这个类不管用
        SwipeLayout swipe;
        View expand;
        private ExpandableLinearLayout expandableLayout;
        public BaseViewHolder(final View itemView) {
            super(itemView);
            View clickview=itemView;
            ButterKnife.bind(this, itemView);
            swipe=(SwipeLayout)itemView.findViewById(R.id.layout_swipelayout);
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandable_layout);
            if(swipe!=null&&swipe_layout!=0) {
                swipe.setShowMode(mShowMode);
                swipe.setDragEdge(mDragEdge);
                swipe.setBounces(isBounces);
                clickview = swipe.getSurfaceView();
            }else{
                swipe.setIsSwipe(false);
            }
            clickview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
            clickview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(itemView, getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });
            clickview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    swipe.setIsSwipe(true);
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        boolean isreturn=false;
                        //关闭展开线
                        if(expand_holder!=null){
                            expand_holder. expandableLayout.collapse();
                            expand_holder. expand.clearFocus();
                            expand_holder=null;
                            isreturn=true;
                        }

                        //关闭滑动菜单
                        if (swipe_holder != null)
                            swipe_holder.swipe.setIsSwipe(true);
                        if (swipe_holder != null) {
                            View view = swipe_holder.swipe;
                            SwipeLayout mTouchView = null;
                            if (view instanceof SwipeLayout) {
                                mTouchView = (SwipeLayout) view;
                            }
                            if (mTouchView != null && mTouchView.getOpenStatus() != SwipeLayout.Status.Close) {
                                if (swipe_holder != null)
                                    swipe.setIsSwipe(false);
                                swipe_holder.swipe.setIsSwipe(false);
                                mTouchView.close();
                                mTouchView = null;
                                isreturn=true;
                            }
                        }
                        return isreturn;
                    } else {
                        return false;
                    }
                }
            });
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (expandableLayout != null) {
                if (hasFocus) {
                    expand_holder=this;
                    expandableLayout.expand();
                } else {
                    expandableLayout.collapse();
                }
            }
        }

        /**
         *设置控件展开按钮
         */
        public void setExpandView(View v){
            expand=v;
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(expand!=null) {
                        if (expand.isFocused()) {
                            expand.clearFocus();
                        } else {
                            expand.requestFocus();
                        }
                    }
                }
            });
            expand.setFocusableInTouchMode(true);
            expand.setOnFocusChangeListener(this);
        }
    }

    public void setDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public Animator[] getAnimators(View view) {
        return new Animator[]{
//                ObjectAnimator.ofFloat(view, "translationX", -view.getRootView().getWidth(), 0)
                ObjectAnimator.ofFloat(view, "alpha", 0, 1f)
        };
    }
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this RecyclerView.Adapter has
         * been clicked.
         * <p>
         * Implementers can call getPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param view The view within the RecyclerView.Adapter that was clicked (this
         *            will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int position);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
    }

    public static OnItemClickListener mOnItemClickListener = null;
    public static OnItemLongClickListener mOnItemLongClickListener = null;

    /**
     * Register a callback to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }
}

