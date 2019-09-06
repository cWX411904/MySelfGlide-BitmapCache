package com.ck.bitmapcachedemo;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;

import com.ck.bitmapcachedemo.disk.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 管理内存中的图片
 */
public class ImageCache {

    private static volatile ImageCache instance;
    private Context context;
    //内存缓存
    private LruCache<String, Bitmap> memoryCache;
    //磁盘缓存
    private DiskLruCache diskLruCache;
    //复用池
    public static Set<WeakReference<Bitmap>> bitmapPool;

    private BitmapFactory.Options options = new BitmapFactory.Options();

    private ImageCache() {}

    public static ImageCache getInstance() {

        if (null == instance) {
            synchronized (ImageCache.class) {
                if (null == instance) {
                    instance = new ImageCache();
                }
            }
        }
        return instance;
    }

    //引用队列
    ReferenceQueue referenceQueue;
    Thread clearReferenceThread;
    boolean isShunDow;

    private ReferenceQueue<Bitmap> getReferenceQueue() {
        if (null == referenceQueue) {
            //当若引用需要被回收的时候，会进到这个队列中
            referenceQueue = new ReferenceQueue<Bitmap>();
            //单开一个线程，去扫描引用队列中GC扫描到的内容，交到native去释放
            clearReferenceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!isShunDow) {
                        Reference<Bitmap> reference = null;
                        try {
                            //是阻塞是的队列（Block Queue）所以不会对内存运行有影响
                            reference = referenceQueue.remove();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (reference == null) return;
                        Bitmap bitmap = reference.get();
                        if (null != bitmap && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    }
                }
            });
            clearReferenceThread.start();
        }
        return referenceQueue;
    }

    /**
     *
     * @param context
     * @param dir 磁盘缓存保存的文件路径
     */
    public void init(Context context, String dir) {
        this.context = context.getApplicationContext();

        //复用池
        bitmapPool = Collections.synchronizedSet(new HashSet<WeakReference<Bitmap>>());

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();

        //需要的参数是表示能够缓存的内存最大值，单位是byte,一般是程序最大内存的八分之一
        memoryCache = new LruCache<String, Bitmap>(memoryClass / 8 * 1024 * 1024) {
            /**
             *
             * @param key
             * @param value
             * @return value 对应的内存大小
             */
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                //getByteCount是只用到的空间
                //getAllocationByteCount是真实存在的空间
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    //4.4版本之后的复用方式，需要拿到真实存在的内存空间,这个也是Google官方给的案例提供的
                    return value.getAllocationByteCount();
                }
                return value.getByteCount();
            }

            /**
             * 当LRU满了，oldValue移除了会通过该方法回调出来
             * @param evicted
             * @param key
             * @param oldValue 被移除的对象
             * @param newValue
             */
            @Override
            protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Bitmap oldValue, @Nullable Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                //如果是设置可复用的内存块,拉到java层来管理
                if (oldValue.isMutable()) {
                    //4.4-8.0以下 是java管理Bitmap
                    //8.0以上 是native 需要主动执行recycle
                    bitmapPool.add(new WeakReference<Bitmap>(oldValue, referenceQueue));

                } else {
                    //oldValue
                    oldValue.recycle();
                }
            }
        };

        //磁盘缓存开始
        //valueCount 表示一个key对应一个valueCount文件
        try {
            diskLruCache = DiskLruCache.open(new File(dir), BuildConfig.VERSION_CODE, 1, 10* 1024 * 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getReferenceQueue();

    }

    public void putBitmapToMemery(String key, Bitmap bitmap) {
        memoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemory(String key) {
        return memoryCache.get(key);
    }

    public void clearMemory() {
        memoryCache.evictAll();
    }

    /**
     * 重复用池里面拿Bitmap的对象（也就是内存）
     * @param w
     * @param h
     * @param inSampleSize
     * @return
     */
    public Bitmap getBitmapFromPool(int w, int h, int inSampleSize) {
        Bitmap resueBitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Iterator<WeakReference<Bitmap>> iterator =
                    bitmapPool.iterator();

            while (iterator.hasNext()) {
                Bitmap bitmap = iterator.next().get();
                if (null != bitmap) {
                    //检查Bitmap的内存复用性
                    if (checkInBitmap(bitmap, w, h, inSampleSize)) {
                        resueBitmap = bitmap;
                        iterator.remove();
                        break;
                    } else {
                        iterator.remove();
                    }
                }
            }
        }
        return resueBitmap;
    }

    private boolean checkInBitmap(Bitmap bitmap, int w, int h, int inSampleSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (inSampleSize >= 1) {
                //做过缩放
                w /= inSampleSize;
                h /= inSampleSize;
            }

            int byteCount = w * h * (bitmap.getConfig() == Bitmap.Config.ARGB_8888 ? 4 : 2 );

            return byteCount <= bitmap.getAllocationByteCount();
        }
        return false;
    }

    //磁盘缓存的处理
    /**
     * 加入磁盘缓存
     *
     */
    public void putBitmapToDisk(String key, Bitmap bitmap) {
        DiskLruCache.Snapshot snapshot = null;
        OutputStream os = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null != snapshot) {
                //如果缓存有对应的文件，不做处理
            } else {
                //如果缓存中没有这个文件
                DiskLruCache.Editor editor = diskLruCache.edit(key);
                if (null != editor) {
                    os = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
                    editor.commit();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从磁盘缓存中取
     * @param key
     */
    public Bitmap getBitmapFromDisk(String key, Bitmap ruseableBitmap) {
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;
        try {
            snapshot = diskLruCache.get(key);
            if (null == snapshot) {
                //磁盘中也没有，就需要从网络或文件中获取
                return null;
            }
            //获取文件输入流，读取bitmap
            InputStream is = snapshot.getInputStream(0);
            //解码图片，写入
            options.inMutable = true;
            options.inBitmap = ruseableBitmap;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            if (null != bitmap) {
                //从磁盘中取到后，再加入内存缓存
                memoryCache.put(key, bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != snapshot) {
                snapshot.close();
            }
        }
        return bitmap;
    }

}





















