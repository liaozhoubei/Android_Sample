package com.example.changeskin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatSupportable;

public class ViewLayoutTwo extends FrameLayout implements SkinCompatSupportable {

    private SkinCompatBackgroundHelper mBackgroundHelper;

    public ViewLayoutTwo(Context context) {
        super(context);
        init(null, 0);
    }

    public ViewLayoutTwo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ViewLayoutTwo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mBackgroundHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundHelper.loadFromAttributes(attrs, defStyle);

        LinearLayout linearLayout = (LinearLayout) View.inflate(getContext(), R.layout.layout_two_view, null);
        addView(linearLayout);

    }


    @Override
    public void applySkin() {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.applySkin();
        }
    }
}
