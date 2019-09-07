package com.ck.myselfglide.glide.cache;

import java.security.MessageDigest;

/**
 * 用来当做缓存中使用的key
 */
public interface Key {

    void updateDiskCacheKey(MessageDigest md);

    byte[] getKeyBytes();
}
