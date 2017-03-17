package choongyul.android.com.study0317;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

/**
 * Created by DoDo on 2017-03-17.
 */

public class MemoryCacher implements CacheHelper<Bitmap>{
    //      내장 객체  , 키 , 밸류
    private LruCache<String, Bitmap> mMemoryCache;

        // 메모리가 기기마다 다르기때문에 비율로 가져오는게 정석이다.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

    public void init(){
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    public Bitmap get(String key) {
        return getBitmapFromMemCache(key);
    }

    @Override
    public void put(String key, Bitmap what) {
        addBitmapToMemoryCache(key,what);
    }
}
