package cn.f_ms.retrofituploadprogress;

import java.io.File;

import cn.f_ms.retrofit2.adapter.rxjava2.upload.progress.ProgressBean;
import io.reactivex.Observable;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by f-ms on 2017/4/21.
 */

public interface Api {

    @Multipart
    @POST("pictureup/uploadshitu")
    Observable<ProgressBean<String>> uploadPic(
            @Part("pos") String pos,
            @Part("uptype") String uptype,
            @Part("fm") String index,
            @Part("image\"; filename=\"xxx.jpg") File image
    );
}
