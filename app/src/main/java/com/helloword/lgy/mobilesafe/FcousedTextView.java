package com.helloword.lgy.mobilesafe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by hasee on 2017/10/2.
 */

public class FcousedTextView extends TextView {
    public FcousedTextView(Context context) {
        super(context);
    }

    public FcousedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FcousedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;

    }
}
