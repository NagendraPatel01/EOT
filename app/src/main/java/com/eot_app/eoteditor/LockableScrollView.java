package com.eot_app.eoteditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScrollView extends ScrollView {

    private boolean mScrollable = true;

    public LockableScrollView(Context context) {
        super(context);
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean ismScrollable() {
        return mScrollable;
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            return mScrollable && super.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrollable && super.onInterceptTouchEvent(ev);
    }
}
