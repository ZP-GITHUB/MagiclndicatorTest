package com.zpguet.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    public static String streamToString(InputStream in) {
        String result = "";
        try {
            // 创建一个字节数组写入流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
                out.flush();
            }
            result = new String(out.toByteArray(), "utf-8");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] compressBitmap(Bitmap bmp, int screenWidth) {
        int twidth = bmp.getWidth();
        if (twidth < screenWidth) {
            bmp = ThumbnailUtils.extractThumbnail(bmp, bmp.getWidth(), bmp.getHeight());
        } else {
            float scale = screenWidth * 1.0f / twidth;
            bmp = ThumbnailUtils.extractThumbnail(bmp, screenWidth, (int) (bmp.getHeight() * scale));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bytes会变小
        return baos.toByteArray();
    }

    public static byte[] compressBitmap(InputStream inputStream, Context context) {
        return compressBitmap(BitmapFactory.decodeStream(inputStream),context.getResources().getDisplayMetrics().widthPixels);
    }

}