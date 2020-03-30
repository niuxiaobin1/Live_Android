package com.yunbao.video.upload;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.common.ServiceAddress;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.yunbao.common.bean.ConfigBean;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.utils.L;
import com.yunbao.video.http.VideoHttpConsts;
import com.yunbao.video.http.VideoHttpUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by cxf on 2018/5/21.
 * 视频上传 七牛云实现类
 */

public class VideoUploadQnImpl implements VideoUploadStrategy {

    private static final String TAG = "VideoUploadQnImpl";
    private VideoUploadBean mVideoUploadBean;
    private VideoUploadCallback mVideoUploadCallback;
    private String mToken;
    private UploadManager mUploadManager;
    private UpCompletionHandler mVideoUpCompletionHandler;//视频上传回调
    private UpCompletionHandler mImageUpCompletionHandler;//封面图片上传回调
    private String mQiNiuHost;


    public VideoUploadQnImpl(ConfigBean configBean) {
        mQiNiuHost = configBean.getVideoQiNiuHost();//服务器返回七牛云分配的地址拼接回调返回的路径
        mVideoUpCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (mVideoUploadBean == null) {
                    return;
                }
                String videoResultUrl = mQiNiuHost + mVideoUploadBean.getVideoFile().getName();
                L.e(TAG, "视频上传结果-------->" + videoResultUrl);
                mVideoUploadBean.setResultVideoUrl(videoResultUrl);
                uploadFile(mVideoUploadBean.getImageFile(), mImageUpCompletionHandler);
            }
        };
        mImageUpCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (mVideoUploadBean == null) {
                    return;
                }
                String imageResultUrl = mQiNiuHost + mVideoUploadBean.getImageFile().getName();
                L.e(TAG, "图片上传结果-------->" + imageResultUrl);
                mVideoUploadBean.setResultImageUrl(imageResultUrl);
                if (mVideoUploadCallback != null) {
                    mVideoUploadCallback.onSuccess(mVideoUploadBean);
                }
            }
        };
    }

    @Override
    public void upload(VideoUploadBean bean, VideoUploadCallback callback) {
        if (bean == null || callback == null) {
            return;
        }
        mVideoUploadBean = bean;
        mVideoUploadCallback = callback;

        //向内部服务器获取七牛云的token
        VideoHttpUtil.getQiNiuToken(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    if (info.length > 0) {
                        mToken = JSON.parseObject(info[0]).getString("token");
                        L.e(TAG, "-------上传的token------>" + mToken);
                        uploadFile(mVideoUploadBean.getVideoFile(), mVideoUpCompletionHandler);
                    }
                }
            }
        });
    }

    /**
     * 上传文件
     */
    private void uploadFile(File file, UpCompletionHandler handler) {
        if (TextUtils.isEmpty(mToken)) {
            return;
        }
        if (mUploadManager == null) {
//            Zone zone = new Zone(new ServiceAddress("upload-z2.qiniup.com"), new ServiceAddress("up-z2.qiniup.com"));
//            Configuration configuration = new Configuration.Builder().zone(zone).build();
            mUploadManager = new UploadManager();
        }
        mUploadManager.put(file, file.getName(), mToken, handler, null);
    }

    @Override
    public void cancel() {
        VideoHttpUtil.cancel(VideoHttpConsts.GET_QI_NIU_TOKEN);
        mVideoUploadCallback = null;
    }

}
