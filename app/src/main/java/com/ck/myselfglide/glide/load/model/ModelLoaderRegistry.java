package com.ck.myselfglide.glide.load.model;

import android.net.Uri;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册机
 */
public class ModelLoaderRegistry<Model, Data> {

    private List<Entry<Model, Data>> entries = new ArrayList<>();

    /**
     * 负责注册Loader
     * @param modelClass 数据来源类型 比如说url
     * @param dataClass 转换数据之后的类型 比如说inputStream
     * @param factory 创建ModelLoader的工厂
     */
    public synchronized void add(Class<Model> modelClass, Class<Data> dataClass,
                                 ModelLoader.ModelLoaderFactory<Model, Data> factory) {

        entries.add(new Entry<Model, Data>(modelClass, dataClass, factory));

    }

    public <Model, Data>ModelLoader<Model, Data> build(Class<Model> modelClass, Class<Data> dataClass) {

        List<ModelLoader<Model, Data>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {

            //是否我们需要的Model与Data类型的Loader
            if (entry.handles(modelClass, dataClass)) {
                loaders.add((ModelLoader<Model, Data>) entry.factory.build(this));
            }

        }
        if (loaders.size() > 1) {
            //表示我们找到了多个Loader
            return new MultiModelLoader<>(loaders);

        } else if (loaders.size() == 1) {
            return loaders.get(0);
        } else {
            throw new RuntimeException("Not Match :" + modelClass.getName() + ",Data :" + dataClass.getName());
        }
    }

    /**
     * 查找匹配的model类型的ModleLoader
     * @param modelClass
     * @param <Model>
     * @return
     */
    public <Model> List<ModelLoader<Model, ?>> getModelLoaders(Class<Model> modelClass) {
        List<ModelLoader<Model, ?>> loaders = new ArrayList<>();
        for (Entry<?, ?> entry : entries) {
            if (entry.handles(modelClass)) {
                loaders.add((ModelLoader<Model, ?>) entry.factory.build(this));
            }
        }
        return loaders;
    }

    private static class Entry<Model, Data> {
        Class<Model> modelClass;
        Class<Data> dataClass;
        ModelLoader.ModelLoaderFactory<Model, Data> factory;

        public Entry(Class<Model> modelClass, Class<Data> dataClass, ModelLoader.ModelLoaderFactory<Model, Data> factory) {
            this.modelClass = modelClass;
            this.dataClass = dataClass;
            this.factory = factory;
        }

        boolean handles(Class<?> modelClass, Class<?> dataClass) {
            //A.isAssignableFrom(B) 判断B和A是同一个类型，或者B是A的子类
            return this.modelClass.isAssignableFrom(modelClass)
                    && this.dataClass.isAssignableFrom(dataClass);
        }

        boolean handles(Class<?> modelClass) {
            // A.isAssignableFrom(B) B和A是同一个类型 或者 B是A的子类
            return this.modelClass.isAssignableFrom(modelClass);
        }
    }
}
