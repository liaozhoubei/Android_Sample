package com.example.changeskin;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatSupportable;

/**
 * 动态换肤的布局
 * 1.实现SkinCompatSupportable接口
 * 2.applySkin方法中实现换肤操作
 * 3.在构造方法中解析出需要换肤的resId

 */
public class ViewLayoutOne extends FrameLayout implements SkinCompatSupportable {

    private SkinCompatBackgroundHelper mBackgroundHelper;


    public ViewLayoutOne(Context context) {
        super(context);
        init(null, 0);
    }

    public ViewLayoutOne(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ViewLayoutOne(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mBackgroundHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundHelper.loadFromAttributes(attrs, defStyle);

        LinearLayout linearLayout = (LinearLayout) View.inflate(getContext(), R.layout.layout_one_view, null);
        addView(linearLayout);

    }


    @Override
    public void applySkin() {
        if (mBackgroundHelper != null) {
            mBackgroundHelper.applySkin();
        }
    }
}
