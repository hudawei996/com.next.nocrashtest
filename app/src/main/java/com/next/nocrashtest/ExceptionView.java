package com.next.nocrashtest;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

/**
 * Created by wanjian on 2018/5/29.
 */

public class ExceptionView extends AppCompatButton {
    boolean excep = false;

    public ExceptionView(Context context) {
        this(context, null);
    }

    public ExceptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                excep = true;
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (excep)
            throw new RuntimeException("view onDraw 抛出异常");
    }
}
