package com.ck.myselfglide;

import com.ck.myselfglide.glide.cache.recycle.Resource;

import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws InterruptedException {
        assertEquals(4, 2 + 2);

//        final Resource resource = new Resource();
//        resource.setResourceListener(new Resource.ResourceListener() {
//            @Override
//            public void onResourceReleased() {
//                //当前这个resource没有在使用了
//                resource.recycle();
//            }
//        });
//
//        //如果有一个地方使用这个resource
//        resource.acquire();
//
//        //不再使用，就调release，如果引用计数为0，当前这个resource没有再使用
//        resource.release();

        //强引用
        String a = new String("1");

        //弱引用
        final ReferenceQueue<String> queue = new ReferenceQueue<>();
        new Thread() {
            @Override
            public void run() {
                try {
                    //弱引用被回收掉后，通知我们
                    Reference<? extends String> remove = queue.remove();
                    System.out.println("回收掉：" + remove);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        WeakReference<String> weakReferce = new WeakReference<>(a, queue);
        System.out.println("弱引用1：" + weakReferce.get());
        a = null;
        System.gc();

        System.out.println("弱引用2：" + weakReferce.get());

        Thread.sleep(1000);
    }
}