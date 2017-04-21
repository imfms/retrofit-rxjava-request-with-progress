package cn.f_ms.retrofituploadprogress;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * MutilPart -> Part FileType-ConverterFactory
 * 由于OkHttpRequestBody单项不能包含文件名，故请求头中不会包含 ‘filename’
 */
public final class FileConverterFactory extends Converter.Factory {

    public static FileConverterFactory create() {
        return new FileConverterFactory();
    }

    private FileConverterFactory() {
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {

        if (type == File.class) {
            return new FileConvert();
        }

        return null;
    }

    public static class FileConvert implements Converter<File, RequestBody> {

        @Override
        public RequestBody convert(File value) throws IOException {

            MediaType mediaType = guessMimeType(value.getName());

            RequestBody fileBody = RequestBody.create(mediaType, value);

            return fileBody;
        }

        public static RequestBody file2RequestBody(File value) {

            MediaType mediaType = guessMimeType(value.getName());

            RequestBody fileBody = RequestBody.create(mediaType, value);

            return fileBody;
        }

        public static MediaType guessMimeType(String path) {
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            path = path.replace("#", "");   //解决文件名中含有#号异常的问题
            String contentType = fileNameMap.getContentTypeFor(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            return MediaType.parse(contentType);
        }

    }


}
