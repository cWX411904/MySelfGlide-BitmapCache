package com.ck.myselfglide.glide.load.model;

import android.content.ContentResolver;
import android.net.Uri;

import com.ck.myselfglide.glide.load.ObjectKey;
import com.ck.myselfglide.glide.load.model.data.UriFileFetcher;

import java.io.File;
import java.io.InputStream;

public class FileUriLoader implements ModelLoader<Uri, InputStream> {

    private ContentResolver contentResolver;
    public FileUriLoader(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public boolean handles(Uri uri) {
        //"file://"从文件的mode
        return ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme());
    }

    @Override
    public LoadData<InputStream> buildData(Uri uri) {
        return new LoadData<>(new ObjectKey(uri), new UriFileFetcher(uri, contentResolver));
    }

    public static class Factory implements ModelLoaderFactory<Uri, InputStream> {

        private final ContentResolver contentResolver;

        public Factory(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        @Override
        public ModelLoader<Uri, InputStream> build(ModelLoaderRegistry registry) {
           return new FileUriLoader(contentResolver);
        }
    }
}
