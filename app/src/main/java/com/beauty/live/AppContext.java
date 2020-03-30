package com.beauty.live;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.mob.MobSDK;
//import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.utils.L;
import com.yunbao.im.utils.ImMessageUtil;
import com.yunbao.im.utils.ImPushUtil;

import cn.tillusory.sdk.TiSDK;

import static com.yunbao.common.utils.L.sDeBug;


/**
 * Created by cxf on 2017/8/3.
 */

public class AppContext extends CommonAppContext {

    public static AppContext sInstance;
    private boolean mBeautyInited;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //腾讯云鉴权url
        String ugcLicenceUrl = "http://license.vod2.myqcloud.com/license/v1/c024af5988223f00b79514b1a5e4a0ae/TXUgcSDK.licence";
        //腾讯云鉴权key
        String ugcKey = "757d9ed808e1a34989c33c417aae7907";
        TXLiveBase.getInstance().setLicence(this, ugcLicenceUrl, ugcKey);
        L.setDeBug(BuildConfig.DEBUG);
        //初始化腾讯bugly
        CrashReport.initCrashReport(this);
        CrashReport.setAppVersion(this, CommonAppConfig.getInstance().getVersion());
        //初始化ShareSdk
        MobSDK.init(this);
        //初始化极光推送
        ImPushUtil.getInstance().init(this);
        //初始化极光IM
        ImMessageUtil.getInstance().init();

        //初始化 ARouter
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }
        sDeBug=true;
    }

    /**
     * 初始化萌颜
     */
    public void initBeautySdk(String beautyKey) {
        if(!TextUtils.isEmpty(beautyKey)){
            if (!mBeautyInited) {
                mBeautyInited = true;
                TiSDK.init(beautyKey, this);
                CommonAppConfig.getInstance().setTiBeautyEnable(true);
                L.e("萌颜初始化------->");
            }
        }else{
            CommonAppConfig.getInstance().setTiBeautyEnable(false);
        }

    }

}
