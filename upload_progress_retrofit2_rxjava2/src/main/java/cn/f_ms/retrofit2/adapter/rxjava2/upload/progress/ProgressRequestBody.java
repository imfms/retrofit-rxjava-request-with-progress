package cn.f_ms.retrofit2.adapter.rxjava2.upload.progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Request With Progress RequestBody
 * RequestBody Wrapper
 */
public final class ProgressRequestBody extends RequestBody {

    /**
     * ProgressListener interface
     */
    public interface ProgressListener {
        void onProgress(long total, long progress);
    }

    /**
     * Wrapper RequestBody
     */
    private RequestBody mRequestBody;

    private ProgressListener mProgressListener;

    private BufferedSink mBufferedSink;

    public ProgressRequestBody(RequestBody requestBody) {
        this(requestBody, null);
    }

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        mRequestBody = requestBody;
        mProgressListener = progressListener;
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        if (mBufferedSink == null) {
            mBufferedSink = Okio.buffer(wrapperSink(sink));
        }

        mRequestBody.writeTo(mBufferedSink);
        mBufferedSink.flush();
    }


    private Sink wrapperSink(Sink sink) {
        return new ForwardingSink(sink) {

            long mWrited = 0;
            long mTotal = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (mTotal == 0) {
                    mTotal = contentLength();
                }

                mWrited += byteCount;
                if (mProgressListener != null) {
                    mProgressListener.onProgress(mTotal, mWrited);
                }
            }
        };
    }
}