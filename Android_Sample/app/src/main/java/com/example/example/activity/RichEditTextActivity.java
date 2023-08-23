package com.example.example.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.example.R;
import com.example.example.util.JsoupUtil;
import com.example.example.util.SpanHelper;
import com.example.example.widget.RichEditText;

public class RichEditTextActivity extends AppCompatActivity {
    private String TAG = "RichEditTextActivity";
    private RichEditText etTest;
    private Button atButton;
    private Button topicButton;
    private Button outButton;
    private TextView tvRichText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rich_edit_text);

        initView();

        initData();
    }


    private void initView() {
        etTest = (RichEditText) findViewById(R.id.et_test);
        atButton = (Button) findViewById(R.id.at_button);
        topicButton = (Button) findViewById(R.id.topic_button);
        outButton = (Button) findViewById(R.id.out_button);
        atButton.setOnClickListener(onClickListener);
        topicButton.setOnClickListener(onClickListener);
        outButton.setOnClickListener(onClickListener);
        tvRichText = (TextView) findViewById(R.id.tv_rich_text);
    }

    private void initData() {
        // 符合超链接的正则表达式
        final String sss = "<[Aa]\\s+(.*?\\s+)*?href\\s*=\\s*(['\"]).+?\\2(\\s+.*?\\s*)*?>.+?</[Aa]>";
        String content = "<a href=\"http://192.168.9.30:8080/apk/office.apk\" >#hello#</a><a href=\"http://www.baidu.com\" >@world </a>\uD83D\uDE04\uD83D\uDE01\uD83E\uDD14\uD83D\uDE02";
        etTest.setText(Html.fromHtml(content));
//        etTest.setSpanColor();
        boolean matches = content.matches(sss);
        Log.e(TAG, "onCreate: " + matches);

        // 在TextView 中显示富文本, 并且可点击


        String html = JsoupUtil.createHtml(content, 100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            val content: String = StringUtils.removeFirstAndLastP(record.content)
            tvRichText.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvRichText.setText(Html.fromHtml(html));
        }
        SpanHelper.textHtmlClick(tvRichText, new SpanHelper.ClickUrlListener() {
            @Override
            public void clickUrl(String url, String text) {
                //                //此处处理点击事件  mUrl 为<a>标签的href属性
                Log.e(TAG, text + ": " + url);
                Toast.makeText(RichEditTextActivity.this, text + ": " + url, Toast.LENGTH_SHORT).show();
            }
        }) ;



    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.at_button:
                    etTest.addAtBody("@奥黑子", "http://www.baidu.com");
                    break;
                case R.id.topic_button:
                    etTest.addTopic("#好烦#", "http://www.163.com");
                    break;
                case R.id.out_button:
                    String richText = etTest.getRichText();
                    Log.e(TAG, "onClick: " + richText);
                    break;
            }
//            etTest.setSpanColor();
        }
    };
}
