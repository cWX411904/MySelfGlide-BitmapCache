package com.ck.bitmapcachedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

/**
 * 1.项目中的图片位置一定要放在执行的x或者xx路径下；
 *
 * 2.图片缓存
 *      内存缓存（LRU） + 磁盘缓存（LRU）
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "wsj";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //decodeResrouce 解码控制参数 ： Options.inDensity表示像素密度，根据drawable目录进行计算
        //Options.inTargetDensity
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.wyz_p);
//
//        getBitmap(bitmap);
//
//        Bitmap afterBitmap = ImageResize.resizeBitmap(getApplicationContext(), R.mipmap.wyz_p, 373, 459, false);
//
//        getBitmapInfoAfter(afterBitmap);

        ImageCache.getInstance().init(this, Environment.getExternalStorageDirectory() + "/dn");

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new MyAdapter(this));
    }

    private void getBitmapInfoAfter(Bitmap bitmap) {
        if (DEBUG) Log.d(TAG, "MainActivity getBitmapInfoAfter: " + "w = " + bitmap.getWidth() + "" +
                ", h = " + bitmap.getHeight() + ", 内存大小 = " + bitmap.getByteCount());
    }

    private void getBitmap(Bitmap bitmap) {
        if (DEBUG) Log.d(TAG, "MainActivity getBitmap: " + "w = " + bitmap.getWidth() + "" +
                ", h = " + bitmap.getHeight() + ", 内存大小 = " + bitmap.getByteCount());
    }
}
