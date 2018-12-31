package com.kuhrusty.morbadscorepad;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Copied from here:
 * https://stackoverflow.com/questions/4656986/how-do-i-keep-the-aspect-ratio-on-image-buttons-in-android
 */
public class SquareLayout extends LinearLayout {

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
