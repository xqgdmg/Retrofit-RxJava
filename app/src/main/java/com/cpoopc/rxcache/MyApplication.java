package com.cpoopc.rxcache;

import android.app.Application;
import android.content.Context;

import com.cpoopc.rxcache.api.REST;

import com.cpoopc.retrofitrxcache.BasicCache;
import retrofit2.Retrofit;
import com.cpoopc.retrofitrxcache.RxCacheCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User: cpoopc
 * Date: 2016-01-18
 * Time: 13:53
 * Ver.: 0.1
 */
public class MyApplication extends Application {

    private static MyApplication instance;
    private static Context mContext;
    private Retrofit mRetrofit;
    private REST mREST;

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {

        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

//        instance = this;
//        mRetrofit = new Retrofit.Builder()
//                .baseUrl("https://api.github.com")
//
//                // 分开读取缓存,网络
//                .addCallAdapterFactory(RxCacheCallAdapterFactory.create(BasicCache.fromContext(this), false))
//
//                // 先读取缓存,再获取网络
////                .addCallAdapterFactory(RxCacheCallAdapterFactory.create(BasicCache.fromContext(this), true))
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
    }

    public REST ApiManager() {
        if (mREST == null) {
            mREST = mRetrofit.create(REST.class);
        }
        return mREST;
    }
}
