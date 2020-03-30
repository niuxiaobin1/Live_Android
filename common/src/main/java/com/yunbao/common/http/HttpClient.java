package com.yunbao.common.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.utils.L;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpClient {

    private static final int TIMEOUT = 10000;
    private static HttpClient sInstance;
    private OkHttpClient mOkHttpClient;
    private String mLanguage;//语言
    private String mUrl;

    private HttpClient() {
        mUrl = CommonAppConfig.HOST + "/api/public/?service=";
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(20000);
//        dispatcher.setMaxRequestsPerHost(10000);
//        builder.dispatcher(dispatcher);
        //输出HTTP请求 响应信息
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BASIC);
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Connection", "keep-alive")
                        .addHeader("referer", CommonAppConfig.HOST)
                        .build();
                return chain.proceed(request);
            }
        });
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(CommonAppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);

    }

    public GetRequest<JsonBean> get(String serviceName, String tag) {
        L.e("uid=="+CommonAppConfig.getInstance().getUid()+"token=="+CommonAppConfig.getInstance().getToken());
        return OkGo.<JsonBean>get(mUrl + serviceName)
                .tag(tag)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {
        return OkGo.<JsonBean>post(mUrl + serviceName)
                .tag(tag)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient, tag);
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

}
