package com.yunbao.im.interfaces;

import android.view.ViewGroup;

/**
 * Created by cxf on 2018/11/8.
 */

public interface ChatRoomActionListener {
    void onCloseClick();

    void onPopupWindowChanged(int height);

    /**
     * 点击选择图片
     */
    void onChooseImageClick();

    /**
     * 点击拍照
     */
    void onCameraClick();

    /**
     * 点击语音输入
     */
    void onVoiceInputClick();

    /**
     * 点击位置
     */
    void onLocationClick();

    /**
     * 点击语音
     */
    void onVoiceClick();

    ViewGroup getImageParentView();

    //void onImageClick();
}
