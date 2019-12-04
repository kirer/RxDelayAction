package com.kirer.lib;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kirer.lib.exception.RxDelayException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public class DelayAction {

    private static final String TAG = "DelayAction";

    private List<Validator> mValidators;
    private Action mAction;
    private LifecycleProvider mLifecycleProvider;
    private final PublishSubject<Object> mBus;
    private Disposable mDispose;

    DelayAction() {
        mValidators = new ArrayList<>();
        mBus = PublishSubject.create();
    }

    public DelayAction addValidator(@NonNull Validator validator) {
        mValidators.add(validator);
        return this;
    }

    public DelayAction lifecycle(@NonNull LifecycleOwner lifecycleOwner) {
        mLifecycleProvider = new LifecycleProvider(lifecycleOwner);
        return this;
    }

    public DelayAction action(Action action){
        mAction = action;
        return this;
    }

    public void doCall() {
        if (null != mDispose && !mDispose.isDisposed()) { // 单例 再次触发，取消上次订阅 重新订阅
            mDispose.dispose();
        }
        Observable<Validator> observable = Observable.fromIterable(mValidators); // 创建拦截器数组的Observable
        if (null != mLifecycleProvider) {
            observable = observable.compose(mLifecycleProvider.<Validator>doOnLifecycle(Lifecycle.Event.ON_RESUME));
        }
        observable = observable
                .filter(new Predicate<Validator>() {
                    @Override
                    public boolean test(Validator validator) {
                        return !validator.check(); // 过滤出需要拦截的项
                    }
                })
                .take(1) // 只执行一个需要拦截的项
                .compose(waitValidator(mValidators.size()));
        if (null != mLifecycleProvider) {
            observable = observable.compose(autoDestroy());
        }
        mDispose = observable.subscribe(new Consumer<Validator>() {
            @Override
            public void accept(Validator v) {
                Log.d(TAG, "onNext Validator:: " + v.getClass());
                if (null == mAction) {
                    return;
                }
                mAction.onVerified(v);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.d(TAG, throwable.getMessage());
            }
        }, new io.reactivex.functions.Action() {
            @Override
            public void run() {
                if (null == mAction) {
                    return;
                }
                mAction.onCompleted();
            }
        });
    }

    void doContinue() {
        mBus.onNext(this);
    }

    public void dispose(){
        if (null != mDispose && !mDispose.isDisposed()) { // 单例 再次触发，取消上次订阅 重新订阅
            mDispose.dispose();
        }
    }

    private ObservableTransformer<Validator, Validator> waitValidator(final int size) {
        return new ObservableTransformer<Validator, Validator>() {
            @Override
            public ObservableSource<Validator> apply(Observable<Validator> upstream) {
                return upstream // 等待拦截器具体操作处理完通知，再继续执行
                        .delay(new Function<Validator, ObservableSource<DelayAction>>() {
                            @Override
                            public ObservableSource<DelayAction> apply(final Validator validator) throws Exception {
                                if (null == validator) {
                                    throw new RxDelayException("Validator event must not be null!");
                                }
                                validator.doValid(); // 拦截器拦截具体操作处理
                                Log.d(TAG, "doValid Validator:: " + validator.getClass());
                                return mBus.ofType(DelayAction.class);
                            }
                        })
                        // 重复 事件流，再次检查，取出一个需要拦截的项;
                        .repeat(size);
            }
        };
    }

    private ObservableTransformer<Validator, Validator> autoDestroy() {
        return new ObservableTransformer<Validator, Validator>() {
            @Override
            public ObservableSource<Validator> apply(Observable<Validator> upstream) { // 当页面关闭，抛出异常，解绑订阅
                return upstream.takeUntil(mLifecycleProvider.getSubject().filter(new Predicate<Lifecycle.Event>() {
                    @Override
                    public boolean test(Lifecycle.Event lifecycleEvent) throws Exception {
                        if (lifecycleEvent.equals(Lifecycle.Event.ON_DESTROY)) {
                            throw new RxDelayException("Validator destroy");
                        }
                        return false;
                    }
                }));
            }
        };
    }


}
