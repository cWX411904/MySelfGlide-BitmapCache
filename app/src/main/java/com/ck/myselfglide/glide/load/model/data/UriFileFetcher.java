package com.ck.myselfglide.glide.load.model.data;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UriFileFetcher implements DataFetcher<InputStream> {

    private Uri uri;
    private ContentResolver resolver;

    public UriFileFetcher(Uri uri, ContentResolver resolver) {
        this.uri = uri;
        this.resolver = resolver;
    }

    @Override
    public void loadData(DataFetcherCallback<InputStream> callback) {

        InputStream is = null;
        try {
            is = resolver.openInputStream(uri);
            callback.onFetcherReady(is);
        } catch (FileNotFoundException e) {
            callback.onLoadFailed(e);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {

    }
}
