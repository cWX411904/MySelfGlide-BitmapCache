package com.ck.myselfglide.glide.cache.recycle;

import android.graphics.Bitmap;

import com.ck.myselfglide.glide.cache.Key;

public class Resource {

    private Bitmap bitmap;
    //引用计数
    private int acquired;
    ResourceListener resourceListener;
    private Key key;

    public Resource(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setResourceListener(Key key, ResourceListener resourceListener) {
        this.key = key;
        this.resourceListener = resourceListener;
    }

    /**
     * 当acquired 为0 时候 回调
     * 这时在回调方法里面，将Bitmap加入内存缓存
     */
    public interface ResourceListener {
        void onResourceReleased(Key key, Resource resource);
    }

    public void recycle() {
        if (acquired >0) {
            return;
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 引用计数-1
     */
    public void release() {
        if (--acquired == 0) {
            resourceListener.onResourceReleased(key, this);
        }
    }

    /**
     * 引用计数+1
     */
    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("bitmap has recycled");
        }
        ++acquired;
    }


}
