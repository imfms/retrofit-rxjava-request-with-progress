/*
 * Copyright (C) 2016 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.f_ms.retrofit2.adapter.rxjava2.upload.progress;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Response;

final class ResultWithProgressObservable<T> extends Observable<ProgressBean<Result<T>>> {
    private final Observable<ProgressBean<Response<T>>> upstream;

    ResultWithProgressObservable(Observable<ProgressBean<Response<T>>> upstream) {
        this.upstream = upstream;
    }

    @Override
    protected void subscribeActual(Observer<? super ProgressBean<Result<T>>> observer) {
        upstream.subscribe(new ResultObserver<>(observer));
    }

    private static class ResultObserver<R> implements Observer<ProgressBean<Response<R>>> {
        private final Observer<? super ProgressBean<Result<R>>> observer;

        ResultObserver(Observer<? super ProgressBean<Result<R>>> observer) {
            this.observer = observer;
        }

        @Override
        public void onSubscribe(Disposable disposable) {
            observer.onSubscribe(disposable);
        }

        @Override
        public void onNext(ProgressBean<Response<R>> progress) {

            Response<R> response = progress.data;

            if (response == null) {
                observer.onNext(
                        new ProgressBean<Result<R>>(progress.total, progress.progress, null)
                );
            } else {
                Result<R> resultResponse = Result.response(response);
                observer.onNext(
                        new ProgressBean<>(progress.total, progress.progress, resultResponse)
                );
            }
        }

        @Override
        public void onError(Throwable throwable) {
            try {
                Result<R> error = Result.error(throwable);
                observer.onNext(
                        new ProgressBean<>(-1, -1, error)
                );
            } catch (Throwable t) {
                try {
                    observer.onError(t);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.onError(new CompositeException(t, inner));
                }
                return;
            }
            observer.onComplete();
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
