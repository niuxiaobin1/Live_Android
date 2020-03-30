package com.yunbao.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.common.BaseLazyLoad;
import com.yunbao.main.views.MainMeViewHolder2;

public class MineFragment extends BaseLazyLoad {

    private View rootView;
    private MainMeViewHolder2 mMeViewHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getLayoutInflater().inflate(R.layout.main_home_view, container, false);
        return rootView;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mMeViewHolder = new MainMeViewHolder2(mContext, (ViewGroup) rootView.findViewById(R.id.parentView));
        mMeViewHolder.addToParent();
        mMeViewHolder.subscribeActivityLifeCycle();
        mMeViewHolder.loadData();
    }

    @Override
    protected void lazyLoad() {

    }
}
