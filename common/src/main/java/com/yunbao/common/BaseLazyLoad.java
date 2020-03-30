package com.yunbao.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.yunbao.common.interfaces.LifeCycleListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/19.
 */

public abstract class BaseLazyLoad extends Fragment {

    protected boolean isVisible = false;
    private boolean isInitView = false;
    private Dialog dialog;
    protected AppCompatActivity mContext;
    protected boolean isResume = false;
    protected boolean isShown = false;
    protected boolean isHide = false;
    private boolean vis;//当前是否显示底部播放条

    protected List<LifeCycleListener> mLifeCycleListeners;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isInitView = true;
        initViews();
        initValues();
        isCanLoadData();
    }

    protected void initViews() {

    }

    protected void initValues() {

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHide = hidden;
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                if (!hidden){
                    listener.onResume();
                }else{
                    listener.onPause();
                }

            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isShown = isVisibleToUser;
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见，获取该标志记录下来
        if (isVisibleToUser) {
            isVisible = true;
            isResume = true;
            isCanLoadData();
        } else {
            isVisible = false;
        }

        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                if (isVisibleToUser){
                    listener.onResume();
                }else{
                    listener.onPause();
                }

            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        mLifeCycleListeners = new ArrayList<>();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onCreate();
            }
        }
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStart();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStop();
            }
        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null && listener != null) {
            mLifeCycleListeners.add(listener);
        }
    }

    public void addAllLifeCycleListener(List<LifeCycleListener> listeners) {
        if (mLifeCycleListeners != null && listeners != null) {
            mLifeCycleListeners.addAll(listeners);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null) {
            mLifeCycleListeners.remove(listener);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = null;
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onDestroy();
            }
            mLifeCycleListeners.clear();
            mLifeCycleListeners = null;
        }
    }

    private void isCanLoadData() {
        //所以条件是view初始化完成并且对用户可见
        if (isInitView && isVisible) {
            lazyLoad();

            //防止重复加载数据
            isInitView = false;
            isVisible = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onPause();
            }
        }
    }

    /**
     * 加载要显示的数据 该方法如果fragment没有被销毁则只会执行一次
     */
    protected abstract void lazyLoad();


    /**
     * 暴露接口 子类实现
     *
     * @param alpha
     */
    public void changeHeadAlpha(int alpha) {

    }

    public void refresh() {

    }

    public void doSomeThing() {

    }

    public void onDraging() {

    }

    public void onDragingEnd() {
    }

}
