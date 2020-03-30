package com.yunbao.common.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import static com.yunbao.common.Constants.MONEY_SIGN;

/**
 * Created by cxf on 2019/7/9.
 */

public class GoodsBean implements Parcelable {

    private String mId;
    private String mUid;
    private String mVideoId;
    private String mLink;
    private String mName;
    private String mPriceOrigin;
    private String mPriceNow;
    private String mDes;
    private String mThumb;
    private String mHits;
    private String mLocalPath;
    private boolean mAdded;
    private String addtime;
    private int issale;
    private int status;

    private int type;

    public GoodsBean() {

    }

    @JSONField(name = "href")
    public String getLink() {
        return mLink;
    }

    @JSONField(name = "href")
    public void setLink(String link) {
        mLink = link;
    }

    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    @JSONField(name = "old_price")
    public String getPriceOrigin() {
        return mPriceOrigin;
    }


    public String getHaveUnitPrice(){
        return appendMoneySign(mPriceNow);
    }


    public String getHaveUnitmOriginPrice(){
        return appendMoneySign(mPriceOrigin);
    }


    @JSONField(name = "old_price")
    public void setPriceOrigin(String priceOrigin) {
        mPriceOrigin = priceOrigin;
    }

    @JSONField(name = "price")
    public String getPriceNow() {
        return mPriceNow;
    }


    private String appendMoneySign(String price) {
        if(!TextUtils.isEmpty(price)&&!price.contains(MONEY_SIGN)){
            return MONEY_SIGN+price;
        }
        return price;
    }


    @JSONField(name = "price")
    public void setPriceNow(String priceNow) {
        mPriceNow = priceNow;
    }

    @JSONField(name = "des")
    public String getDes() {
        return mDes;
    }

    @JSONField(name = "des")
    public void setDes(String des) {
        mDes = des;
    }

    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    @JSONField(name = "id")
    public String getId() {
        return mId;
    }

    @JSONField(name = "id")
    public void setId(String id) {
        mId = id;
    }

    @JSONField(name = "uid")
    public String getUid() {
        if(mUid==null) return "";
        return mUid;
    }

    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
    }

    @JSONField(name = "videoid")
    public String getVideoId() {
        return mVideoId;
    }

    @JSONField(name = "videoid")
    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    @JSONField(name = "hits")
    public String getHits() {
        return mHits;
    }

    @JSONField(name = "hits")
    public void setHits(String hits) {
        mHits = hits;
    }

    @JSONField(serialize = false)
    public String getLocalPath() {
        return mLocalPath;
    }

    @JSONField(serialize = false)
    public void setLocalPath(String localPath) {
        mLocalPath = localPath;
    }

    @JSONField(serialize = false)
    public boolean isAdded() {
        return mAdded;
    }

    @JSONField(serialize = false)
    public void setAdded(boolean added) {
        mAdded = added;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mVideoId);
        dest.writeString(mLink);
        dest.writeString(mName);
        dest.writeString(mPriceOrigin);
        dest.writeString(mPriceNow);
        dest.writeString(mDes);
        dest.writeString(mThumb);
        dest.writeString(mHits);
        dest.writeString(mLocalPath);
        dest.writeString(addtime);
        dest.writeInt(issale);
        dest.writeInt(status);
        dest.writeInt(type);

    }


    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getIssale() {
        return issale;
    }

    public void setIssale(int issale) {
        this.issale = issale;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GoodsBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mVideoId = in.readString();
        mLink = in.readString();
        mName = in.readString();
        mPriceOrigin = in.readString();
        mPriceNow = in.readString();
        mDes = in.readString();
        mThumb = in.readString();
        mHits = in.readString();
        mLocalPath = in.readString();
        addtime = in.readString();
        issale=in.readInt();
        status=in.readInt();
        type=in.readInt();
    }


    public static final Creator<GoodsBean> CREATOR = new Creator<GoodsBean>() {
        @Override
        public GoodsBean createFromParcel(Parcel in) {
            return new GoodsBean(in);
        }

        @Override
        public GoodsBean[] newArray(int size) {
            return new GoodsBean[size];
        }
    };

}
