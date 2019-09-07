package com.ck.myselfglide.glide.load.model;

import android.net.Uri;

import com.ck.myselfglide.glide.load.ObjectKey;
import com.ck.myselfglide.glide.load.model.data.HttpUriFetcher;

import java.io.InputStream;

/**
 * 处理http请求的ModelLoader
 * 将URI转换为InputStream
 */
public class HttpUrlLoader implements ModelLoader<Uri, InputStream> {

    /**
     * http类型的uri
     * @param uri
     * @return
     */
    @Override
    public boolean handles(Uri uri) {
        String scheme = uri.getScheme();
        return scheme.equalsIgnoreCase("http")
                || scheme.equalsIgnoreCase("https");
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {

        /**
         * eg:以下这两个图片虽然一样，但是对象不一样，key的作用就是为了区分
         * Object 1 =  Uri.fromFile(new File("sdcard/a.png));
         * Object 2 =  Uri.fromFile(new File("sdcard/a.png));
         */
        return new LoadData<InputStream>(new ObjectKey(uri), new HttpUriFetcher(uri));
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
            return new HttpUrlLoader();
        }
    }
}
