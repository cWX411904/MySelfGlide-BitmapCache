package com.ck.myselfglide.glide.load.model.data;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpUriFetcher implements DataFetcher<InputStream> {

    private final Uri uri;
    private boolean isCanceled;

    public HttpUriFetcher(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void loadData(DataFetcherCallback<InputStream> callback) {
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(uri.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            int responseCode = conn.getResponseCode();
            if (isCanceled) {
                return;
            }
            if (responseCode == HttpURLConnection.HTTP_OK) {
                callback.onFetcherReady(is);
            } else {
                callback.onLoadFailed(new RuntimeException(conn.getResponseMessage()));
            }
        }  catch (MalformedURLException e) {
            callback.onLoadFailed(e);
        } catch (IOException e) {
            callback.onLoadFailed(e);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
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
        isCanceled = true;
    }
}
