package com.example.example.util;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * 项  目 :
 * 包  名 :  com.example.example.util
 * 类  名 :  MentionMovementMethod
 * 作  者 :  刘晓祥
 * 时  间 :  2018/10/16 11:50
 * 描  述 :  ${TODO}
 *
 * @author Administrator
 */
public class MentionMovementMethod extends BaseMovementMethod {
    private static MentionMovementMethod customMovementMethod;

    public static MentionMovementMethod getInstance() {
        if (customMovementMethod == null) {
            synchronized (MentionMovementMethod.class) {
                if (customMovementMethod == null) {
                    customMovementMethod = new MentionMovementMethod();
                }
            }
        }
        return customMovementMethod;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    //除了点击事件，我们不要其他东西
                    link[0].onClick(widget);

//                    ViewParent parent = widget.getParent();//处理widget的父控件点击事件
//                    if (parent instanceof ViewGroup) {
//                        return ((ViewGroup) parent).performClick();
//                    }
                }
                return true;
            } else {
                if (action == MotionEvent.ACTION_UP) {
                    ViewParent parent = widget.getParent();//处理widget的父控件点击事件
                    if (parent instanceof ViewGroup) {
                        return ((ViewGroup) parent).performClick();
                    }
                }
            }
        }
        return true;
    }

    private MentionMovementMethod() {

    }

}
