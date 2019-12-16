package com.example.example.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;


/**
 * 项  目 :
 * 包  名 :  com.example.example.util
 * 类  名 :  SpanHelper
 * 作  者 :
 * 时  间 :  2018/10/17 17:05
 * 描  述 :  ${TODO}
 *
 * @author Administrator
 */
public class SpanHelper {
    private static final String TAG = "SpanHelper";

    /**
     * 处理html文本超链接点击事件     * @param context     * @param tv
     */
    public static void textHtmlClick(TextView tv, ClickUrlListener listener) {
        tv.setMovementMethod(MentionMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spanned) {
            int end = text.length();
            Spanned sp = (Spanned) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            // should clear old spans
            for (URLSpan url : urls) {
                CharSequence charSequence = style.subSequence(sp.getSpanStart(url), sp.getSpanEnd(url));
                EzbURLSpan ezbURLSpan = new EzbURLSpan(url.getURL(), charSequence.toString(), listener);
                style.setSpan(ezbURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setLinksClickable(true);
            tv.setText(style);
        }
    }

    /**
     * 处理html文本超链接点击事件     * @param context     * @param tv
     */
    public static void textHtmlClick(TextView tv, int color, ClickUrlListener listener) {
        tv.setMovementMethod(MentionMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spanned) {
            int end = text.length();
            Spanned sp = (Spanned) text;
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            // should clear old spans
            for (URLSpan url : urls) {
                CharSequence charSequence = style.subSequence(sp.getSpanStart(url), sp.getSpanEnd(url));
                EzbURLSpan ezbURLSpan = new EzbURLSpan(url.getURL(), charSequence.toString(), color, listener);
                style.setSpan(ezbURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            tv.setLinksClickable(true);
            tv.setText(style);
        }
    }


    private static class EzbURLSpan extends ClickableSpan {
        private final ClickUrlListener mListener;
        private final String mText;
        protected String mUrl;
        private int mColor;


        public EzbURLSpan(String url, String text, ClickUrlListener listener) {
            this(url, text, Color.parseColor("#4382FF"), listener);
        }

        public EzbURLSpan(String url, String text, int color, ClickUrlListener listener) {
            mListener = listener;
            mText = text;
            mUrl = url;
            mColor = color;
        }

        @Override
        public void updateDrawState(TextPaint ds) {

            ds.setColor(mColor);
        }

        @Override
        public void onClick(View widget) {
            if (mListener != null) {
                mListener.clickUrl(mUrl, mText);
            }
//
//            if (!TextUtils.isEmpty(mUrl)) {
//
//                if (mUrl.contains(TOPIC)) {
//                    String[] topics = mUrl.split("=");
//                    if (topics.length > 1 && !TextUtils.isEmpty(topics[topics.length - 1])) {
//                        Intent intent = new Intent(ActivityStackManager.getInstance().getTopActivity(), DynamicDetailActivity.class);
//                        ActivityStackManager.getInstance().getTopActivity().startActivity(intent);
//                    }
//                }
//            }
        }
    }

    public interface ClickUrlListener {
        void clickUrl(String url, String text);
    }
}
