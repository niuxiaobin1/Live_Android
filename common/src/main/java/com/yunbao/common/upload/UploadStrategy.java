package com.yunbao.common.upload;

import java.util.List;

/**
 * Created by cxf on 2019/4/16.
 */

public interface UploadStrategy {

    /**
     * 执行上传
     *
     * @param list         被上传的文件列表
     * @param needCompress 是否需要压缩
     * @param callback     上传回调
     */
    void upload(List<UploadBean> list, boolean needCompress, UploadCallback callback);

    /**
     * 取消上传
     */
    void cancelUpload();
}
