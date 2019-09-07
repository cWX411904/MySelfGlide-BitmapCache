package com.ck.myselfglide.glide.load.model;

import android.net.Uri;

import java.io.File;
import java.io.InputStream;

/**
 *
 */
public class StringModelLoader implements ModelLoader<String, InputStream> {

    private final ModelLoader<Uri, InputStream> loader;

    public StringModelLoader(ModelLoader<Uri, InputStream> loader) {
        this.loader = loader;
    }

    @Override
    public boolean handles(String s) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildData(String model) {
        Uri uri = null;
        if (model.startsWith("/")) {
            uri = Uri.fromFile(new File(model));
        } else {
            uri = Uri.parse(model);
        }
        return loader.buildData(uri);
    }

    public static class Factory implements ModelLoaderFactory<String, InputStream> {

        @Override
        public ModelLoader<String, InputStream> build(ModelLoaderRegistry registry) {
            return new StringModelLoader(registry.build(Uri.class, InputStream.class));
        }
    }
}
