package cn.f_ms.retrofituploadprogress;


import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.f_ms.retrofit2.adapter.rxjava2.upload.progress.ProgressBean;
import cn.f_ms.retrofit2.adapter.rxjava2.upload.progress.RxJava2WithProgressCallAdapterFactory;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Http管理器
 * 管理所有Api的使用，对Api作简单的交互
 * Created by _Ms on 2017/4/14.
 */
public class HttpManager {

    private final Api mApi;

    private static class SingleInstance {
        private final static HttpManager INSTANCE = new HttpManager();
    }

    private HttpManager() {

        OkHttpClient.Builder okHttpClientBuild = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = okHttpClientBuild.build();

        mApi = new Retrofit.Builder()
                .baseUrl("http://image.baidu.com/")
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2WithProgressCallAdapterFactory.createAsync())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(FileConverterFactory.create())
                .build()
                .create(Api.class);

    }

    public static HttpManager instance() {
        return SingleInstance.INSTANCE;
    }


    public Observable<ProgressBean<String>> uploadPic(File image) {
        return mApi.uploadPic("upload", "upload_pc", "index", image);
    }
}
