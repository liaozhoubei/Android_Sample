package com.example.example.studytouch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;


public class ThirdTextView extends androidx.appcompat.widget.AppCompatTextView {

	public ThirdTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		LogUtils.printLog(getClass(), "【实习生】来一任务<%s>:需要{分派}给下一级吗？我想分派，我底下没人了，怎么办？我还是老老实实的干吧", event);
		return super.dispatchTouchEvent(event);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean is=false;
		String result;
		if(is){
			result="皇天不负有心人，【解决了】。";
		}else{
			result="皇天就负有心人，无人为力，【没解决】。";
		}
		LogUtils.printLog(getClass(), "【实习生】自己{处理}任务<%s>:查阅资料，埋头苦干，一声不吭的干了起来。"+result, event);
	
		return is;
	}
}
