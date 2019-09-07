package com.ck.myselfglide.glide.load;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.ck.myselfglide.BuildConfig;
import com.ck.myselfglide.glide.load.model.FileUriLoader;
import com.ck.myselfglide.glide.load.model.HttpUrlLoader;
import com.ck.myselfglide.glide.load.model.ModelLoader;
import com.ck.myselfglide.glide.load.model.ModelLoaderRegistry;
import com.ck.myselfglide.glide.load.model.StringModelLoader;
import com.ck.myselfglide.glide.load.model.data.DataFetcher;

import java.io.InputStream;
import java.util.List;

public class LoadTest {

    private static final String TAG = "wsj";
    private static final boolean DEBUG = BuildConfig.DEBUG;


    public static void testsFindLoader(Context context) {
        ModelLoaderRegistry loaderRegistry = new ModelLoaderRegistry<>();
        loaderRegistry.add(String.class, InputStream.class, new StringModelLoader.Factory());
        loaderRegistry.add(Uri.class, InputStream.class, new FileUriLoader.Factory(context.getContentResolver()));
        loaderRegistry.add(Uri.class, InputStream.class, new HttpUrlLoader.Factory());

        List<ModelLoader<String, ?>> modelLoaders = loaderRegistry.getModelLoaders(String.class);
        if (DEBUG) Log.d(TAG, "LoadTest testsFindLoader: " + "" + modelLoaders.size());

        ModelLoader<String, ?> stringModelLoader = modelLoaders.get(0);
        stringModelLoader.buildData("http://www.sdff.sdfds");

    }





    public void test(Context context) {


        ModelLoaderRegistry loaderRegistry = new ModelLoaderRegistry<>();
        loaderRegistry.add(String.class, InputStream.class, new StringModelLoader.Factory());
        loaderRegistry.add(Uri.class, InputStream.class, new FileUriLoader.Factory(context.getContentResolver()));
        loaderRegistry.add(Uri.class, InputStream.class, new HttpUrlLoader.Factory());

        List modelLoaders = loaderRegistry.getModelLoaders(String.class);

        Uri uri = Uri.parse("http://www.xxx.yyyy");
        HttpUrlLoader httpUrlLoader = new HttpUrlLoader();
        ModelLoader.LoadData<InputStream> loadData = httpUrlLoader.buildData(uri);

        loadData.dataFetcher.loadData(new DataFetcher.DataFetcherCallback<InputStream>() {
            @Override
            public void onFetcherReady(InputStream inputStream) {
                BitmapFactory.decodeStream(inputStream);
            }

            @Override
            public void onLoadFailed(Exception e) {

            }
        });

        Uri uriFile = Uri.parse("file://a.b.c");
        FileUriLoader fileUriLoader = new FileUriLoader(context.getContentResolver());
        ModelLoader.LoadData<InputStream> loadDataFile = fileUriLoader.buildData(uriFile);
        loadDataFile.dataFetcher.loadData(new DataFetcher.DataFetcherCallback<InputStream>() {
            @Override
            public void onFetcherReady(InputStream inputStream) {

            }

            @Override
            public void onLoadFailed(Exception e) {

            }
        });
    }
}
