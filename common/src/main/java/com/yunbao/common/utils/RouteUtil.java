package com.yunbao.common.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.GoodsBean;

/**
 * Created by cxf on 2019/2/25.
 */

public class RouteUtil {
    //Intent隐式启动 action
    public static final String PATH_LAUNCHER = "/app/LauncherActivity";
    public static final String PATH_LOGIN_INVALID = "/main/LoginInvalidActivity";
    public static final String PATH_USER_HOME = "/main/UserHomeActivity";
    public static final String PATH_COIN = "/main/MyCoinActivity";
    public static final String PATH_GOODS = "/main/ShopGoodsActivity";


    /**
     * 启动页
     */
    public static void forwardLauncher(Context context) {
        ARouter.getInstance().build(PATH_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .navigation();
    }

    /**
     * 登录过期
     */
    public static void forwardLoginInvalid(String tip) {
        ARouter.getInstance().build(PATH_LOGIN_INVALID)
                .withString(Constants.TIP, tip)
                .navigation();
    }

    /**
     * 跳转到个人主页
     */
    public static void forwardUserHome(Context context, String toUid) {
        forwardUserHome(context, toUid, false,null);
    }

    /**
     * 跳转到个人主页
     */
    public static void forwardUserHome(Context context, String toUid, boolean fromLiveRoom,String fromLiveUid) {
        ARouter.getInstance().build(PATH_USER_HOME)
                .withString(Constants.TO_UID, toUid)
                .withBoolean(Constants.FROM_LIVE_ROOM, fromLiveRoom)
                .withString(Constants.LIVE_UID, fromLiveUid)
                .navigation();
    }

    /**
     * 跳转到充值页面
     */
    public static void forwardMyCoin(Context context) {
        ARouter.getInstance().build(PATH_COIN).navigation();
    }


    public static void forwardGoods(Context context,GoodsBean goodsBean,String storeId) {
        Postcard postcard= ARouter.getInstance().build(PATH_GOODS);
        postcard.
                withParcelable(Constants.GOODS,goodsBean);
        if(!TextUtils.isEmpty(storeId)){
            postcard.withString(Constants.UID,storeId);
        }
        postcard.navigation(context);
    }



}
