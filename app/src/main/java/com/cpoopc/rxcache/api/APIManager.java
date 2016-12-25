package com.cpoopc.rxcache.api;

import com.cpoopc.retrofitrxcache.BasicCache;
import com.cpoopc.retrofitrxcache.RxCacheCallAdapterFactory;
import com.cpoopc.rxcache.MyApplication;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Administrator on 2016/11/21.
 * API 管理的类
 */
public class APIManager {

    private static String myCacheType;
    private static String BasicUrl = "https://api.github.com";
    private static REST apiInstance;


    //返回 REST ，之后是 调用接口中的方法 再 执行同步/异步请求
    public static REST buildAPI(String url,int cacheType) {// 需要传入缓存类型
        if (apiInstance == null) {
            synchronized (APIManager.class) {
                if (apiInstance == null) {

                    // add
                    //缓存容量
                    long SIZE_OF_CACHE = 20 * 1024 * 1024; // 20 MiB
                    //缓存路径

                    // 1. 设置 okhttp
                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)//
//                            .addInterceptor(new ReceivedCookiesInterceptor())// 获得cookie
//                            .addInterceptor(new ReceivedTokenInterceptor())// 获得token
//                            .addInterceptor(new AddCookiesInterceptor())// 请求头加 cookie
//                            .addInterceptor(new AddTokenInterceptor())// 请求头添加 token
//                            .addInterceptor(new AddNormalHeadsInterceptor())// 请求头 普通公共请求头
//                            .addNetworkInterceptor(new NetInterceptor(mCacheType))//有网络时的拦截器,需要传入缓存的类型
//                            .addInterceptor(new CacheInterceptor(mCacheType))//没网络时的拦截器
//                            .cache(cache)
                            .build();

                    // 2. 创建 Retrofit 对象
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BasicUrl)//
//                            .addConverterFactory(ScalarsConverterFactory.create()) // String 解析
                            .addConverterFactory(GsonConverterFactory.create()) // gson 解析
                            .client(okHttpClient)      //自己定制的okhttpClient
                                    // 分开读取缓存,网络
                            .addCallAdapterFactory(RxCacheCallAdapterFactory.create(BasicCache.fromContext(MyApplication.getContext(),"haha"), false))
                            .build();

                    // 3. 转化成接口
                    apiInstance = retrofit.create(REST.class);

                }
            }
        }
        return apiInstance;
    }


}
