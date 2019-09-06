package com.ck.myselfglide.glide.cache;

import com.ck.myselfglide.glide.cache.recycle.Resource;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动缓存
 * 正在使用的图片资源
 */
public class ActivityResource {

    private ReferenceQueue<Resource> queue;
    private final Resource.ResourceListener resourceListener;
    private Map<Key, ResourceWeakReference> activityResources = new HashMap<>();
    private Thread cleanReferenceQueueThread;
    private boolean isShutDown;

    public ActivityResource(Resource.ResourceListener listener) {
        this.resourceListener = listener;
    }

    /**
     * 加入活动缓存
     * @param key
     * @param resource
     */
    public void activate(Key key, Resource resource) {
        resource.setResourceListener(key, resourceListener);
        activityResources.put(key, new ResourceWeakReference(key,resource, getReferenceQueue()));
    }

    /**
     * 移除活动缓存
     */
    public Resource deActivate(Key key) {
        ResourceWeakReference reference = activityResources.remove(key);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    public Resource get(Key key) {
        ResourceWeakReference reference = activityResources.get(key);
        if (reference != null) {
            return reference.get();
        }
        return null;
    }

    public void shundown() {
        isShutDown = true;
        if (cleanReferenceQueueThread != null) {
            cleanReferenceQueueThread.interrupt();
            try {
                //保证线程被关掉
                cleanReferenceQueueThread.join(5000);
                if (cleanReferenceQueueThread.isAlive()) {
                    throw new RuntimeException("5s后还是没关掉");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private ReferenceQueue<Resource> getReferenceQueue() {
        if (null == queue) {
            queue = new ReferenceQueue<>();
            Thread cleanReferenceQueueThread = new Thread() {
                @Override
                public void run() {
                    while (!isShutDown) {
                        //监听到别回收掉的引用
                        try {
                            ResourceWeakReference ref = (ResourceWeakReference) queue.remove();
                            activityResources.remove(ref);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
            cleanReferenceQueueThread.start();
        }
        return queue;
    }

    static final class ResourceWeakReference extends WeakReference<Resource> {

        private final Key key;

        public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }
}
