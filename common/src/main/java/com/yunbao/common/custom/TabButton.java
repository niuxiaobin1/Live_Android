package com.yunbao.common.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunbao.common.R;

/**
 * Created by cxf on 2018/9/21.
 */

public class TabButton extends LinearLayout {

    private Context mContext;
    private float mScale;
    private String mTip;
    private int mIconSize;
    private int mTextSize;
    private int mTextColorChecked;
    private int mTextColorUnChecked;
    private boolean mChecked;
    private ImageView mImg;
    private TextView mText;
    private Drawable[] mDrawables;
    private int mDrawaleArrayLength;
    private ValueAnimator mAnimator;
    private int mDrawableIndex;

    public TabButton(Context context) {
        this(context, null);
    }

    public TabButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabButton);
        int iconArrayId = ta.getResourceId(R.styleable.TabButton_tbn_icon_array_id, 0);
        mTip = ta.getString(R.styleable.TabButton_tbn_tip);
        mIconSize = (int) ta.getDimension(R.styleable.TabButton_tbn_icon_size, 0);
        mTextSize = (int) ta.getDimension(R.styleable.TabButton_tbn_text_size, 0);
        mTextColorChecked = ta.getColor(R.styleable.TabButton_tbn_text_color_checked, 0);
        mTextColorUnChecked = ta.getColor(R.styleable.TabButton_tbn_text_color_unchecked, 0);
        mChecked = ta.getBoolean(R.styleable.TabButton_tbn_checked, false);
        ta.recycle();
        if (iconArrayId != 0) {
            TypedArray arr = getResources().obtainTypedArray(iconArrayId);
            int len = arr.length();
            int[] iconResArray = new int[len];
            for (int i = 0; i < len; i++) {
                iconResArray[i] = arr.getResourceId(i, 0);
            }
            arr.recycle();
            mDrawaleArrayLength = iconResArray.length;
            if (mDrawaleArrayLength > 0) {
                mDrawables = new Drawable[mDrawaleArrayLength];
                for (int i = 0; i < mDrawaleArrayLength; i++) {
                    mDrawables[i] = ContextCompat.getDrawable(context, iconResArray[i]);
                }
            }
        }
        mAnimator = ValueAnimator.ofFloat(1, mDrawaleArrayLength - 1);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                int index = (int) v;
                if (mDrawableIndex != index) {
                    mDrawableIndex = index;
                    if (mImg != null) {
                        mImg.setImageDrawable(mDrawables[index]);
                    }
                }
            }
        });
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        mImg = new ImageView(mContext);
        LayoutParams params1 = new LayoutParams(mIconSize, mIconSize);
        params1.setMargins(0, dp2px(4), 0, 0);
        mImg.setLayoutParams(params1);
        if (mDrawables != null && mDrawaleArrayLength > 0) {
            if (mChecked) {
                mImg.setImageDrawable(mDrawables[mDrawaleArrayLength - 1]);
            } else {
                mImg.setImageDrawable(mDrawables[0]);
            }
        }
        mText = new TextView(mContext);
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mText.setLayoutParams(params2);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mText.setText(mTip);
        mText.setTextColor(mChecked ? mTextColorChecked : mTextColorUnChecked);
        addView(mImg);
        addView(mText);
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        if (mChecked) {
            if (mText != null) {
                mText.setTextColor(mTextColorChecked);
            }
            if (mAnimator != null&& mDrawables != null && mDrawaleArrayLength > 0) {
                mAnimator.start();
            }
        } else {
            if (mAnimator != null) {
                mAnimator.cancel();
            }
            if (mImg != null && mDrawables != null && mDrawaleArrayLength > 0) {
                mImg.setImageDrawable(mDrawables[0]);
            }
            if (mText != null) {
                mText.setTextColor(mTextColorUnChecked);
            }
        }

    }

    private int dp2px(int dpVal) {
        return (int) (mScale * dpVal + 0.5f);
    }

    public void cancelAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator.removeAllUpdateListeners();
        }
    }

}
