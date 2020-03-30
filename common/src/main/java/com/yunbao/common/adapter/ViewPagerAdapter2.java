package com.yunbao.common.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 */

public class ViewPagerAdapter2 extends FragmentPagerAdapter {

    private List<Fragment> mViewList;

    public ViewPagerAdapter2(FragmentManager fm, List<Fragment> list) {
        super(fm);
        mViewList = list;
    }


    @Override
    public Fragment getItem(int position) {
        return mViewList.get(position);
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }
}
