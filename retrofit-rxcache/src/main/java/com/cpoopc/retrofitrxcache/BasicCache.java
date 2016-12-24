package com.cpoopc.retrofitrxcache;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * A basic caching system that stores responses in RAM and disk
 * It uses {@link DiskLruCache} and {@link LruCache} to do the former.
 */
public class BasicCache implements IRxCache {
    private DiskLruCache diskCache;
    private LruCache<String, Object> memoryCache;

    /**
     * 构造方法
     */
    public BasicCache(File diskDirectory, long maxDiskSize, int memoryEntries) {
        try {
            diskCache = DiskLruCache.open(diskDirectory, 1, 1, maxDiskSize);
        } catch (IOException exc) {
            Log.e("BasicCache", "", exc);
            diskCache = null;
        }

        memoryCache = new LruCache<>(memoryEntries);
    }

    private static final long REASONABLE_DISK_SIZE = 1024 * 1024; // 1 MB
    private static final int REASONABLE_MEM_ENTRIES = 50; // 50 entries

    /***
     * Constructs a BasicCaching system using settings that should work for everyone
     * 返回 BasicCache，里面是缓存数据
     * @param context    上下文
     * @return  BasicCache
     */
    public static BasicCache fromContext(Context context) {
        return new BasicCache(
                new File(context.getCacheDir(), "retrofit_rxcache"),// 内部缓存建立 名称为 retrofit_rxcache 的文件夹
                REASONABLE_DISK_SIZE,
                REASONABLE_MEM_ENTRIES);
    }

    /**
     * url 转换成 key 的方法，转为 MD5 的字符串
     */
    private String urlToKey(HttpUrl url) {
        return MD5.getMD5(url.toString());
    }

    /**
     * 添加到 缓存
     */
    @Override
    public void addInCache(Request request, Buffer buffer) {
        byte[] rawResponse = buffer.readByteArray();
        String cacheKey = urlToKey(request.url());// 用网址作为 key

        // 添加到内存
        memoryCache.put(cacheKey, rawResponse);

        // 添加到磁盘
        try {
            DiskLruCache.Editor editor = diskCache.edit(urlToKey(request.url()));
            editor.set(0, new String(rawResponse, Charset.defaultCharset()));
            editor.commit();
        } catch (IOException exc) {
            Log.e("BasicCache", "", exc);
        }
    }

    /**
     * @param request
     * @return
     * 从两种缓存中读取数据
     */
    @Override
    public ResponseBody getFromCache(Request request) {
        String cacheKey = urlToKey(request.url());
        byte[] memoryResponse = (byte[]) memoryCache.get(cacheKey);
        if (memoryResponse != null) {
            Log.d("BasicCache", "从内存读取数据!");
            return ResponseBody.create(null, memoryResponse);
        }

        try {
            DiskLruCache.Snapshot cacheSnapshot = diskCache.get(cacheKey);
            if (cacheSnapshot != null) {
                Log.d("BasicCache", "从磁盘读取数据!");
                return ResponseBody.create(null, cacheSnapshot.getString(0).getBytes());
            } else {
                return null;
            }
        } catch (IOException exc) {
            return null;
        }
    }
}
