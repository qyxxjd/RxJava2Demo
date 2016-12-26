package com.classic.demo;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 文件描述: 北京旷视科技有限公司 人脸识别API {https://www.faceplusplus.com.cn/face/index.html}
 * 创 建 人: 续写经典
 * 创建时间: 2016/12/6 16:18
 */
public interface FaceApi {

    @Multipart
    @POST("facepp/v3/compare")
    Observable<String> compare(@Part("api_key") RequestBody apiKey,
                               @Part("api_secret") RequestBody apiSecret,
                               @Part MultipartBody.Part... files);
}
