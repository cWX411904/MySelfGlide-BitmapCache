package com.ck.bitmapcachedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MyAdapter extends BaseAdapter {

    private static final String TAG = "wsj";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private Context context;

    public MyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 999;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Bitmap bitmap = ImageCache.getInstance().getBitmapFromMemory(String.valueOf(position));

        if (null == bitmap) {
            //如果内存中没有数据，就去复用池找
            Bitmap reuseable = ImageCache.getInstance().getBitmapFromPool(60, 60,  1);
            //将能复用的内存块，从磁盘中找
            bitmap = ImageCache.getInstance().getBitmapFromDisk(String.valueOf(position), reuseable);

            if (null == bitmap) {
                Log.d(TAG, "getView: 磁盘中没有，从网络中加载数据");
                bitmap = ImageResize.resizeBitmap(context, R.mipmap.wyz_p, 80, 80, false, reuseable);
                ImageCache.getInstance().putBitmapToMemery(String.valueOf(position), bitmap);
                ImageCache.getInstance().putBitmapToDisk(String.valueOf(position), bitmap);
            } else {
                Log.d(TAG, "getView: 从磁盘中找到了数据");
            }

        } else {
            Log.d(TAG, "getView: 从内存中找到了数据");
        }

        holder.iv.setImageBitmap(bitmap);

        return convertView;
    }

    class ViewHolder {
        ImageView iv;

        ViewHolder(View view) {
            iv = view.findViewById(R.id.iv);
        }
    }
}
