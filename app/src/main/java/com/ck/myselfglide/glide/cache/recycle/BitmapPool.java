package com.ck.myselfglide.glide.cache.recycle;

import android.graphics.Bitmap;

public interface BitmapPool {

    void put(Bitmap bitmap);

    /**
     * 通过这三个就可以计算内存大小
     * 获得一个可复用的Bitmap
     * @param width
     * @param hegith
     * @param config
     * @return
     */
    Bitmap get(int width, int hegith, Bitmap.Config config);
}
