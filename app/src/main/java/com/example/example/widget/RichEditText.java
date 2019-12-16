package com.example.example.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 简单的富文本 EditText ,无粘贴复制/长按功能
 * 目前仅处理了超链接
 * 更改富文本的颜色请更改 colorAccent 的色值
 */
@SuppressLint("AppCompatCustomView")
public class RichEditText extends EditText implements View.OnKeyListener {
    static String TAG = "RichEditText";
    // 方案1 选中再次按下 del 删除, 比较麻烦，需要做多种处理,问题多; 方案2 选中直接删除
    // 目前方案一有偶发性的bug: 再 onKey 回调中, 虽然设置了 setSelection 选中字符串,
    // getSelectionStart() 以及 getSelectionEnd() 的值也改变了，然而屏幕上选中效果一闪而过，
    // 再次点击 del ,回调 deleteSurroundingText 时，其 beforeLength 为 1, 导致删除错误长度

    // 修复方案1 偶发删除无法选中的 bug ,解决方案为: 按下 del 时再次进行重选
    private int PLAN = 1;
    // 上次选中的富文本 (仅在选中删除时使用)
    String preString = "";
    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnKeyListener(this);
        DisablePaste();

    }

    /**
     * 禁止粘贴复制
     */
    private void DisablePaste() {
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        setLongClickable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // call that method
            setCustomInsertionActionModeCallback(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }



    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //按下键盘时会出发动作，弹起键盘时同样会触发动作
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
            String selectRich = getSelectRich();
            if (PLAN == 1) {
                //  方案1 先选中,不直接删除
                if (!TextUtils.isEmpty(selectRich) && getSelectionStart() == getSelectionEnd()) {
                    int startPos = getSelectionStart();
                    clearFocus();
                    requestFocus();
                    setSelection(startPos - selectRich.length(), startPos);
                    preString = selectRich;
                    return true;
                }
                preString = "";
            } else if (PLAN == 2) {
                // 方案二 直接选中删除，此方法可少做更多处理
                if (!TextUtils.isEmpty(selectRich)) {
                    int startPos = getSelectionStart();
                    clearFocus();
                    requestFocus();
                    setSelection(startPos - selectRich.length(), startPos);
                    return false;
                }
            }


//            lastSelectionEnd = getSelectionEnd();
        }
        return false;
    }

    /**
     * 获取光标前面的富文本
     *
     * @return
     */
    private String getSelectRich() {
        String text = getText().toString();
        String subText = text.substring(0, getSelectionStart());
        SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, getSelectionStart());
        URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        if (urlSpans == null || urlSpans.length <= 0) {
            return "";
        }
        // 通过循环判断富文本是否存在于最后面
        String sub = "";
        for (int i = 0; i < urlSpans.length; i++) {
            URLSpan clickableSpan = urlSpans[i];
            int spanStart = ssb.getSpanStart(clickableSpan);
            int spanEnd = ssb.getSpanEnd(clickableSpan);
            String substring = subText.substring(spanStart, spanEnd);
            if (subText.contains(substring)) {
                if (subText.endsWith(substring)) {
                    sub = substring;
                    break;
                }
            }
        }
        return sub;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        try {
            selectChanged(selStart, selEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为了避免死循环触发onSelectionChanged(),设置的两个标志变量
     */
    private int mNewSelStart, mNewSelEnd;

    private void selectChanged(int selStart, int selEnd) {
        //调用setText()会导致先触发onSelectionChanged()并且start和end均为0,然后才是正确的start和end的值
        if (0 == selStart && 0 == selEnd
                //避免下面的setSelection()触发onSelectionChanged()造成死循环
                || selStart == mNewSelStart && selEnd == mNewSelEnd) {
            return;
        }

        //校准左边光标
        int targetStart = getSelectIndex(selStart);
        targetStart = targetStart == -1 ? selStart : targetStart;
        //校准右边光标
        int targetEnd = getSelectIndex(selEnd);
        targetEnd = targetEnd == -1 ? selEnd : targetEnd;
        //保存新值
        mNewSelStart = targetStart;
        mNewSelEnd = targetEnd;
        //更新选中区域
        setSelection(targetStart, targetEnd);
    }


    /**
     * 掐头去尾,取中间字符串中的富文本
     *
     * @param pos
     * @return 由于富文本无法选中, 所以返回一个合适的位置(返回-1表示不做特殊处理)
     */
    private int getSelectIndex(int pos) {
        if (TextUtils.isEmpty(getText())) {
            return -1;
        }
        String subText = getText().toString();

        SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, getText().length());
        URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        if (urlSpans == null || urlSpans.length <= 0) {
            return -1;
        }
        int index = -1;
        for (int i = 0; i < urlSpans.length; i++) {
            URLSpan clickableSpan = urlSpans[i];
            int spanStart = ssb.getSpanStart(clickableSpan);
            int spanEnd = ssb.getSpanEnd(clickableSpan);
            String substring = subText.substring(spanStart, spanEnd);
            if (subText.contains(substring)) {
                if (pos >= spanStart && pos <= spanEnd) {
                    //将光标移动离当前位置较近的地方
                    index = (pos - spanStart < spanEnd - pos) ? spanStart : spanEnd;
                    break;
                }
            }
        }
        return index;
    }


    @Override
    public void setSelection(int start, int stop) {
        if (0 <= start && stop <= getText().toString().length()) {
            super.setSelection(start, stop);
        }
    }


    /**
     * 插入话题
     *
     * @param topic
     * @param url
     */
    public void addTopic(String topic, String url) {
        final int index = getSelectionStart();//获取光标所在位置
        Editable edit = getEditableText();//获取EditText的文字
        Spanned spanned = Html.fromHtml(createTopic(topic, url));
        if (index < 0 || index >= edit.length()) {
            edit.append(spanned);
        } else {
            edit.insert(index, spanned);//光标所在位置插入文字
        }
//        setSpanColor();
        final int length = spanned.length();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSelection((length + index));
            }
        }, 10);
    }

    public String createTopic(String topic, String url) {
        return "<a href=\"" + url + "\">" + topic + "</a>";
    }


    /**
     * 插入 @某人
     *
     * @param name
     * @param url
     */
    public void addAtBody(String name, String url) {
        final int index = getSelectionStart();//获取光标所在位置
        Editable edit = getEditableText();//获取EditText的文字
        Spanned spanned = Html.fromHtml(createAtBody(name, url));
        if (index < 0 || index >= edit.length()) {
            edit.append(spanned);
        } else {
            edit.insert(index, spanned);//光标所在位置插入文字
        }
        final int length = spanned.length();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setSelection((length + index));
            }
        }, 10);
