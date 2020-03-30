package com.yunbao.common.views;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yunbao.common.BaseLazyLoad;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.interfaces.LifeCycleListener;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;

/**
 * Created by cxf on 2018/9/22.
 */

public abstract class AbsViewHolder implements LifeCycleListener {

    private String mTag;
    protected Context mContext;
    protected ViewGroup mParentView;
    protected View mContentView;

    public AbsViewHolder(Context context, ViewGroup parentView) {
        L.e("当前的AbsViewHolder路径=" + getClass().getName());
        mTag = getClass().getSimpleName();
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);

        init();
    }

    public AbsViewHolder(Context context, ViewGroup parentView, Object... args) {
        L.e("当前的AbsViewHolder路径=" + getClass().getName());
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);
        init();
    }

    public AbsViewHolder(Fragment fragment, ViewGroup parentView, Object... args) {
        L.e("当前的AbsViewHolder路径=" + getClass().getName());
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = fragment.getActivity();
        mParentView = parentView;
        mContentView = LayoutInflater.from(mContext).inflate(getLayoutId(), mParentView, false);
    }

    protected void processArguments(Object... args) {

    }

    protected abstract int getLayoutId();

    public abstract void init();

    protected <T extends View> T findViewById(int res) {
        return mContentView.findViewById(res);
    }

    public View getContentView() {
        return mContentView;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }

    public void removeFromParent() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }

    /**
     * 订阅Activity的生命周期
     */
    public void subscribeActivityLifeCycle() {
        if (mContext instanceof AbsActivity) {
            ((AbsActivity) mContext).addLifeCycleListener(this);
        }
    }

    /**
     * 订阅Activity的生命周期
     */
    public void subscribeFragmentLifeCycle(Fragment frgament) {
        ((BaseLazyLoad) frgament).addLifeCycleListener(this);
    }

    /**
     * 取消订阅Activity的生命周期
     */
    public void unSubscribeActivityLifeCycle() {
        if (mContext instanceof AbsActivity) {
            ((AbsActivity) mContext).removeLifeCycleListener(this);
        }
    }


    public void finishAcitivty() {
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        L.e(mTag, "release-------->");
    }

    @Override
    public void onCreate() {
        L.e(mTag, "lifeCycle-----onCreate----->");
    }

    @Override
    public void onStart() {
        L.e(mTag, "lifeCycle-----onStart----->");
    }

    @Override
    public void onReStart() {
        L.e(mTag, "lifeCycle-----onReStart----->");
    }

    @Override
    public void onResume() {
        L.e(mTag, "lifeCycle-----onResume----->");
    }

    @Override
    public void onPause() {
        L.e(mTag, "lifeCycle-----onPause----->");
    }

    @Override
    public void onStop() {
        L.e(mTag, "lifeCycle-----onStop----->");
    }

    @Override
    public void onDestroy() {
        L.e(mTag, "lifeCycle-----onDestroy----->");
    }

}
