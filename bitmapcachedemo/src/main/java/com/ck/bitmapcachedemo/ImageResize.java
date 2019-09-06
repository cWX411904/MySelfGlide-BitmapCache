package com.ck.bitmapcachedemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageResize {

    public static Bitmap resizeBitmap(Context context, int id, int maxW, int maxH, boolean hasAlpha, Bitmap mutableBitmap) {

        Resources resources = context.getResources();
        //自定义一个Options
        BitmapFactory.Options options = new BitmapFactory.Options();
        //需要拿到系统处理的信息，比如解码后的宽高
        options.inJustDecodeBounds = true;
        //把原来的解码参数改了再生成Bitmap
        BitmapFactory.decodeResource(resources, id, options);
        //得到原图的宽高
        int oldWidth = options.outWidth;
        int oldHeight = options.outHeight;

        //设置缩放系数
        options.inSampleSize = calcuteInSampleSize(oldWidth, oldHeight, maxW, maxH);

        /**
         * 如果不需要Alpha通道
         */
        if (!hasAlpha) {
            //16位=两个字节，不需要透明度
            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        //拿到系数后，将inJustDecodesBounds关闭
        options.inJustDecodeBounds = false;

        //设置成能复用内存
        options.inMutable = true;
        options.inBitmap = mutableBitmap;

        //把原来的解码参数改了再生成Bitmap
        return BitmapFactory.decodeResource(resources, id, options);
    }

    /**
     *
     * @param oldWidth
     * @param oldHeight
     * @param maxW
     * @param maxH
     * @return 原来解码的图片的大小，是我们需要的大小的最接近2的几次方
     */
    private static int calcuteInSampleSize(int oldWidth, int oldHeight, int maxW, int maxH) {

        int inSampleSize = 1;

        if (oldWidth > maxW && oldHeight > maxH) {
            inSampleSize = 2;
            while (oldWidth / inSampleSize > maxW && oldHeight / inSampleSize > maxH) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
