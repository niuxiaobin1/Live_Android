package com.yunbao.common.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.GoodsBean;

public class IntentHelper {
    public static void intentOutBrowser(Context context,String url){
        if(TextUtils.isEmpty(url))
            return;
        try {
            Intent intent= new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void intentWechatApi(Context context){
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

        }
    }


    public static void intentXCX(Context context,String path){

        IWXAPI api = WXAPIFactory.createWXAPI(context, CommonAppConfig.getxCxId(context));
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = CommonAppConfig.getOriginalXcxId(context); // 填小程序原始id
        req.path = path;                  ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);

    }

    public static void intentGoodsLinks(Context context, GoodsBean bean) {
        if(context==null||bean==null){
            return;
        }
        int type=bean.getType();
        if(type==1){
            intentXCX(context,bean.getLink());
        }else{
            intentOutBrowser(context,bean.getLink());
        }
    }




}
