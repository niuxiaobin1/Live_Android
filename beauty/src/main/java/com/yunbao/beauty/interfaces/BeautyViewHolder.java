package com.yunbao.beauty.interfaces;

/**
 * Created by cxf on 2018/12/13.
 */

public interface BeautyViewHolder {

    void setEffectListener(BeautyEffectListener effectListener);

    void show();

    void hide();

    boolean isShowed();

    void release();

    void setVisibleListener(VisibleListener visibleListener);

    interface VisibleListener {
        void onVisibleChanged(boolean visible);
    }
}
