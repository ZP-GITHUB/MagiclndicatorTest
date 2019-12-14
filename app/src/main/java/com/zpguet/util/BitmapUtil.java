package com.zpguet.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.toString();

    public static final String APP_ID = "你的APP id";        // APP id
    public static final String APP_KEY = "你的APP key";       // APP key
    public static final String SECRET_KEY = "你的APP secret key";    // APP secret key

    /*
人像分割（返回的二值图像需要进行二次处理才可查看分割效果）
注意：CreateBitmap传入矩阵缩放，效果比较好，分割出来的图像效果好一些
 */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight){
        int w = bm.getWidth();
        int h = bm.getHeight();

        float scalew = ((float) newWidth) / w;
        float scaleh = ((float) newHeight) / h;

        Matrix matrix = new Matrix();
        matrix.postScale(scalew, scaleh);

        Bitmap scaleBitmap = Bitmap.createBitmap(bm, 0, 0, w, h, matrix, true);
        Bitmap newbitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        int r, g, b = 0;
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                int color = scaleBitmap.getPixel(i, j);
                r = Color.red(color) * 255;
                g = Color.green(color) * 255;
                b = Color.blue(color) * 255;

                newbitmap.setPixel(i, j, Color.argb(122, r, g, b));

            }
        }
        return newbitmap;
    }
}
