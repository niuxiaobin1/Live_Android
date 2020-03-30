package com.yunbao.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by cxf on 2019/4/11.
 */

public class CoinPayBean {
    private String mId;
    private String mName;
    private String mThumb;
    private String mHref;
    private boolean mChecked;

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "href")
    public String getHref() {
        return mHref;
    }

    @JSONField(name = "href")
    public void setHref(String href) {
        mHref = href;
    }

    @JSONField(serialize = false)
    public boolean isChecked() {
        return mChecked;
    }

    @JSONField(serialize = false)
    public void setChecked(boolean checked) {
        mChecked = checked;
    }
}
