package com.yunbao.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.main.views.MainListViewHolder;
import com.yunbao.common.BaseLazyLoad;

public class MessageFragment extends BaseLazyLoad {

    private View rootView;
    private MainListViewHolder mListViewHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getLayoutInflater().inflate(R.layout.main_home_near_view, container, false);
        return rootView;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mListViewHolder = new MainListViewHolder(mContext, (ViewGroup) rootView.findViewById(R.id.parentView));
        mListViewHolder.addToParent();
        mListViewHolder.subscribeActivityLifeCycle();
        mListViewHolder.loadData();
    }

    @Override
    protected void lazyLoad() {

    }
}
