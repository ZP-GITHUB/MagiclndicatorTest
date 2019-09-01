package com.zpguet.test;

import android.util.Log;

/**
 * Created by Android Studio.
 * User: ZP
 * Date: 2019/6/29
 * Time: 13:21
 */
public class ZPTEST {

    public static void main(String[] args) {
        String str = "asdfsdfs，{br}世纪东方刷卡单";
        str.replace("{br}","\n");
        Log.d("结果：",str);
    }

}
