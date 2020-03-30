package com.yunbao.im.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.utils.ScreenDimenUtil;
import com.yunbao.im.R;
import com.yunbao.im.adapter.ChatImagePreviewAdapter;
import com.yunbao.im.bean.ImChatImageBean;
import com.yunbao.im.bean.ImMessageBean;

import java.util.List;

/**
 * Created by cxf on 2018/11/28.
 */

public class ChatImageDialog extends AbsDialogFragment {

    private View mBg;
    private RecyclerView mRecyclerView;
    private ImageView mCover;
    private float mScale;
    private int mScreenWidth;
    private int mScreenHeight;
    private ValueAnimator mAnimator;
    private int mStartX;
    private int mStartY;
    private int mDistanceX;
    private int mDistanceY;
    private List<ImMessageBean> mList;
    private int mPosition;
    private int mImageWidth;
    private int mImageHeight;
    private Drawable mImageDrawable;


    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_image;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScreenDimenUtil util = ScreenDimenUtil.getInstance();
        mScreenWidth = util.getScreenWdith();
        mScreenHeight = util.getContentHeight();
        mBg = mRootView.findViewById(R.id.bg);
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mCover = (ImageView) mRootView.findViewById(R.id.cover);
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(300);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mCover.setTranslationX(mStartX + mDistanceX * v);
                mCover.setTranslationY(mStartY + mDistanceY * v);
                mCover.setScaleX(1 + (mScale - 1) * v);
                mCover.setScaleY(1 + (mScale - 1) * v);
                mBg.setAlpha(v);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRecyclerView != null && mList != null && mList.size() > 0) {
                    ChatImagePreviewAdapter adapter = new ChatImagePreviewAdapter(mContext, mList);
                    adapter.setActionListener(new ChatImagePreviewAdapter.ActionListener() {
                        @Override
                        public void onImageClick() {
                            dismiss();
                        }
                    });
                    mRecyclerView.setAdapter(adapter);
                    if (mPosition >= 0 && mPosition < mList.size()) {
                        mRecyclerView.scrollToPosition(mPosition);
                    }
                    if (mCover != null) {
                        mCover.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mCover != null) {
                                    mCover.setVisibility(View.INVISIBLE);
                                }
                            }
                        }, 300);
                    }
                }
            }
        });
        showImage();
    }

    public void setImageInfo(ImChatImageBean bean, int x, int y, int imageWidth, int imageHeight, Drawable drawable) {
        mList = bean.getList();
        mPosition = bean.getPosition();
        mStartX = x;
        mStartY = y;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        mImageDrawable = drawable;
    }


    public void showImage() {
        if (mCover == null || mImageWidth <= 0 || mImageHeight <= 0 || mImageDrawable == null) {
            return;
        }
        ViewGroup.LayoutParams params = mCover.getLayoutParams();
        params.width = mImageWidth;
        params.height = mImageHeight;
        mCover.requestLayout();
        mCover.setTranslationX(mStartX);
        mCover.setTranslationY(mStartY);
        mCover.setImageDrawable(mImageDrawable);
        mScale = mScreenWidth / ((float) mImageWidth);
        int targetX = mScreenWidth / 2 - mImageWidth / 2;
        int targetY = mScreenHeight / 2 - mImageHeight / 2;
        mDistanceX = targetX - mStartX;
        mDistanceY = targetY - mStartY;
        mAnimator.start();
    }


    @Override
    public void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        super.onDestroy();
    }


}
