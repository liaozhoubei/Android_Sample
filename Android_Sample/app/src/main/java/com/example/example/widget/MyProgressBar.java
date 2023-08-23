package com.example.example.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.example.R;

/**
 * 自定义 progressbar
 * @see https://www.jianshu.com/p/df387b08c76f
 * 自定义view，你真的理解onMeasure了吗
 * @see https://blog.csdn.net/u012732170/article/details/55045472
 * margin 和 padding的处理
 */
public class MyProgressBar extends View {

    private Paint mbackPaint;
    private Paint mfronPaint;
    private Paint mIndecatePaint;
    private Paint mTextPaint;
    private float durProgress = 500;  // 进度，需要计算获得
    private Bitmap indicateBitmap;

    int defaultWidth = 150;
    int defaultHeight = 150;
    int progressTextPadding = 10;   // 气泡中文字的padding 值
    int progressHeight = 30; // 进度条高度
    int progressRound = 30; // 进度条矩形角度弧度
    Rect indecateRect = new Rect();
    private int defaultTextSize;
    private int progressBackcolor;
    private int progressFrontcolor;
    private int textColor;
    private int textBackgroundColor;
    private int viewPadding = 12;
    int trangleH = 15;
    private Bitmap bitmap;

    public MyProgressBar(Context context) {
        super(context);

    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        mbackPaint = new Paint();
        mfronPaint = new Paint();
        mIndecatePaint = new Paint();
        mTextPaint = new Paint();
        //第二个参数就是我们在styles.xml文件中的<declare-styleable>标签
        //即属性集合的标签，在R文件中名称为R.styleable+name
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBarView);

