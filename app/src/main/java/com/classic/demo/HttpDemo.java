package com.classic.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.classic.android.BasicProject;
import com.classic.android.base.RxActivity;
import com.classic.android.consts.MIME;
import com.classic.android.event.ActivityEvent;
import com.classic.android.rx.RxUtil;
import com.classic.android.utils.SDCardUtil;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings({"Convert2Lambda", "RedundantTypeArguments"})
public class HttpDemo extends RxActivity {

    private static final int CONNECT_TIMEOUT_TIME = 15;

    private FaceApi mFaceApi;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        BasicProject.config(new BasicProject.Builder().setLog(BuildConfig.DEBUG ?
                                                                      LogLevel.ALL : LogLevel.NONE));
        initApi();

        //身份证内置的照片
        final String img1 = SDCardUtil.getImageDirPath() + "/IDCardPhoto.jpg";
        //摄像头拍摄的照片
        final String img2 = SDCardUtil.getImageDirPath() + "/CurrentPhoto.jpg";

        testFaceApi(img1, img2);
    }

    /**
     * 测试人脸识别API
     * <p>
     * 实际项目中：步骤1和3会在合适的地方进行统一处理，不需要每个接口都进行设置
     *
     * @param imagePath1 需要比对的照片1
     * @param imagePath2 需要比对的照片2
     * @return
     */
    private void testFaceApi(@NonNull String imagePath1, @NonNull String imagePath2) {
        //PrivateConstant里面声明的私有api_id,需要自己到官网申请
        mFaceApi.compare(convert(PrivateConstant.FACE_API_ID),
                         convert(PrivateConstant.FACE_API_SECRET),
                         convert("image_file1", new File(imagePath1)),
                         convert("image_file2", new File(imagePath2)))
                //1.线程切换的封装
                .compose(RxUtil.<String>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                //2.当前Activity onStop时自动取消请求
                .compose(this.<String>bindEvent(ActivityEvent.STOP))
                //3.原始数据转换为对象
                .map(DATA_PARSE_FUNCTION)
                .subscribeWith(new DisposableObserver<IdentifyResult>() {
                    @Override
                    public void onNext(IdentifyResult identifyResult) {
                        XLog.d("FaceApi --> " + identifyResult.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        XLog.e("FaceApi --> " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        XLog.d("FaceApi --> onComplete");
                    }
                });
    }

    private static final Function<String, IdentifyResult> DATA_PARSE_FUNCTION =
            new Function<String, IdentifyResult>() {
                @Override
                public IdentifyResult apply(String s) throws Exception {
                    if (null != s) {
                        return new Gson().fromJson(s, new TypeToken<IdentifyResult>() {
                        }.getType());
                    }
                    return null;
                }
            };

    private MultipartBody.Part convert(@NonNull String key, @NonNull File file) {
        return MultipartBody.Part.createFormData(key, key,
                                                 RequestBody.create(MultipartBody.FORM, file));
    }

    private RequestBody convert(@NonNull String param) {
        return RequestBody.create(MediaType.parse(MIME.TEXT), param);
    }


    private void initApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        mFaceApi = new Retrofit.Builder().baseUrl(PrivateConstant.FACE_URL_PREFIX)
                                         .client(okHttpClient)
                                         .addConverterFactory(GsonConverterFactory.create(gson))
                                         .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                         .build()
                                         .create(FaceApi.class);
    }
}
