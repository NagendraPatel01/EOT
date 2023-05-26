package com.eot_app.eoteditor;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

public class CustomEditor extends EotEditor {
    private NestedScrollView parentScrollView;
    private int scrollSize = 0;

    public CustomEditor(Context context) {
        super(context);
    }

    public CustomEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //   onChangeListener();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onChangeListener() {
        this.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            scrollSize=oldScrollY;
            parentScrollView.post(() -> parentScrollView.onNestedScroll(parentScrollView,0,0,0,scrollSize));
        });
        parentScrollView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> parentScrollView.post(() -> parentScrollView.onNestedScroll(parentScrollView,0,0,0,scrollSize)));
    }

    public void setParentScrollView(NestedScrollView parentScroll)  {
        parentScrollView = parentScroll;
    }
}
