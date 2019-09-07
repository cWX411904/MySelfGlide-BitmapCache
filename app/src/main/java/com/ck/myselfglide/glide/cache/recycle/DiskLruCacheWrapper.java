package com.ck.myselfglide.glide.cache.recycle;

import android.content.Context;

import com.ck.myselfglide.glide.cache.Key;
import com.ck.myselfglide.glide.cache.Utils;
import com.ck.myselfglide.glide.cache.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;

public class DiskLruCacheWrapper implements DiskCache {

    final static int DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024;
    final static String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";

    private MessageDigest MD;
    private DiskLruCache diskLruCache;

    public DiskLruCacheWrapper(Context context) {
        this(new File(context.getCacheDir(), DEFAULT_DISK_CACHE_DIR), DEFAULT_DISK_CACHE_SIZE);
    }

    protected DiskLruCacheWrapper(File directory, long maxSize) {

        try {
            MD = MessageDigest.getInstance("SHA-256");
            //打开一个缓存目录，如果没有则首先创建它；
            //directory：指定数据缓存地址
            //appVersion：APP版本号，当版本号改版时，缓存数据会被清除
            //valueCount：同一个key可以对应多少文件
            //maxSize：最大可以缓存的数据量
            diskLruCache = DiskLruCache.open(directory, 1, 1, maxSize);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getKey(Key key) {
        key.updateDiskCacheKey(MD);
        return new String(Utils.sha256BytesToHex(MD.digest()));
    }

    @Override
    public File get(Key key) {
        String k = getKey(key);
        File result = null;
        try {
            DiskLruCache.Value value = diskLruCache.get(k);
            if (value != null) {
                result = value.getFile(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加入磁盘缓存
     * @param key
     * @param writer
     */
    @Override
    public void put(Key key, Writer writer) {

        String k = getKey(key);
        try {
            DiskLruCache.Value current = diskLruCache.get(k);
            if (current != null) return;
            DiskLruCache.Editor editor = diskLruCache.edit(k);
            try {
                File file = editor.getFile(0);
                if (writer.write(file)) {
                    editor.commit();
                }
            } finally {
                editor.abortUnlessCommitted();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Key key) {
        String k = getKey(key);
        try {
            diskLruCache.remove(k);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            diskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