//        setSpanColor();
    }

    /**
     * 使用 @某人 后面要添加空格便于区分
     *
     * @param name
     * @param url
     */
    public String createAtBody(String name, String url) {
        return "<a href=\"" + url + "\">" + name + " </a>";
    }


    /**
     * 返回 editText 中填入的富文本信息
     *
     * @return
     */
    public String getRichText() {
        if (TextUtils.isEmpty(getText())) {
            return "";
        }
        String text = getText().toString();

        StringBuffer sb = new StringBuffer();
        SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, getText().length());

        URLSpan[] urlSpans = ssb.getSpans(0, ssb.length(), URLSpan.class);
        if (urlSpans == null || urlSpans.length <= 0) {
            return text;
        }
        int preSpanEndPos = 0;
        //   ssb.getSpans 直接插入文本或其他 span 时，其顺序会被打乱需要重新排序
        Arrays.sort(urlSpans, new MyComparator());
        for (URLSpan clickableSpan : urlSpans) {
            int spanStart = ssb.getSpanStart(clickableSpan);
            int spanEnd = ssb.getSpanEnd(clickableSpan);
            String substring = text.substring(spanStart, spanEnd);
            // 添加span 前面的普通文本
            if ((spanStart - preSpanEndPos) > 0) {
                sb.append(text.substring(preSpanEndPos, spanStart));
            }
            if (substring.startsWith("#")) {
                sb.append(createTopic(substring, clickableSpan.getURL()));
            } else if (substring.startsWith("@")) {
                sb.append(createAtBody(substring, clickableSpan.getURL()));
            }

            preSpanEndPos = spanEnd;
        }
        // 添加最末尾的剩余文本
        if (preSpanEndPos < text.length()) {
            sb.append(text.substring(preSpanEndPos));
        }

        return sb.toString();
    }

    public void setSpanColor() {
        SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, getText().length());
        URLSpan[] urlSpans = ssb.getSpans(0, getText().length(), URLSpan.class);
        for (URLSpan urlSpan : urlSpans) {
            CharSequence charSequence = ssb.subSequence(getText().getSpanStart(urlSpan), getText().getSpanEnd(urlSpan));
            EzbURLSpan ezbURLSpan = new EzbURLSpan(urlSpan.getURL(), charSequence.toString());
            ssb.setSpan(ezbURLSpan, ssb.getSpanStart(urlSpan), ssb.getSpanEnd(urlSpan), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        this.setText(ssb);
    }


    /**
     * 对 SpannableStringBuilder 进行排序
     */
    class MyComparator implements Comparator<URLSpan> {

        @Override
        public int compare(URLSpan o1, URLSpan o2) {
            SpannableStringBuilder ssb = (SpannableStringBuilder) getText().subSequence(0, getText().length());
            return ssb.getSpanStart(o1) - ssb.getSpanStart(o2);
        }
    }

    // 用此自定义富文本的显示和点击
    private static class EzbURLSpan extends ClickableSpan {
        private final String mText;
        protected String mUrl;
        private int mColor;


        public EzbURLSpan(String url, String text) {
            this(url, text, Color.parseColor("#4382FF"));
        }

        public EzbURLSpan(String url, String text, int color) {
            mText = text;
            mUrl = url;
            mColor = color;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // 默认使用  colorAccent 颜色值, 若不使用 colorAccent 颜色值, 则在 spanString 中输入文本会有颜色异常的问题
//            ds.setColor(mColor);
        }

        @Override
        public void onClick(View widget) {

        }
    }

    //

    /**
     * 覆盖输入框和键盘的关联接口, 否则在部分机型中无法获取 del 键，如 honor 9x 在英文键盘中无法获取删除键
     *
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new MyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }



    private class MyInputConnection extends InputConnectionWrapper {

        public MyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        //覆盖事件传递
        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }

        // 重新分发删除键事件
        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // 在此处 getSelectionStart() 及  getSelectionEnd() 的值是一样的
            if (PLAN == 1) {
                // 方案二 直接选中删除
                String s = getText().toString();

                String substring = s.substring(getSelectionEnd() - beforeLength, getSelectionEnd());

                // 检测是否包含表情, 如果包含表情,则直接交由系统处理
                boolean isEmoji = containsEmoji(substring);
                if (!isEmoji) {
                    //在删除时，输入框无内容，或者删除以后输入框无内容
                    if (beforeLength == 1 || afterLength == 0 || beforeLength == 0) {
                        // backspace
                        // 判断选中文字是否就是上次选中的文字，如果不是则发送至 onKey 中处理, 如果是,则直接调用 deleteSurroundingText 方法
                        if (!preString.equals(substring)) {
                            return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                                    && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                        } else {
                            // 如果有选中再删除的字符, 则需要在删除时置空, 避免相同字符进入上一次的判断导致删除错误
                            preString = "";
                        }
                    }
                }
            } else if (PLAN == 2) {
                // 方案二 直接选中删除
                if (beforeLength == 1 || afterLength == 0 || beforeLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
            }


            return super.deleteSurroundingText(beforeLength, afterLength);
        }

    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

}
