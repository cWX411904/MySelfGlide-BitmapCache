package com.ck.myselfglide.glide.load.model.data;

/**
 * 负责加载数据的接口，至于怎么加载，看具体实现类
 * @param <Data>
 */
public interface DataFetcher<Data> {

    interface DataFetcherCallback<Data> {

        /**
         * 数据加载完成
         */
        void onFetcherReady(Data data);

        /**
         * 加载失败
         */
        void onLoadFailed(Exception e);
    }

    void loadData(DataFetcherCallback<Data> callback);

    void cancel();
}
