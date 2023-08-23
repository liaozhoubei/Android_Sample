package com.example.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.example.R;

/**
 * 圆形指示器
 * 如果想要大小一样，可以将选中和默认设置成同样大小
 */
public class CircleIndicator extends View implements Indicator {
    private int mNormalRadius;
    private int mSelectedRadius;
    private int maxRadius;
    protected IndicatorConfig config;
    protected Paint mPaint;
    protected float offset = 0f;

    // 指示器相关配置
    private int normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH;
    private int selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH;
    private int normalColor = BannerConfig.INDICATOR_NORMAL_COLOR;
    private int selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR;
    private int indicatorGravity = IndicatorConfig.Direction.CENTER;
    private int indicatorSpace = 0;
    private int indicatorMargin = 0;
    private int indicatorMarginLeft = 0;
    private int indicatorMarginTop = 0;
    private int indicatorMarginRight = 0;
    private int indicatorMarginBottom = 0;
    private int indicatorHeight = BannerConfig.INDICATOR_HEIGHT;
    private int indicatorRadius = BannerConfig.INDICATOR_RADIUS;


    public CircleIndicator(Context context) {
        this(context, null);
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValue(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }

        mNormalRadius = config.getNormalWidth() / 2;
        mSelectedRadius = config.getSelectedWidth() / 2;
        //考虑当 选中和默认 的大小不一样的情况
        maxRadius = Math.max(mSelectedRadius, mNormalRadius);
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        int width = (count - 1) * config.getIndicatorSpace() + config.getSelectedWidth() + config.getNormalWidth() * (count - 1);
        setMeasuredDimension(width, Math.max(config.getNormalWidth(), config.getSelectedWidth()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = config.getIndicatorSize();
        if (count <= 1) {
            return;
        }
        float left = 0;
        for (int i = 0; i < count; i++) {
            mPaint.setColor(config.getCurrentPosition() == i ? config.getSelectedColor() : config.getNormalColor());
            int indicatorWidth = config.getCurrentPosition() == i ? config.getSelectedWidth() : config.getNormalWidth();
            int radius = config.getCurrentPosition() == i ? mSelectedRadius : mNormalRadius;
            canvas.drawCircle(left + radius, maxRadius, radius, mPaint);
            left += indicatorWidth + config.getIndicatorSpace();
        }
//        mPaint.setColor(config.getNormalColor());
//        for (int i = 0; i < count; i++) {
//            canvas.drawCircle(left + maxRadius, maxRadius, mNormalRadius, mPaint);
//            left += config.getNormalWidth() + config.getIndicatorSpace();
//        }
//        mPaint.setColor(config.getSelectedColor());
//        left = maxRadius + (config.getNormalWidth() + config.getIndicatorSpace()) * config.getCurrentPosition();
//        canvas.drawCircle(left, maxRadius, mSelectedRadius, mPaint);
    }

    @NonNull
    @Override
    public View getIndicatorView() {
        if (config.isAttachToBanner()) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            switch (config.getGravity()) {
                case IndicatorConfig.Direction.LEFT:
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
                    break;
                case IndicatorConfig.Direction.CENTER:
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                    break;
                case IndicatorConfig.Direction.RIGHT:
                    layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
                    break;
            }
            layoutParams.leftMargin = config.getMargins().leftMargin;
            layoutParams.rightMargin = config.getMargins().rightMargin;
            layoutParams.topMargin = config.getMargins().topMargin;
            layoutParams.bottomMargin = config.getMargins().bottomMargin;
            setLayoutParams(layoutParams);
        }
        return this;
    }

    private void initValue(AttributeSet attrs) {
        config = new IndicatorConfig();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setColor(config.getNormalColor());
        mNormalRadius = config.getNormalWidth() / 2;
        mSelectedRadius = config.getSelectedWidth() / 2;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Indicator);
            normalWidth = a.getDimensionPixelSize(
                    R.styleable.Indicator_indicator_normal_width,
                    BannerConfig.INDICATOR_NORMAL_WIDTH
            );
            selectedWidth = a.getDimensionPixelSize(
                    R.styleable.Indicator_indicator_selected_width,
                    BannerConfig.INDICATOR_SELECTED_WIDTH
            );
            normalColor = a.getColor(
                    R.styleable.Indicator_indicator_normal_color,
                    BannerConfig.INDICATOR_NORMAL_COLOR
            );
            selectedColor = a.getColor(
                    R.styleable.Indicator_indicator_selected_color,
                    BannerConfig.INDICATOR_SELECTED_COLOR
            );
            indicatorGravity =
                    a.getInt(R.styleable.Indicator_indicator_gravity, IndicatorConfig.Direction.CENTER);
            indicatorSpace = a.getDimensionPixelSize(R.styleable.Indicator_indicator_space, 0);
            indicatorMargin = a.getDimensionPixelSize(R.styleable.Indicator_indicator_margin, 0);
            indicatorMarginLeft =
                    a.getDimensionPixelSize(R.styleable.Indicator_indicator_marginLeft, 0);
            indicatorMarginTop =
                    a.getDimensionPixelSize(R.styleable.Indicator_indicator_marginTop, 0);
            indicatorMarginRight =
                    a.getDimensionPixelSize(R.styleable.Indicator_indicator_marginRight, 0);
            indicatorMarginBottom =
                    a.getDimensionPixelSize(R.styleable.Indicator_indicator_marginBottom, 0);
            indicatorHeight = a.getDimensionPixelSize(
                    R.styleable.Indicator_indicator_height,
                    BannerConfig.INDICATOR_HEIGHT
            );
            indicatorRadius = a.getDimensionPixelSize(
                    R.styleable.Indicator_indicator_radius,
                    BannerConfig.INDICATOR_RADIUS
            );
        }
    }

    private void initIndicatorAttr() {
        if (indicatorMargin != 0) {
            setIndicatorMargins(new IndicatorConfig.Margins(indicatorMargin));
        } else if (indicatorMarginLeft != 0
                || indicatorMarginTop != 0
                || indicatorMarginRight != 0
                || indicatorMarginBottom != 0) {
            setIndicatorMargins(new IndicatorConfig.Margins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom));
        }
        if (indicatorSpace > 0) {
            setIndicatorSpace(indicatorSpace);
        }
        if (indicatorGravity != IndicatorConfig.Direction.CENTER) {
            setIndicatorGravity(indicatorGravity);
        }
        if (normalWidth > 0) {
            setIndicatorNormalWidth(normalWidth);
        }
        if (selectedWidth > 0) {
            setIndicatorSelectedWidth(selectedWidth);
        }

        if (indicatorHeight > 0) {
            setIndicatorHeight(indicatorHeight);
        }
        if (indicatorRadius > 0) {
            setIndicatorRadius(indicatorRadius);
        }
        setIndicatorNormalColor(normalColor);
        setIndicatorSelectedColor(selectedColor);
    }


    public void  setIndicatorSelectedColor(@ColorInt int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedColor(color);
        }
    }

    public void  setIndicatorSelectedColorRes(@ColorRes int color) {
        setIndicatorSelectedColor(ContextCompat.getColor(getContext(), color));
    }

    public void  setIndicatorNormalColor(@ColorInt int color) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalColor(color);
        }
    }

    public void  setIndicatorNormalColorRes(@ColorRes int color) {
        setIndicatorNormalColor(ContextCompat.getColor(getContext(), color));
    }

    public void  setIndicatorGravity(@IndicatorConfig.Direction int gravity) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setGravity(gravity);
            getIndicatorView().postInvalidate();
        }
    }

    public void  setIndicatorSpace(int indicatorSpace) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setIndicatorSpace(indicatorSpace);
        }
    }

    public void  setIndicatorMargins(IndicatorConfig.Margins margins) {
        if (getIndicatorConfig() != null && getIndicatorConfig().isAttachToBanner()) {
            getIndicatorConfig().setMargins(margins);
            getIndicatorView().requestLayout();
        }
    }

    public void  setIndicatorWidth(int normalWidth, int selectedWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth);
            getIndicatorConfig().setSelectedWidth(selectedWidth);
        }
    }

    public void  setIndicatorNormalWidth(int normalWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setNormalWidth(normalWidth);
        }
    }

    public void  setIndicatorSelectedWidth(int selectedWidth) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setSelectedWidth(selectedWidth);
        }
    }

    public void setIndicatorRadius(int indicatorRadius) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setRadius(indicatorRadius);
        }
    }

    public void  setIndicatorHeight(int indicatorHeight ) {
        if (getIndicatorConfig() != null) {
            getIndicatorConfig().setHeight(indicatorHeight) ;
        }
    }

    @Override
    public IndicatorConfig getIndicatorConfig() {
        return config;
    }

    @Override
    public void onPageChanged(int count, int currentPosition) {
        config.setIndicatorSize(count);
        config.setCurrentPosition(currentPosition);
        requestLayout();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        offset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        config.setCurrentPosition(position);
        invalidate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
