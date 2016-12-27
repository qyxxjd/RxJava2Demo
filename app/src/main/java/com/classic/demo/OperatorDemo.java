package com.classic.demo;

import android.os.Bundle;

import com.classic.android.BasicProject;
import com.classic.android.base.RxActivity;
import com.classic.android.rx.RxUtil;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 文件描述: RxJava2.X 使用示例
 * 创 建 人: 续写经典
 * 创建时间: 2016/12/6 16:18
 */
@SuppressWarnings("All")
public class OperatorDemo extends RxActivity {

    @Override public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        BasicProject.config(new BasicProject.Builder().setLog(BuildConfig.DEBUG ?
                                                              LogLevel.ALL : LogLevel.NONE));
        create();
    }

    /**
     * create 示例
     */
    private void create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
                      @Override public void subscribe(ObservableEmitter<Integer> emitter)
                              throws Exception {
                          for (int i = 0; i < 10; i++) {
                              emitter.onNext(i);
                          }
                          emitter.onComplete();
                      }
                  })
                  .subscribeOn(Schedulers.io())
                  .unsubscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  //这里只列举三种常见的使用方式
                  .subscribe(OBSERVER); //方式1
                  //.subscribeWith(DISPOSABLE_OBSERVER); //方式2
                  //.subscribe(NEXT_CONSUMER, ERROR_CONSUMER, COMPLETE); //方式3
    }

    /**
     * fromArray 示例
     */
    private Disposable fromArray() {
        return Observable.fromArray(1, 2, 3, 4, 5)
                         //使用变换将线程控制的代码封装起来，使代码更简洁，也便于管理
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * fromCallable 示例
     */
    private Disposable fromCallable() {
        return Observable.fromCallable(new Callable<Integer>() {
                             @Override public Integer call() throws Exception {
                                 return 123;
                             }
                         })
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * fromIterable 示例
     */
    private Disposable fromIterable() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(123);
        list.add(456);
        list.add(789);
        return Observable.fromIterable(list)
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * fromPublisher 示例
     */
    private Disposable fromPublisher() {
        return Observable.fromPublisher(new Publisher<Integer>() {
                             @Override public void subscribe(Subscriber<? super Integer> s) {
                                 s.onNext(6);
                                 s.onNext(7);
                                 s.onNext(8);
                                 s.onNext(9);
                             }
                         })
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * just 示例
     */
    private Disposable just() {
        return Observable.just(1, 2, 3, 4, 5, 6)
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * range 示例
     */
    private Disposable range() {
        return Observable.range(100, 60)
                         .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
                         .subscribeWith(DISPOSABLE_OBSERVER);
    }

    /**
     * time 示例
     */
    private Disposable time() {
        return Observable.timer(10, TimeUnit.MILLISECONDS)
                         .compose(RxUtil.<Long>applySchedulers(RxUtil.COMPUTATION_TRANSFORMER))
                         .subscribe(new Consumer<Long>() {
                             @Override
                             public void accept(Long aLong) throws Exception {
                                 XLog.d("延迟10毫秒的任务启动");
                             }
                         });
    }

    /**
     * interval 示例
     */
    private Disposable interval(long period, TimeUnit timeUnit, Consumer<Long> onNext) {
        return Observable.interval(1, TimeUnit.SECONDS)
                         .compose(RxUtil.<Long>applySchedulers(RxUtil.COMPUTATION_TRANSFORMER))
                         .subscribe(new Consumer<Long>() {
                             @Override
                             public void accept(Long aLong) throws Exception {
                                 XLog.d("每隔1秒的定时任务启动");
                             }
                         });
    }

    private static final Observer<Integer> OBSERVER = new Observer<Integer>() {
        @Override public void onSubscribe(Disposable d) {
            XLog.d("onSubscribe");
        }

        @Override public void onNext(Integer value) {
            XLog.d("onNext:" + value);
        }

        @Override public void onError(Throwable e) {
            XLog.e("onError:" + e.getMessage());
        }

        @Override public void onComplete() {
            XLog.d("onComplete");
        }
    };

    private static final Consumer<Integer> NEXT_CONSUMER = new Consumer<Integer>() {
        @Override public void accept(Integer integer) throws Exception {
            XLog.d("onNext:" + integer);
        }
    };

    private static final Consumer<Throwable> ERROR_CONSUMER = new Consumer<Throwable>() {
        @Override public void accept(Throwable throwable) throws Exception {
            XLog.e("onError:" + throwable.getMessage());
        }
    };

    private static final Action COMPLETE = new Action() {
        @Override public void run() throws Exception {
            XLog.d("onComplete");
        }
    };

    private static final DisposableObserver<Integer> DISPOSABLE_OBSERVER
            = new DisposableObserver<Integer>() {
        @Override public void onNext(Integer value) {
            XLog.d("onNext:" + value);
        }

        @Override public void onError(Throwable e) {
            XLog.e("onError:" + e.getMessage());
        }

        @Override public void onComplete() {
            XLog.d("onComplete");
        }
    };


}
