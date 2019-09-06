package com.ck.myselfglide.glide.cache;

import com.ck.myselfglide.glide.cache.recycle.Resource;

public interface MemoryCache {

    Resource put(Key key, Resource resource);

    Resource remove(Key key);

    Resource remove2(Key key);

    void setResourceRemoveListener(ResourceRemoveListener listener);

    interface ResourceRemoveListener {
        void onResourceRemoved(Resource resource);
    }
}
