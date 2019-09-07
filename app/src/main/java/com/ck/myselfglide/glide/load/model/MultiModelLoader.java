package com.ck.myselfglide.glide.load.model;

import java.util.List;

public class MultiModelLoader<Model, Data> implements ModelLoader<Model, Data> {

    //假设list有两个：HttpUriLoader 和 FileUriLoader
    private final List<ModelLoader<Model, Data>> modelLoaders;

    public MultiModelLoader(List<ModelLoader<Model, Data>> modelLoaders) {
        this.modelLoaders = modelLoaders;
    }

    @Override
    public boolean handles(Model model) {
        for (ModelLoader<Model, Data> modelLoader : modelLoaders) {
            if (modelLoader.handles(model)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LoadData<Data> buildData(Model model) {
        for (ModelLoader<Model, Data> modelLoader : modelLoaders) {
            if (modelLoader.handles(model)) {
                LoadData<Data> loadData = modelLoader.buildData(model);
                return loadData;
            }
        }
        return null;
    }

}
