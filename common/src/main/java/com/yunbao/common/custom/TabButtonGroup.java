package com.yunbao.common.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yunbao.common.utils.ToastUtil;

/**
 * Created by cxf on 2018/9/22.
 */

public class TabButtonGroup extends LinearLayout implements View.OnClickListener {

    private TabButton[] mTabButtons;
    private ViewPager mViewPager;
    private int mCurPosition;


    public TabButtonGroup(Context context) {
        this(context, null);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 0) {
            mTabButtons = new TabButton[childCount];
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                v.setTag(i);
                v.setOnClickListener(this);
                mTabButtons[i] = (TabButton) v;
            }
        }
    }


    public void setCurPosition(int position) {
        if (position == mCurPosition) {
            return;
        }
        if (position==2){
            ToastUtil.show("装修中，敬请期待...");
            return;
        }
        mTabButtons[mCurPosition].setChecked(false);
        mTabButtons[position].setChecked(true);
        mCurPosition = position;
        if (mViewPager != null) {
            mViewPager.setCurrentItem(position, false);
        }
    }


    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null) {
            setCurPosition((int) tag);
        }
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
    }

    public void cancelAnim() {
        if (mTabButtons != null) {
            for (TabButton tbn : mTabButtons) {
                if (tbn != null) {
                    tbn.cancelAnim();
                }
            }
        }
    }
}
