package com.kirer.lib;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;


import com.kirer.lib.exception.OutsideLifecycleException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public class LifecycleProvider implements LifecycleObserver {

    private final BehaviorSubject<Lifecycle.Event> subject = BehaviorSubject.create();

    public LifecycleProvider(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onEvent(@NonNull LifecycleOwner owner, Lifecycle.Event event) {
        subject.onNext(event);
    }

    /**
     * RxJava绑定生命周期，destroy时自动解绑
     */
    public <T> ObservableTransformer<T, T> bindToLifecycle() {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<Boolean> ob = Observable.combineLatest(
                        subject.share().take(1).map(LIFECYCLE),
                        subject.share().skip(1),
                        new BiFunction<Lifecycle.Event, Lifecycle.Event, Boolean>() {
                            @Override
                            public Boolean apply(Lifecycle.Event event, Lifecycle.Event event2) throws Exception {
                                return event.equals(event2);
                            }
                        }
                )
                        .onErrorReturn(RESUME_FUNCTION)
                        .filter(SHOULD_COMPLETE);
                return upstream.takeUntil(ob);
            }
        };
    }

    /**
     * 待OnResume时，进行接下流处理
     */
    public <T> ObservableTransformer<T, T> doOnLifecycle(final Lifecycle.Event lifeEvent) {
        return new ObservableTransformer<T, T>() {

            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                final Observable<Lifecycle.Event> ob = subject.filter(new Predicate<Lifecycle.Event>() {
                    @Override
                    public boolean test(Lifecycle.Event event) throws Exception {
                        return lifeEvent.equals(event);
                    }
                });
                return upstream.delay(new Function<T, ObservableSource<Lifecycle.Event>>() {
                    @Override
                    public ObservableSource<Lifecycle.Event> apply(T t) throws Exception {
                        return ob;
                    }
                });
            }
        };
    }

    private final Function<Lifecycle.Event, Lifecycle.Event> LIFECYCLE = new Function<Lifecycle.Event, Lifecycle.Event>() {
        @Override
        public Lifecycle.Event apply(Lifecycle.Event lastEvent) throws Exception {
            switch (lastEvent) {
                case ON_CREATE:
                    return Lifecycle.Event.ON_CREATE;
                case ON_START:
                    return Lifecycle.Event.ON_START;
                case ON_RESUME:
                    return Lifecycle.Event.ON_PAUSE;
                case ON_PAUSE:
                    return Lifecycle.Event.ON_STOP;
                case ON_STOP:
                    return Lifecycle.Event.ON_DESTROY;
                case ON_DESTROY:
                    throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                default:
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
            }
        }
    };
    private final Function<Throwable, Boolean> RESUME_FUNCTION = new Function<Throwable, Boolean>() {
        @Override
        public Boolean apply(Throwable throwable) throws Exception {
            if (throwable instanceof OutsideLifecycleException) {
                return true;
            }
            //noinspection ThrowableResultOfMethodCallIgnored
            Exceptions.propagate(throwable);
            return false;
        }
    };
    private final Predicate<Boolean> SHOULD_COMPLETE = new Predicate<Boolean>() {
        @Override
        public boolean test(Boolean shouldComplete) throws Exception {
            return shouldComplete;
        }
    };

    public BehaviorSubject<Lifecycle.Event> getSubject() {
        return subject;
    }
}
