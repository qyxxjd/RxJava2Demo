package com.classic.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.classic.android.rx.RxUtil;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


@SuppressWarnings("All")
public class Demo extends Activity{

    private ViewController mViewController;
    private final String mPath = Environment.getExternalStorageDirectory() + "/images/";

    public interface ViewController {
        void addImage(Bitmap bitmap);
    }

    private void demo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final File[] rootFiles = new File(mPath).listFiles();
                for (File item : rootFiles) {
                    File[] subFiles = item.listFiles();
                    for (File subItem : subFiles) {
                        if(isImage(subItem)) {
                            final Bitmap bitmap = file2Bitmap(subItem);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mViewController.addImage(bitmap);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    private void demoRxJava() {
        Observable.fromArray(new File(mPath).listFiles())
                  .flatMap(new Function<File, ObservableSource<File>>() {
                      @Override
                      public ObservableSource<File> apply(File file) throws Exception {
                          return Observable.fromArray(file.listFiles());
                      }
                  })
                  .filter(new Predicate<File>() {
                      @Override
                      public boolean test(File file) throws Exception {
                          return isImage(file);
                      }
                  })
                  .map(new Function<File, Bitmap>() {
                      @Override
                      public Bitmap apply(File file) throws Exception {
                          return file2Bitmap(file);
                      }
                  })
                  .compose(RxUtil.<Bitmap>applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                  .subscribe(new Consumer<Bitmap>() {
                      @Override
                      public void accept(Bitmap bitmap) throws Exception {
                          mViewController.addImage(bitmap);
                      }
                  });

    }

    private void demoByLambda() {
        Observable.fromArray(new File(mPath).listFiles())
                  .flatMap((file) -> Observable.fromArray(file.listFiles()))
                  .filter(file -> isImage(file))
                  .map(file -> file2Bitmap(file))
                  .compose(RxUtil.applySchedulers(RxUtil.IO_ON_UI_TRANSFORMER))
                  .subscribe(bitmap -> mViewController.addImage(bitmap));
    }


    private boolean isImage(@NonNull File file) {
        return file.isFile() && (file.getName().toLowerCase().endsWith(".jpg") ||
                                 file.getName().toLowerCase().endsWith(".png"));
    }

    private Bitmap file2Bitmap(@NonNull File file) {
        return BitmapFactory.decodeFile(file.getPath());
    }

}
