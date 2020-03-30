package com.yunbao.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.L;

import java.lang.ref.WeakReference;

/**
 * Created by cxf on 2018/9/29.
 */

public abstract class AbsDialogFragment extends DialogFragment {

    protected Context mContext;
    protected View mRootView;
    private LifeCycleListener mLifeCycleListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = new WeakReference<>(getActivity()).get();
        mRootView = LayoutInflater.from(mContext).inflate(getLayoutId(), null);
        Dialog dialog = new Dialog(mContext, getDialogStyle());
        dialog.setContentView(mRootView);
        dialog.setCancelable(canCancel());
        dialog.setCanceledOnTouchOutside(canCancel());
        setWindowAttributes(dialog.getWindow());
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        L.e("当前的Dialog路径="+getClass().getName());
        super.onActivityCreated(savedInstanceState);
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onDialogFragmentShow(this);
        }
    }


    @Override
    public void onDestroy() {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onDialogFragmentHide(this);
        }
        mLifeCycleListener = null;
        super.onDestroy();
    }

    protected abstract int getLayoutId();

    protected abstract int getDialogStyle();

    protected abstract boolean canCancel();

    protected abstract void setWindowAttributes(Window window);


    protected  <T extends View> T findViewById(int id) {
        if (mRootView != null) {
            return mRootView.findViewById(id);
        }
        return null;
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }


    public void setLifeCycleListener(LifeCycleListener lifeCycleListener) {
        mLifeCycleListener = lifeCycleListener;
    }

    public interface LifeCycleListener {
        void onDialogFragmentShow(AbsDialogFragment fragment);

        void onDialogFragmentHide(AbsDialogFragment fragment);
    }

}
