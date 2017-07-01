package com.rx_java2_demo2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author: Matthew
 * Description:
 *   RxJava有强大的线程控制.
 *   主线程中去创建一个上游Observable 来发送事件, 则这个上游默认就在主线程发送事件.
 *
 * */

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Matthew -- > DEBUG : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Thread  :" + Thread.currentThread().getName());
    }

    public void clickView(View view) {
 /*       Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.i(TAG, "Observable thread is :" + Thread.currentThread().getName());
                Log.i(TAG, "emit  1");
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };

        observable.subscribe(consumer);*/
        //通过Rxjava内置线程调度
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.i(TAG, "Observable thread is : " + Thread.currentThread());
                Log.i(TAG, "emit 1");
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.i(TAG, "Observer accepted thread is : " + Thread.currentThread().getName());
                Log.i(TAG, "onNext: " + integer);
            }
        };
/*
        observable.subscribeOn(Schedulers.newThread())//上游的
                .observeOn(AndroidSchedulers.mainThread())// 将 下游的 的线程变为  子线程;
                .subscribe(consumer);*/

        // test to more cut;
        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        String threadName = Thread.currentThread().getName();
                        Log.i(TAG, "After obserOn (mainThread) current thread is: " +threadName);
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        String threadName = Thread.currentThread().getName();
                        Log.d(TAG, "After obserOn(io), current thread is: " + threadName);
                    }
                })
                .subscribe(consumer);
    }
}
/**
 * 注意:
 *   Rx中: 上游 subscribeOn指定一个就生效, 之后的不再生效
 *         下游 observerOn指定一次就切换一次.
 * */