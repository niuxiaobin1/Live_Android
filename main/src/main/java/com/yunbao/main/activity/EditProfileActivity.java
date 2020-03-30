package com.yunbao.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.UpdateFieldEvent;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.ActivityResultCallback;
import com.yunbao.common.interfaces.CommonCallback;
import com.yunbao.common.interfaces.ImageResultCallback;
import com.yunbao.common.utils.CityUtil;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.ProcessImageUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class EditProfileActivity extends AbsActivity {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mSign;
    private TextView mBirthday;
    private TextView mSex;
    private TextView mCity;
    private ProcessImageUtil mImageUtil;
    private UserBean mUserBean;
    private String mProvinceVal;
    private String mCityVal;
    private String mZoneVal;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mSign = (TextView) findViewById(R.id.sign);
        mBirthday = (TextView) findViewById(R.id.birthday);
        mSex = (TextView) findViewById(R.id.sex);
        mCity = (TextView) findViewById(R.id.city);

        mImageUtil = new ProcessImageUtil(this);
        mImageUtil.setImageResultCallback(new ImageResultCallback() {
            @Override
            public void beforeCamera() {

            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(mContext, file, mAvatar);
                    MainHttpUtil.updateAvatar(file, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0 && info.length > 0) {
                                ToastUtil.show(R.string.edit_profile_update_avatar_success);
                                UserBean bean = CommonAppConfig.getInstance().getUserBean();
                                if (bean != null) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    bean.setAvatar(obj.getString("avatar"));
                                    bean.setAvatarThumb(obj.getString("avatarThumb"));
                                }
                                EventBus.getDefault().post(new UpdateFieldEvent());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mUserBean = CommonAppConfig.getInstance().getUserBean();
        if (mUserBean != null) {
            showData(mUserBean);
        } else {
            MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    mUserBean = u;
                    showData(u);
                }
            });
        }
    }


    public void editProfileClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_avatar) {
            editAvatar();

        } else if (i == R.id.btn_name) {
            forwardName();

        } else if (i == R.id.btn_sign) {
            forwardSign();

        } else if (i == R.id.btn_birthday) {
            editBirthday();

        } else if (i == R.id.btn_sex) {
            forwardSex();

        } else if (i == R.id.btn_city) {
            chooseCity();
        }
    }

    private void editAvatar() {
        DialogUitl.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUitl.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    private void forwardName() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditNameActivity.class);
        intent.putExtra(Constants.NICK_NAME, mUserBean.getUserNiceName());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String name = intent.getStringExtra(Constants.NICK_NAME);
                    mUserBean.setUserNiceName(name);
                    mName.setText(name);
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }
        });
    }


    private void forwardSign() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSignActivity.class);
        intent.putExtra(Constants.SIGN, mUserBean.getSignature());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String sign = intent.getStringExtra(Constants.SIGN);
                    mUserBean.setSignature(sign);
                    mSign.setText(sign);
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }

        });
    }

    private void editBirthday() {
        if (mUserBean == null) {
            return;
        }
        DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(final String date) {
                MainHttpUtil.updateFields("{\"birthday\":\"" + date + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                mUserBean.setBirthday(date);
                                mBirthday.setText(date);
                                EventBus.getDefault().post(new UpdateFieldEvent());
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    private void forwardSex() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSexActivity.class);
        intent.putExtra(Constants.SEX, mUserBean.getSex());
        mImageUtil.startActivityForResult(intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    int sex = intent.getIntExtra(Constants.SEX, 0);
                    if (sex == 1) {
                        mSex.setText(R.string.sex_male);
                        mUserBean.setSex(sex);
                    } else if (sex == 2) {
                        mSex.setText(R.string.sex_female);
                        mUserBean.setSex(sex);
                    }
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }

        });
    }


    @Override
    protected void onDestroy() {
        if (mImageUtil != null) {
            mImageUtil.release();
        }
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_AVATAR);
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void showData(UserBean u) {
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        mSign.setText(u.getSignature());
        mBirthday.setText(u.getBirthday());
        mSex.setText(u.getSex() == 1 ? R.string.sex_male : R.string.sex_female);
        mCity.setText(u.getLocation());
    }


    /**
     * 选择城市
     */
    private void chooseCity() {
        ArrayList<Province> list = CityUtil.getInstance().getCityList();
        if (list == null || list.size() == 0) {
            final Dialog loading = DialogUitl.loadingDialog(mContext);
            loading.show();
            CityUtil.getInstance().getCityListFromAssets(new CommonCallback<ArrayList<Province>>() {
                @Override
                public void callback(ArrayList<Province> newList) {
                    loading.dismiss();
                    if (newList != null) {
                        showChooseCityDialog(newList);
                    }
                }
            });
        } else {
            showChooseCityDialog(list);
        }
    }

    /**
     * 选择城市
     */
    private void showChooseCityDialog(ArrayList<Province> list) {
        String province = mProvinceVal;
        String city = mCityVal;
        String district = mZoneVal;
        if (TextUtils.isEmpty(province)) {
            province = CommonAppConfig.getInstance().getProvince();
        }
        if (TextUtils.isEmpty(city)) {
            city = CommonAppConfig.getInstance().getCity();
        }
        if (TextUtils.isEmpty(district)) {
            district = CommonAppConfig.getInstance().getDistrict();
        }
        DialogUitl.showCityChooseDialog(this, list, province, city, district, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, final City city, County county) {
                String provinceName = province.getAreaName();
                String cityName = city.getAreaName();
                String zoneName = county.getAreaName();
                mProvinceVal = provinceName;
                mCityVal = cityName;
                mZoneVal = zoneName;
                final String location = StringUtil.contact(mProvinceVal, mCityVal, mZoneVal);
                if (mCity != null) {
                    mCity.setText(location);
                }

                MainHttpUtil.updateFields("{\"location\":\"" + location + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            if (info.length > 0) {
                                UserBean u = CommonAppConfig.getInstance().getUserBean();
                                if (u != null) {
                                    u.setLocation(location);
                                }
                                EventBus.getDefault().post(new UpdateFieldEvent());
                            }
                            ToastUtil.show(obj.getString("msg"));
                        }
                    }
                });


            }
        });
    }

}
