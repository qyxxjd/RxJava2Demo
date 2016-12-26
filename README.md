此`Demo`为*[BaseProject](https://github.com/qyxxjd/BaseProject)*的示例项目

#### RxJava 2.X使用示例：

声明一些示例代码需要用到的变量
```java
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
```

`create`操作符
```java
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
```

`fromArray`操作符
```java
    Observable.fromArray(1, 2, 3, 4, 5)
              //使用变换将线程控制的代码封装起来，使代码更简洁，也便于管理
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

`fromCallable`操作符
```java
    Observable.fromCallable(new Callable<Integer>() {
                  @Override public Integer call() throws Exception {
                      return 123;
                  }
              })
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

`fromIterable`操作符
```java
    ArrayList<Integer> list = new ArrayList<>();
    list.add(123);
    list.add(456);
    list.add(789);
    Observable.fromIterable(list)
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

`fromPublisher`操作符
```java
    Observable.fromPublisher(new Publisher<Integer>() {
                  @Override public void subscribe(Subscriber<? super Integer> s) {
                      s.onNext(6);
                      s.onNext(7);
                      s.onNext(8);
                      s.onNext(9);
                  }
              })
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

`just`操作符
```java
    Observable.just(1, 2, 3, 4, 5, 6)
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

`range`操作符
```java
    Observable.range(100, 60)
              .compose(RxUtil.<Integer>applySchedulers(RxUtil.IO_TRANSFORMER))
              .subscribeWith(DISPOSABLE_OBSERVER);
```

#### TODO 待更新