        //第一个参数为属性集合里面的属性，R文件名称：R.styleable+属性集合名称+下划线+属性名称
        //第二个参数为，如果没有设置这个属性，则设置的默认的值
        defaultWidth = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_width, 10);
        defaultHeight = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_height, 0);
        int textSize = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_text_size, 12);
        int defaultprogressHeight = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_progress_height, 8);
        int defaultProgressRound = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_progress_round, 8);
        int defaultPadding = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_padding, 12);
        int defaultTextPadding = a.getDimensionPixelSize(R.styleable.CustomProgressBarView_default_text_padding, 8);
        progressHeight = dip2px(defaultprogressHeight);
        defaultTextSize = sp2px(textSize);
        progressRound = dip2px(defaultProgressRound);
        viewPadding = dip2px(defaultPadding);
        progressTextPadding = dip2px(defaultTextPadding);

        progressBackcolor = a.getColor(R.styleable.CustomProgressBarView_default_progress_back_color, getResources().getColor(R.color.colorPrimary));
        progressFrontcolor = a.getColor(R.styleable.CustomProgressBarView_default_progress_front_color, getResources().getColor(R.color.colorAccent));
        textColor = a.getColor(R.styleable.CustomProgressBarView_default_text_color, Color.WHITE);
        textBackgroundColor = a.getColor(R.styleable.CustomProgressBarView_default_text_background_color, getResources().getColor(R.color.colorAccent));

        indicateBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        bitmap = resizeBitmap(indicateBitmap, progressHeight * 3, progressHeight * 3);
        if (defaultHeight == 0){
            mTextPaint.setTextSize(defaultTextSize);     //单位为 px
            String text = "123%";
            Rect textRect = new Rect();
            mTextPaint.setTextSize(defaultTextSize);     //单位为 px
            mTextPaint.getTextBounds(text, 0, text.length(), textRect);
            defaultHeight = px2dip(bitmap.getHeight() + trangleH + (textRect.bottom - textRect.top) + viewPadding);
            int dip2px = dip2px(defaultHeight);
            Log.e("init", "init: "+ dip2px );
        }

        //最后记得将TypedArray对象回收
        a.recycle();



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取合适的宽度值
        int width = getProperSize(defaultWidth, widthMeasureSpec);
        // 获取合适的高度值
        int height = getProperSize(defaultHeight, heightMeasureSpec);
        // 设置宽高尺寸大小值，此方法决定view最终的尺寸大小
        setMeasuredDimension(width, height);
    }

    /**
     * 获取合适的大小
     *
     * @param defaultSize 默认大小
     * @param measureSpec 测量规格
     */
    private int getProperSize(int defaultSize, int measureSpec) {
        int properSize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                // 没有指定大小，设置为默认大小
                properSize = defaultSize;
                break;
            case MeasureSpec.EXACTLY:
                // 固定大小，无需更改其大小
                properSize = size;
                break;
            case MeasureSpec.AT_MOST:
                // 在 wrap_content 的情况下要自定义最小值
                // 此处该值可以取小于等于最大值的任意值，此处取最大值的1/4
//                if (size < defaultSize) {
//                    properSize = size | MEASURED_STATE_TOO_SMALL;
//                } else {
//                    properSize = size;
//                }
                properSize = dip2px(defaultHeight);

        }

        return properSize;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int defaultPadding = viewPadding;
        if (paddingLeft == 0 || paddingLeft <defaultPadding){
            paddingLeft = defaultPadding;
        }
        if (paddingRight == 0 || paddingRight < defaultPadding){
            paddingRight = defaultPadding;
        }

        // 设置进度条居中显示
        // 进度条背景色
        mbackPaint.setColor(progressBackcolor);
        int progressTop = getMeasuredHeight()  - progressHeight *2  + paddingTop;
        int progressBottom = getMeasuredHeight()  - progressHeight - paddingBottom;
        // left, top, right, bottom 是矩形四条边的坐标。
        RectF rf = new RectF(0 + paddingLeft,
                progressTop,
                getMeasuredWidth() - paddingRight,
                progressBottom);
        canvas.drawRoundRect(rf, progressRound, progressRound, mbackPaint);

        // 包含 padding left
        int maxWidth = getMeasuredWidth() - paddingRight;
        if (durProgress > maxWidth) {
            durProgress = maxWidth;
        } else if (durProgress <= paddingLeft) {
            durProgress = paddingLeft;
        }
        // 实际进度绘制
        // 进度条前景色
        mfronPaint.setColor(progressFrontcolor);
        RectF rff = new RectF(0 + paddingLeft,
                progressTop,
                durProgress,
                progressBottom);
        canvas.drawRoundRect(rff, progressRound, progressRound, mfronPaint);




        mIndecatePaint.reset();
        int bitmapStartX = (int) (durProgress - bitmap.getWidth() / 2);
        int bitmapStartY = paddingTop + getMeasuredHeight() - progressHeight *2 + progressHeight /2 - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, bitmapStartX, bitmapStartY, mIndecatePaint);

        indecateRect.left = bitmapStartX;
        indecateRect.top = bitmapStartY;
        indecateRect.right = bitmapStartX + bitmap.getWidth();
        indecateRect.bottom = bitmapStartY + bitmap.getHeight();

        // 顶部指示标
        Path path = new Path();
        // 进度条的终点
        int startPointX = (int) (durProgress);
        int startPointY = bitmapStartY;

        Path tranglePath = new Path();

        tranglePath.moveTo(startPointX, startPointY);// 此点为多边形的起点
        tranglePath.lineTo(startPointX - trangleH, startPointY - trangleH);
        tranglePath.lineTo(startPointX + trangleH, startPointY - trangleH);
        tranglePath.close(); // 使这些点构成封闭的多边形


        float percentValue = (durProgress- paddingLeft)/(maxWidth- paddingLeft) *100;
        String percent = String.format("%.2f", percentValue);


        mTextPaint.reset();
        String text = percent + "%";
        Rect textRect = new Rect();
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(defaultTextSize);     //单位为 px
        mTextPaint.getTextBounds(text, 0, text.length(), textRect);
        int textWidth = textRect.right - textRect.left;
        int textHeight = textRect.bottom - textRect.top;
        // 如果文字的宽度比底部三角的边长大小不超过10像素
        if (Math.abs(textWidth - trangleH) < 10) {
            textWidth += 2 * trangleH;
        }

        int popRectFLeft = startPointX - textWidth / 2 - progressTextPadding;
        int popRectFRight = startPointX + textWidth / 2 + progressTextPadding;
        int textleft = 0;
        // 当进度条移到左边屏幕边界
        if (popRectFLeft < 0) {
            popRectFLeft = 0;
            popRectFRight = textWidth + 2 * progressTextPadding;
            textleft = progressTextPadding;
        }
        else if (popRectFRight > maxWidth && popRectFRight > getMeasuredWidth()) {
            // 当进度条移到右边屏幕边界
            popRectFLeft = getMeasuredWidth() - (textWidth + 2 * progressTextPadding);
            popRectFRight = getMeasuredWidth();
            textleft = getMeasuredWidth() - textWidth - progressTextPadding;
        }
        else {
            textleft = startPointX - textWidth / 2;
        }
        RectF popRectF = new RectF(popRectFLeft,
                startPointY - trangleH - textHeight - progressTextPadding,
                popRectFRight,
                startPointY - trangleH);
        mIndecatePaint.setColor(textBackgroundColor);
        path.addRoundRect(popRectF, 10, 10, Path.Direction.CW);
        path.op(tranglePath, Path.Op.UNION);
        canvas.drawPath(path, mIndecatePaint);

        // 如果文字的宽度比底部三角的边长大小不超过10像素
        if (Math.abs((textRect.right - textRect.left) - trangleH) < 10) {
            // 获取文字宽度，绘制在边框中间
            int w = textRect.right - textRect.left;
            int popRectfR = (int) (popRectF.right - popRectF.left)/2 - w/2;
            textleft = (int) (popRectF.left+popRectfR);
        }
        // text 绘制以左下角为基准点
        canvas.drawText(text, textleft, startPointY - trangleH - progressTextPadding/2, mTextPaint);


    }


    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public  int sp2px( float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private boolean canMove = false;
    float prePositionX = 0;
    float prePositionY = 0;

    // 如果想自己处理触摸事件，返回值要为true,否则只能获取 MotionEvent.ACTION_DOWN 事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float positionX = event.getX();
        float positionY = event.getY();
//        Log.e("Progress ", "onTouchEvent: " + positionX + "   y:" + positionY);
//        Log.e("Progress ", "rect left : " + indecateRect.left + "   right:" + indecateRect.right + "   top:" + indecateRect.top + "   bottom:" + indecateRect.bottom);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prePositionX = event.getX();
                prePositionY = event.getY();
                // 点中的区域是进度条指示标
                if (positionX > indecateRect.left && positionX < indecateRect.right && positionY > indecateRect.top && positionY < indecateRect.bottom) {
                    canMove = true;
                }
                // 点中的区域是进度条的其他位置
                if (positionY > indecateRect.top && positionY < indecateRect.bottom) {
                    canMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMove) {
                    durProgress = (int) positionX;
                    postInvalidate();
                    canMove = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e("Progress ACTION_MOVE", "onTouchEvent: " + positionX + "   y:" + positionY);
                if (Math.abs(prePositionY - positionY) > 150) {
                    canMove = false;
                }
                // 上下移动不理会
                if (canMove) {
                    durProgress = (int) positionX;
                    postInvalidate();
                }

                break;
        }
        if (canMove) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
