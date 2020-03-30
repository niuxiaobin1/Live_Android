package com.yunbao.common.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2018/10/27.
 * 获取键盘高度的工具类
 */

public class KeyBoardHeightUtil  implements ViewTreeObserver.OnGlobalLayoutListener {

    private final static String TAG = "KeyBoardHeightUtil";
    private View mParentView;
    private Rect mRect;
    private int mScreenHeight;
    private int mScreenStatusHeight;
    private int mLastRectHeight;
    private KeyBoardHeightChangeListener mKeyBoardChangeListener;
    private boolean mSoftInputShowed;
    private int mErrorHeight;
    private boolean mFirst = true;
    private int mLastKeyBoardHeight;

    public KeyBoardHeightUtil(Context context, View parentView, KeyBoardHeightChangeListener listener) {
        mParentView = parentView;
        mRect = new Rect();
        mFirst = true;
        ScreenDimenUtil util = ScreenDimenUtil.getInstance();
        mScreenHeight = util.getScreenHeight();
        mScreenStatusHeight = util.getStatusBarHeight();
        L.e(TAG, "---屏幕高度--->" + mScreenHeight);
        L.e(TAG, "---状态栏高度--->" + mScreenStatusHeight);
        mKeyBoardChangeListener = new WeakReference<>(listener).get();
    }


    @Override
    public void onGlobalLayout() {
        L.e(TAG, "-------onGlobalLayout--->");
        if (mParentView != null && mRect != null) {
            mParentView.getWindowVisibleDisplayFrame(mRect);
            int curRectHeight = mRect.height();
            if (mFirst) {
                mFirst = false;
                mScreenHeight = curRectHeight + mScreenStatusHeight;
                mLastRectHeight = curRectHeight;
            }
            if (mLastRectHeight != curRectHeight) {
                mLastRectHeight = curRectHeight;
                int visibleHeight = curRectHeight + mScreenStatusHeight;
                int keyboardHeight = mScreenHeight - visibleHeight;
                mSoftInputShowed = keyboardHeight > mScreenHeight / 4;
                if (mSoftInputShowed) {
                    keyboardHeight -= mErrorHeight;
                } else {
                    if (keyboardHeight > 0) {
                        mErrorHeight = keyboardHeight;
                    }else {
                        mErrorHeight = 0;
                    }
                    keyboardHeight = 0;
                }
                visibleHeight = mScreenHeight - keyboardHeight;
                if (mLastKeyBoardHeight != keyboardHeight) {
                    mLastKeyBoardHeight = keyboardHeight;
                    L.e(TAG, "-------可视区高度----->" + visibleHeight);
                    L.e(TAG, "-------键盘高度----->" + keyboardHeight);
                    if (mKeyBoardChangeListener != null) {
                        mKeyBoardChangeListener.onKeyBoardHeightChanged(visibleHeight, keyboardHeight);
                    }
                }
            }
        }
    }


    /**
     * 添加布局变化的监听器
     */
    public void start() {
        if (mParentView != null) {
            mParentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
            L.e(TAG, "-------添加键盘监听--->");
        }
    }


    /**
     * 移除布局变化的监听器
     */
    public void release() {
        if (mParentView != null) {
            mParentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        mParentView = null;
        mRect = null;
        mKeyBoardChangeListener = null;
        L.e(TAG, "-------移除键盘监听--->");
    }


    public boolean isSoftInputShowed() {
        return mSoftInputShowed;
    }
}
