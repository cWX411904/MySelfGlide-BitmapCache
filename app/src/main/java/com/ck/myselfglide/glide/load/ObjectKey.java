package com.ck.myselfglide.glide.load;

import com.ck.myselfglide.glide.cache.Key;

import java.security.MessageDigest;

public class ObjectKey implements Key {

    private final Object object;

    public ObjectKey(Object object) {
        this.object = object;
    }

    /**
     * 存储内存缓存时候也要有个key
     * 存储磁盘缓存时候，要有个String类型的作为key,
     * 这个方法就是为了获得md或是sha加密的字符串
     * @param md
     */
    @Override
    public void updateDiskCacheKey(MessageDigest md) {
        md.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return object.toString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectKey objectKey = (ObjectKey) o;

        return object != null ? object.equals(objectKey.object) : objectKey.object == null;
    }

    @Override
    public int hashCode() {
        return object != null ? object.hashCode() : 0;
    }
}
