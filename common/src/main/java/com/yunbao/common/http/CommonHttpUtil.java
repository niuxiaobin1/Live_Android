package com.yunbao.common.http;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.R;
import com.yunbao.common.activity.ErrorActivity;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.MD5Util;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by cxf on 2018/9/17.
 */

public class CommonHttpUtil {


    /**
     * 初始化
     */
    public static void init() {
        HttpClient.getInstance().init();
    }

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }

    /**
     * 使用腾讯定位sdk获取 位置信息
     *
     * @param lng 经度
     * @param lat 纬度
     * @param poi 是否要查询POI
     */
    public static void getAddressInfoByTxLocaitonSdk(final double lng, final double lat, final int poi, int pageIndex, String tag, final HttpCallback commonCallback) {
        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/geocoder/v1/?get_poi=" + poi + "&key=" + txMapAppKey + "&location=" + lat + "," + lng
                + "&poi_options=address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        OkGo.<String>get("https://apis.map.qq.com/ws/geocoder/v1/")
                .params("location", lat + "," + lng)
                .params("get_poi", poi)
                .params("poi_options", "address_format=short;radius=1000;page_size=20;page_index=" + pageIndex + ";policy=5")
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(tag)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj != null && commonCallback != null) {
                            commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("result")});
                        }
                    }


                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (commonCallback != null) {
                            commonCallback.onError();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (commonCallback != null) {
                            commonCallback.onFinish();
                        }
                    }
                });
    }

    /**
     * 使用腾讯地图API进行搜索
     *
     * @param lng 经度
     * @param lat 纬度
     */
    public static void searchAddressInfoByTxLocaitonSdk(final double lng, final double lat, String keyword, int pageIndex, final HttpCallback commonCallback) {

        String txMapAppKey = CommonAppConfig.getInstance().getTxMapAppKey();
        String s = "/ws/place/v1/search?boundary=nearby(" + lat + "," + lng + ",1000)&key=" + txMapAppKey + "&keyword=" + keyword + "&orderby=_distance&page_index=" + pageIndex +
                "&page_size=20" + CommonAppConfig.getInstance().getTxMapAppSecret();
        String sign = MD5Util.getMD5(s);
        OkGo.<String>get("https://apis.map.qq.com/ws/place/v1/search")
                .params("keyword", keyword)
                .params("boundary", "nearby(" + lat + "," + lng + ",1000)&orderby=_distance&page_size=20&page_index=" + pageIndex)
                .params("key", txMapAppKey)
                .params("sig", sign)
                .tag(CommonHttpConsts.GET_MAP_SEARCH)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        JSONObject obj = JSON.parseObject(response.body());
                        if (obj != null && commonCallback != null) {
                            commonCallback.onSuccess(obj.getIntValue("status"), "", new String[]{obj.getString("data")});
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        if (commonCallback != null) {
                            commonCallback.onError();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (commonCallback != null) {
                            commonCallback.onFinish();
                        }
                    }
                });
    }


    /**
     * 获取config
     */
    public static void getConfig(final CommonCallback<ConfigBean> commonCallback) {
        HttpClient.getInstance().get("Home.getConfig", CommonHttpConsts.GET_CONFIG)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            try {
                                JSONObject obj = JSON.parseObject(info[0]);
                                ConfigBean bean = JSON.toJavaObject(obj, ConfigBean.class);
                                CommonAppConfig.getInstance().setConfig(bean);
                                CommonAppConfig.getInstance().setLevel(obj.getString("level"));
                                CommonAppConfig.getInstance().setAnchorLevel(obj.getString("levelanchor"));
                                SpUtil.getInstance().setStringValue(SpUtil.CONFIG, info[0]);
                                if (commonCallback != null) {
                                    commonCallback.callback(bean);
                                }
                            } catch (Exception e) {
                                String error = "info[0]:" + info[0] + "\n\n\n" + "Exception:" + e.getClass() + "---message--->" + e.getMessage();
                                ErrorActivity.forward("GetConfig接口返回数据异常", error);
                            }
                        }
                    }

                    @Override
                    public void onError() {
                        if (commonCallback != null) {
                            commonCallback.callback(null);
                        }
                    }
                });
    }


    /**
     * QQ登录的时候 获取unionID 与PC端互通的时候用
     */
    public static void getQQLoginUnionID(String accessToken, final CommonCallback<String> commonCallback) {
        OkGo.<String>get("https://graph.qq.com/oauth2.0/me?access_token=" + accessToken + "&unionid=1")
                .tag(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (commonCallback != null) {
                            String data = response.body();
                            data = data.substring(data.indexOf("{"), data.lastIndexOf("}") + 1);
                            L.e("getQQLoginUnionID------>" + data);
                            JSONObject obj = JSON.parseObject(data);
                            commonCallback.callback(obj.getString("unionid"));
                        }
                    }
                });
    }


    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String touid, CommonCallback<Integer> callback) {
        setAttention(CommonHttpConsts.SET_ATTENTION, touid, callback);
    }

    /**
     * 关注别人 或 取消对别人的关注的接口
     */
    public static void setAttention(String tag, final String touid, final CommonCallback<Integer> callback) {
        if (touid.equals(CommonAppConfig.getInstance().getUid())) {
            ToastUtil.show(WordUtil.getString(R.string.cannot_follow_self));
            return;
        }
        HttpClient.getInstance().get("User.setAttent", tag)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("touid", touid)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            int isAttention = JSON.parseObject(info[0]).getIntValue("isattent");//1是 关注  0是未关注
                            EventBus.getDefault().post(new FollowEvent(touid, isAttention));
                            if (callback != null) {
                                callback.callback(isAttention);
                            }
                        }
                    }
                });
    }

    /**
     * 充值页面，我的钻石
     */
    public static void getBalance(HttpCallback callback) {
        HttpClient.getInstance().get("User.getBalance", CommonHttpConsts.GET_BALANCE)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("type", 0)
                .execute(callback);
    }

    /**
     * 用支付宝充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getAliOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_ALI_ORDER)
                .execute(callback);
    }

    /**
     * 用微信支付充值 的时候在服务端生成订单号
     *
     * @param callback
     */
    public static void getWxOrder(String parmas, HttpCallback callback) {
        HttpClient.getInstance().get(parmas, CommonHttpConsts.GET_WX_ORDER)
                .execute(callback);
    }


    /**
     * 检查token是否失效
     */
    public static void checkTokenInvalid() {
        HttpClient.getInstance().get("User.ifToken", CommonHttpConsts.CHECK_TOKEN_INVALID)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(NO_CALLBACK);
    }

    //不做任何操作的HttpCallback
    public static final HttpCallback NO_CALLBACK = new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };


    /**
     * 上传文件 获取七牛云token的接口
     */

    public static void getUploadQiNiuToken(HttpCallback callback) {
        HttpClient.getInstance().get("Video.getQiniuToken", CommonHttpConsts.GET_UPLOAD_QI_NIU_TOKEN)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .execute(callback);
    }





    /*用于获取商品列表*/
    public static void getGoodsList(int p,String keyword,HttpCallback callback) {
        HttpClient.getInstance().get("Shop.GetGoodsList", CommonHttpConsts.GET_GOODS_LIST)
                .params("uid", CommonAppConfig.getInstance().getUid())
                .params("token", CommonAppConfig.getInstance().getToken())
                .params("keyword", keyword)
                .params("p", p)
                .execute(callback);
    }

}




