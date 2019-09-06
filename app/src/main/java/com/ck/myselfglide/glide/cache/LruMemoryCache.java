package com.ck.myselfglide.glide.cache;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.ck.myselfglide.glide.cache.recycle.Resource;

/**
 * 内存缓存
 */
public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache {


    private ResourceRemoveListener listener;

    private boolean isRemove;

    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    /**
     * 返回对应value占用内存大小
     * @param key
     * @param value
     * @return
     */
    @Override
    protected int sizeOf(@NonNull Key key, @NonNull Resource value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4以上版本，当复用的时候，需要通过此函数获得占用内存
            return value.getBitmap().getAllocationByteCount();
        }
        return value.getBitmap().getByteCount();
    }

    /**
     * oldValue 会通过此函数返回
     * 这个是被动移除的
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, @NonNull Key key, @NonNull Resource oldValue, @Nullable Resource newValue) {
        //给复用池使用
        if (null != listener && null != oldValue && !isRemove) {
            listener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public Resource remove2(Key key) {
        //如果是主动移除的不会回调onResourceRemoved
        isRemove = true;
        Resource remove = remove(key);
        isRemove = false;
        return remove;
    }

    /**
     * 当资源从内存缓存移除的时候，告诉感兴趣的人
     *
     * @param listener
     */
    @Override
    public void setResourceRemoveListener(ResourceRemoveListener listener) {
        this.listener = listener;
    }
}
