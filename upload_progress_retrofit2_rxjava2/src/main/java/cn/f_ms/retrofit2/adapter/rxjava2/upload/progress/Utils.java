package cn.f_ms.retrofit2.adapter.rxjava2.upload.progress;

import java.lang.reflect.Field;

import okhttp3.Request;
import retrofit2.Call;

/**
 * Some Utils
 */
public class Utils {

    public static void replaceToProgressRequestBody(Call<?> call, ProgressRequestBody.ProgressListener listener) {
        Request request = call.request();
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(request.body(), listener);

        try {
            Field requestBody = Request.class.getDeclaredField("body");
            requestBody.setAccessible(true);
            requestBody.set(request, progressRequestBody);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
