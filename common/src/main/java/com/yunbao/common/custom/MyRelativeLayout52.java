package com.yunbao.common.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by cxf on 2018/9/26.
 */

public class MyRelativeLayout52 extends RelativeLayout {

    public MyRelativeLayout52(Context context) {
        super(context);
    }

    public MyRelativeLayout52(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout52(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize*2, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
