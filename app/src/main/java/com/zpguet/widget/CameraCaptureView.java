package com.zpguet.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class CameraCaptureView extends View {
    private Paint paint;
    public CameraCaptureView(Context context) {
        this(context,null);
    }

    public CameraCaptureView(Context context, AttributeSet attributeSet) {
        this(context,attributeSet,0);
    }

    public CameraCaptureView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        this(context,attributeSet,defStyleAttr,0);
    }

    public CameraCaptureView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes) {
        super(context,attributeSet,defStyleAttr,defStyleRes);
        paint = new Paint();

    }
}
