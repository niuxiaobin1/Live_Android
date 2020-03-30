package com.yunbao.common.upload;

import java.util.List;

/**
 * Created by cxf on 2019/4/16.
 */

public interface UploadCallback {
    void onFinish(List<UploadBean> list, boolean success);
}
