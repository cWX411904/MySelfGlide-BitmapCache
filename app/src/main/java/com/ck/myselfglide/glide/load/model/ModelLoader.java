package com.ck.myselfglide.glide.load.model;

import com.ck.myselfglide.glide.cache.Key;
import com.ck.myselfglide.glide.load.model.data.DataFetcher;

/**
 *
 * @param <Mode> 转换前的类型，比如说file、url等等
 * @param <Data> 转换后的类型， 比如说Bitmap、outStream等等
 */
public interface ModelLoader<Mode, Data> {

    /**
     * 返回此loader是否能够处理对应Mode的数据
     * @param mode
     * @return
     */
    boolean handles(Mode mode);

    /**
     * 创建加载数据
     * @param mode
     * @return
     */
    LoadData<Data> buildData(Mode mode);

    class LoadData<Data> {

        //缓存的key，为了标注唯一性
        public final Key key;
        //加载数据
        public final DataFetcher<Data> dataFetcher;

        public LoadData(Key key, DataFetcher<Data> fetcher) {
            this.key = key;
            this.dataFetcher = fetcher;
        }
    }

    interface ModelLoaderFactory<Mode, Data> {

        ModelLoader<Mode, Data> build(ModelLoaderRegistry registry);
    }
}
