package com.yunbao.common.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.utils.ToastUtil;

/**
 * Created by cxf on 2018/9/29.
 * 处理 检查权限和 startActivityForResult 的回调的Fragment
 */

public class ProcessFragment extends Fragment {

    private Context mContext;
    private Runnable mPermissionCallback;
    private ActivityResultCallback mActivityResultCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    /**
     * 检查是否具有多个权限
     *
     * @param permissions
     * @return true 有权限 false无权限
     */
    private boolean checkPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (isAllGranted(permissions, grantResults)) {
            if (mPermissionCallback != null) {
                mPermissionCallback.run();
            }
        }
        mPermissionCallback = null;
    }


    /**
     * 判断申请的权限有没有被允许
     */
    private boolean isAllGranted(String[] permissions, int[] grantResults) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                showTip(permissions[i]);
                return false;
            }
        }
        return true;
    }

    /**
     * 拒绝某项权限时候的提示
     */
    private void showTip(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                ToastUtil.show(R.string.permission_storage_refused);
                break;
            case Manifest.permission.CAMERA:
                ToastUtil.show(R.string.permission_camera_refused);
                break;
            case Manifest.permission.RECORD_AUDIO:
                ToastUtil.show(R.string.permission_record_audio_refused);
                break;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                ToastUtil.show(R.string.permission_location_refused);
                CommonAppConfig.getInstance().clearLocationInfo();
                break;
            case Manifest.permission.READ_PHONE_STATE:
                ToastUtil.show(R.string.permission_read_phone_state_refused);
                break;
        }
    }

    public void requestPermissions(String[] permissions, Runnable runnable) {
        if (runnable != null) {
            if (checkPermissions(permissions)) {
                runnable.run();
            } else {
                mPermissionCallback = runnable;
                requestPermissions(permissions, 0);
            }
        }
    }

    public void startActivityForResult(Intent intent, ActivityResultCallback callback) {
        mActivityResultCallback = callback;
        super.startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mActivityResultCallback != null) {
            if (resultCode == -1) {//RESULT_OK
                mActivityResultCallback.onSuccess(data);
            } else {
                mActivityResultCallback.onFailure();
            }
        }
    }

    public void release(){
        mPermissionCallback=null;
        mActivityResultCallback=null;
    }
}
