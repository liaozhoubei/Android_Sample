package com.example.example.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

/**
 * 圆形的surfaceview
 * surfaceView虽然继承自View，但是并没有重写onDraw()方法，所以，即使重写了surfaceView的onDraw()方法，也不会被调用。
 * 解决方法是实现了surfaceHolder.Callback接口之后，在surfaceCreated(SurfaceHoder arg0)方法中添加一句 setWillNotDraw(false);
 * 第二种解决方案：https://blog.csdn.net/iteye_17686/article/details/82089862  未尝试
 *
 * 原理： https://blog.csdn.net/luoshengyang/article/details/8661317
 */
public class CircleSurfaceView extends SurfaceView {



    public CircleSurfaceView(Context context) {
        super(context);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        int height = this.getHeight();
        int width = this.getWidth();
        // 计算出控件宽高的最小值为直径
        int diameter = Math.min(width, height);
        float radius = (float) diameter/2;
        int max = Math.max(width, height);
        // 计算出绘制圆形的y轴起始点，让圆形处于中间
        float offset =(float) (max/2);
        //设置裁剪的圆心，半径
        path.addCircle(radius , offset , radius , Path.Direction.CCW);
        //裁剪画布，并设置其填充方式
        if(Build.VERSION.SDK_INT >= 26){
            canvas.clipPath(path);
        }else {
            canvas.clipPath(path, Region.Op.REPLACE);
        }
        super.draw(canvas);
    }

}
