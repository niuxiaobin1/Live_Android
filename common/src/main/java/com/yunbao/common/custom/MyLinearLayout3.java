package com.yunbao.common.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import com.yunbao.common.R;

/**
 * Created by cxf on 2018/12/3.
 */

public class MyLinearLayout3 extends LinearLayout {

    private int mScreenHeight;
    private float mHeightPercent;
    private int mHeight;

    public MyLinearLayout3(Context context) {
        this(context, null);
    }

    public MyLinearLayout3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyLinearLayout3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyLinearLayout3);
        mHeightPercent = ta.getFloat(R.styleable.MyLinearLayout3_mll_height_percent, 0);
        mHeight = (int) (mHeightPercent * mScreenHeight);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getHeight2() {
        return mHeight;
    }
}
