package com.example.example.studytouch;

import android.util.Log;
import android.view.MotionEvent;

public class LogUtils {

	public static String getEvent( MotionEvent event) {
		String motionEvent="";
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			motionEvent+="按下";
			break;
		case MotionEvent.ACTION_MOVE:
			motionEvent+="滑动";
			break;
		case MotionEvent.ACTION_UP:
			motionEvent+="离开";
			break;
		default:
			break;
		}
		return motionEvent;
	}
	public static void printLog(Class mClass,String content,MotionEvent ev){
		Log.d(mClass.getSimpleName(), String.format(content, getEvent(ev)));
	}
	public static void printEventLog(Class mClass,String position,boolean isDispatch, MotionEvent event){
		Log.d(mClass.getSimpleName(), position+"-来一个任务<"+getEvent(event)+">:需要给分派给下一届人做吗？"+isDispatch);
	}
	
}
