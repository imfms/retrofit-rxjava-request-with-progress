package cn.f_ms.retrofit2.adapter.rxjava2.upload.progress;

/**
 * ProgressBean
 * @param <T>    FinalData
 */
public class ProgressBean<T> {

    public ProgressBean() {}

    public ProgressBean(long total, long progress, T data) {
        this.total = total;
        this.progress = progress;
        this.data = data;
    }

    public long total;

    public long progress;

    public T data;

    @Override
    public String toString() {
        return "ProgressBean{" +
                "total=" + total +
                ", progress=" + progress +
                ", data=" + data +
                '}';
    }
}