package com.ck.myselfglide.glide.cache.recycle;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.ck.myselfglide.glide.cache.recycle.BitmapPool;

import java.util.NavigableMap;
import java.util.TreeMap;

public class LruBitmapPool extends LruCache<Integer, Bitmap>implements BitmapPool {

    //负责筛选
    NavigableMap<Integer, Integer> map = new TreeMap<>();

    private final static int MAX_OVER_SIZE_MULTIPLE = 2;

    private boolean isRemoved;

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }


    /**
     * 将Bitmap放入复用池
     * @param bitmap
     */
    @Override
    public void put(Bitmap bitmap) {

        //isMutable必须是true，才能被复用
        if (!bitmap.isMutable()) {
            bitmap.recycle();
            return;
        }

        int size = bitmap.getAllocationByteCount();

        if (size >= maxSize()) {
            bitmap.recycle();
            return;
        }

        put(size, bitmap);

        map.put(size, 0);

    }

    @Override
    protected int sizeOf(@NonNull Integer key, @NonNull Bitmap value) {
        return value.getAllocationByteCount();
    }

    @Override
    protected void entryRemoved(boolean evicted, @NonNull Integer key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
        map.remove(key);
        if (!isRemoved) {
            oldValue.recycle();
        }
    }

    /**
     * 获得一个可复用的Bitmap
     * @param width
     * @param hegith
     * @param config
     * @return
     */
    @Override
    public Bitmap get(int width, int hegith, Bitmap.Config config) {
        //新Bitmap需要的内存大小 只关心ARGB888 和RGB565
        int size = width * hegith * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);

        //获得等于size或者大于size的key
        Integer key = map.ceilingKey(size);

        //从key集合中找到一个大于等于size，并且小于等于2倍的size
        if (null != key && key <= size * MAX_OVER_SIZE_MULTIPLE) {
            isRemoved = true;
            Bitmap remove = remove(key);
            isRemoved = false;
            return remove;
        }

        return null;
    }
}
