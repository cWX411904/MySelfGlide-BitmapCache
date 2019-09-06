package com.ck.myselfglide.glide.cache;

import com.ck.myselfglide.glide.cache.recycle.BitmapPool;
import com.ck.myselfglide.glide.cache.recycle.LruBitmapPool;
import com.ck.myselfglide.glide.cache.recycle.Resource;

public class CacheTest {

    LruMemoryCache lruMemoryCache;
    ActivityResource activityResource;
    BitmapPool bitmapPool;

    public Resource test(Key key) {

        bitmapPool = new LruBitmapPool(10);

        //内存缓存
        lruMemoryCache = new LruMemoryCache(10);

        //活动缓存
        activityResource = new ActivityResource(new Resource.ResourceListener() {
            @Override
            public void onResourceReleased(Key key, Resource resource) {

                //引用计数=0，会回调，表示这个资源没有正在使用
                //将其重活动资源中移除，重新加入内存缓存中
                activityResource.deActivate(key);
                lruMemoryCache.put(key, resource);
            }
        });

        lruMemoryCache.setResourceRemoveListener(new MemoryCache.ResourceRemoveListener() {
            @Override
            public void onResourceRemoved(Resource resource) {
                //图片被动的从内存缓存中移除了,放入复用池

                //复用池是内存的优化，能够重复利用图片的内存，并不会减少内存使用的大小
                //但是避免频繁申请内存带来的性能问题（抖动、碎片）
                //Bitmap从4.4-8.0是java中，8.0以上是native，所以8.0以上需要执行recycle
                bitmapPool.put(resource.getBitmap());
            }
        });

        /**
         * 第一步 从活动资源中查找，是否有正在使用的图片
         */
        Resource resource = activityResource.get(key);
        if (null == resource) {
            //活动缓存中没有
            /**
             *  第二步 从内存缓存中查找
             */
            resource = lruMemoryCache.get(key);
            if (null != resource) {

                //先从内存缓存中移除  这是为什么呢？
                //原因：1.内存缓存使用了LRU算法，如果不从内存缓存移除，内存缓存如果达到了最大值，那么可能图片会
                //被释放的风险，第二次再来使用，从活动缓存中拿，那么就会报错，因为图片已经执行了recycle()；
                //
                lruMemoryCache.remove(key);

                //内存缓存中有，先添加到活动缓存中
                resource.acquire();
                activityResource.activate(key, resource);
                return resource;
            }
        } else {
            //活动缓存中有
            resource.acquire();
            return resource;
        }
        return resource;
    }
}
