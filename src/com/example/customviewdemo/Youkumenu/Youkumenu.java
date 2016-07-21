package com.example.customviewdemo.Youkumenu;

import com.example.customviewdemo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
/**
 * 实现优酷菜单的方法
 * @author ASUS-H61M
 *
 */
public class Youkumenu extends Activity implements OnClickListener{
	private RelativeLayout rl_level1;
	private RelativeLayout rl_level2;
	private RelativeLayout rl_level3;
	private boolean isDisplaylevel3 = true;
	private boolean isDisplaylevel2 = true;
	private boolean isDisplaylevel1 = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_youkumenu);
		
		initView();
	}
	// 初始化控件
	private void initView() {
		findViewById(R.id.ib_home).setOnClickListener(this);
		findViewById(R.id.ib_menu).setOnClickListener(this);
		
		rl_level1 = (RelativeLayout) findViewById(R.id.rl_level1);
		rl_level2 = (RelativeLayout) findViewById(R.id.rl_level2);
		rl_level3 = (RelativeLayout) findViewById(R.id.rl_level3);
	}

	@Override
	public void onClick(View v) {
		// 如果在动画正在运行的时候点击，那么直接返回，不执行新的动画
		if (AnimationUtil.runningAnimationCount > 0) {
			return;
		}
		
		switch (v.getId()) {
		case R.id.ib_home:
			// 点击了主页按钮
			if (isDisplaylevel2) {
				// 设置延时时间
				long delay = 0;
				if (isDisplaylevel3) {
					// 如果菜单已经显示，那么设置不显示
					AnimationUtil.RotateAnimationOut(rl_level3, 0);
					isDisplaylevel3 = false;
					// 当第三级菜单存在是，设置延时时间为200，然后程序往下运行时，二级菜单将会延时执行
					delay += 200;
				}
				AnimationUtil.RotateAnimationOut(rl_level2,delay);
			} else {
				AnimationUtil.RotateAnimationIn(rl_level2, 0);
			}
			isDisplaylevel2 = !isDisplaylevel2;
			break;
			
		case R.id.ib_menu:
			// 点击了菜单按钮
			if (isDisplaylevel3) {
				// 如果菜单已经显示，那么设置不显示
				AnimationUtil.RotateAnimationOut(rl_level3, 0);
			} else {
				// 如果菜单不显示，那么选择显示出来
				AnimationUtil.RotateAnimationIn(rl_level3, 0);
			}
			isDisplaylevel3 = !isDisplaylevel3;
			break;

		default:
			break;
		}
	}
	// 按下物理按键menu的时候（使用虚拟机时要设置成有物理按键）
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (AnimationUtil.runningAnimationCount > 0) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			long delay = 0;
			//判断是否存在1级菜单没有隐藏
			if (isDisplaylevel1) {
				
				if (isDisplaylevel3){
					// 隐藏三级菜单
					AnimationUtil.RotateAnimationOut(rl_level3, delay);
					isDisplaylevel3 = false;
					delay += 200;
				}
				if (isDisplaylevel2) {
					// 隐藏二级菜单
					AnimationUtil.RotateAnimationOut(rl_level2, delay);
					isDisplaylevel2 = false;
					delay += 200;
				}
				AnimationUtil.RotateAnimationOut(rl_level1, delay);
			} else {
				// 如果菜单都被隐藏，那么现实出来
				AnimationUtil.RotateAnimationIn(rl_level1, 0);
				AnimationUtil.RotateAnimationIn(rl_level2, 200);
				AnimationUtil.RotateAnimationIn(rl_level3, 300);
				
				isDisplaylevel2 = true;	
				isDisplaylevel3 = true;	
			}
			isDisplaylevel1 = !isDisplaylevel1;
			return true;
			
		}
		// 返回true则代表在onKeyDown中使用了点击事件，这样点击事件就不会被其他代码使用
		return super.onKeyDown(keyCode, event);
	}


}
