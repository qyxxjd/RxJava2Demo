

#### `RxJava 2.X` + `Retrofit` + `OkHttp` 简单示例：

1.声明接口
```java
    public interface FaceApi {

        @Multipart
        @POST("facepp/v3/compare")
        Observable<String> compare(@Part("api_key") RequestBody apiKey,
                                   @Part("api_secret") RequestBody apiSecret,
                                   @Part MultipartBody.Part... files);
    }
```

2.初始化Api
```java
    private void initApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .retryOnConnectionFailure(true)
                .connectTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .writeTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        mFaceApi = new Retrofit.Builder().baseUrl(PrivateConstant.FACE_URL_PREFIX)
                                         .client(okHttpClient)
                                         .addConverterFactory(GsonConverterFactory.create(gson))
                                         .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                         .build()
                                         .create(FaceApi.class);
    }
```

3.开始网络请求
```java
    /**
     * 测试人脸识别API
     *
     * 实际项目中：步骤1和3会在合适的地方进行统一处理，不需要每个接口都进行设置
     *
     * @param imagePath1 需要比对的照片1
     * @param imagePath2 需要比对的照片2
     * @return
     */
    private void testFaceApi(@NonNull String imagePath1, @NonNull String imagePath2) {
        //PrivateConstant里面声明的私有api_id,需要自己到官网申请
        mFaceApi.compare(convert(PrivateConstant.FACE_API_ID),
                         convert(PrivateConstant.FACE_API_SECRET),
                         convert("image_file1", new File(imagePath1)),
                         convert("image_file2", new File(imagePath2)))
                //1.线程切换的封装
                .compose(RxUtil.<String>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                //2.当前Activity onStop时自动取消请求
                .compose(this.<String>bindEvent(ActivityEvent.STOP))
                //3.原始数据转换为对象
                .map(DATA_PARSE_FUNCTION)
                .subscribeWith(new DisposableObserver<IdentifyResult>() {
                    @Override
                    public void onNext(IdentifyResult identifyResult) {
                        XLog.d("FaceApi --> " + identifyResult.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        XLog.e("FaceApi --> " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        XLog.d("FaceApi --> onComplete");
                    }
                });
    }
```


#### `RxJava 2.X` 简单示例：

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

`timer`操作符
```java
    Observable.timer(10, TimeUnit.MILLISECONDS)
              .compose(RxUtil.<Long>applySchedulers(RxUtil.COMPUTATION_TRANSFORMER))
              .subscribe(new Consumer<Long>() {
                  @Override
                  public void accept(Long aLong) throws Exception {
                      XLog.d("延迟10毫秒的任务启动");
                  }
              });
```

`interval`操作符
```java
    Observable.interval(1, TimeUnit.SECONDS)
              .compose(RxUtil.<Long>applySchedulers(RxUtil.COMPUTATION_TRANSFORMER))
              .subscribe(new Consumer<Long>() {
                  @Override
                  public void accept(Long aLong) throws Exception {
                      XLog.d("每隔1秒的定时任务启动");
                  }
              });
```

上面示例代码用到的变量
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
