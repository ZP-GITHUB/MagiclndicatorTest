package com.zpguet.util;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.zpguet.magiclndicatortest.R;

public class SystemUIUtil {
    public static void fitStatusBar(Window window, Boolean isLight,Boolean fitLayout) {
        int systemUIFlags = window.getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(window.getContext().getColor(R.color.transparent_color));
            if (isLight) {
                systemUIFlags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }else {
                systemUIFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            window.getDecorView().setSystemUiVisibility(systemUIFlags);
        }else {
            window.setStatusBarColor(Color.parseColor("#1C000000"));
        }
        if (fitLayout) {
            fitSystemLayout(window);
        }
    }
    static void fitSystemLayout(Window window) {
        int ui = window.getDecorView().getSystemUiVisibility();
        ui |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        ui |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.getDecorView().setSystemUiVisibility(ui);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
}
