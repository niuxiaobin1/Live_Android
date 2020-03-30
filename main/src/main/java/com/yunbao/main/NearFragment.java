package com.yunbao.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.main.views.MainNearViewHolder;
import com.yunbao.common.BaseLazyLoad;
import com.yunbao.main.views.MainNearViewHolder2;

public class NearFragment extends BaseLazyLoad {

    private View rootView;
    private MainNearViewHolder2 mNearViewHolder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getLayoutInflater().inflate(R.layout.main_home_near_view2, container, false);
        return rootView;
    }

    @Override
    protected void initViews() {
        super.initViews();
        mNearViewHolder = new MainNearViewHolder2(mContext, (ViewGroup) rootView.findViewById(R.id.parentView));
        mNearViewHolder.addToParent();
        mNearViewHolder.subscribeActivityLifeCycle();
        mNearViewHolder.loadData();
    }

    @Override
    protected void lazyLoad() {

    }
}
