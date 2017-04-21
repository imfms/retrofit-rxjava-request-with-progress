package cn.f_ms.retrofituploadprogress;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.f_ms.retrofit2.adapter.rxjava2.upload.progress.ProgressBean;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private TextView mTvShow;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mTvShow = (TextView) findViewById(R.id.tv_show);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
    }

    private void request() {


        File file;

        try {
            String sourceDir = getPackageManager().getApplicationInfo(getPackageName(), 0).sourceDir;
            if (sourceDir != null) {
                file = new File(sourceDir);
            }
            else {
                showError();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError();
            return;
        }

        HttpManager.instance().uploadPic(file)
                .filter(new Predicate<ProgressBean<String>>() {
                    @Override
                    public boolean test(@NonNull ProgressBean<String> stringProgressBean) throws Exception {

                        mDialog.setMax((int) stringProgressBean.total);
                        mDialog.setProgress((int) stringProgressBean.progress);

                        return stringProgressBean.data != null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<ProgressBean<String>, String>() {
                    @Override
                    public String apply(@NonNull ProgressBean<String> stringProgressBean) throws Exception {
                        return stringProgressBean.data;
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDialog = new ProgressDialog(mContext);
                        mDialog.setCancelable(false);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        mDialog.show();
                    }

                    @Override
                    public void onNext(String result) {
                        mTvShow.setText(result);
                        Toast.makeText(mContext, "result:" + result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        Toast.makeText(mContext, "error:" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        dismissDialog();
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void dismissDialog() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void showError() {
        Toast.makeText(mContext, "获取APK信息失败(测试上传apk)", Toast.LENGTH_SHORT).show();
    }

}
