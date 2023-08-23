package com.example.customviewdemo.Sliding2;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout {

	private View menuView;// 菜单的view
	private View mainView;// 主界面的view
	private ViewDragHelper viewDragHelper;
	private int width;
	private float dragRange;// 拖拽范围
	private FloatEvaluator floatEvaluator;//float的计算器
	private IntEvaluator intEvaluator;//int的计算器

	public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
		intEvaluator = new IntEvaluator();
	}

	/**
	 * 当DragLayout的xml布局的结束标签被读取完成会执行该方法，此时会知道自己有几个子View了 一般用来初始化子View的引用
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 简单的异常处理
		if (getChildCount() != 2) {
			throw new IllegalArgumentException("SlideMenu only have 2 children!");
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}

	/**
	 * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己和子View的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		dragRange = width * 0.6f;
	}

	// 拦截触摸事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}

	// 处理触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		/**
		 * 用于判断是否捕获当前child的触摸事件 child: 当前触摸的子View return: true:就捕获并解析 false：不处理
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == menuView || child == mainView;
		}

		/**
		 * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return (int) dragRange;
		}

		/**
		 * 控制child在水平方向的移动 left:
		 * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
		 * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (child == mainView) {
				if (left < 0)
					left = 0;
				if (left > dragRange)
					left = (int) dragRange;
			}
			return left;
		}

		/**
		 * 控制child在垂直方向的移动 top:
		 * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
		 * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			if (changedView == menuView) {
				menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
				// 让mainView移动起来
				int newLeft = mainView.getLeft() + dx;
				if (newLeft < 0)
					newLeft = 0;
				if (newLeft > dragRange)
					newLeft = (int) dragRange;
				mainView.layout(newLeft, mainView.getTop() + dy, newLeft + mainView.getMeasuredWidth(),
						mainView.getBottom() + dy);
			}
			
			//1.计算滑动的百分比
			float fraction = mainView.getLeft()/dragRange;
			//2.执行伴随动画
			executeAnim(fraction);
		}

		/**
		 * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
		 * left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (mainView.getLeft() < dragRange / 2) {
				viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			} else {
				viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
		}

	};

	/**
	 * 打开菜单
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(DragLayout.this);
	}
	
	/**
	 * 执行伴随动画
	 * @param fraction
	 */
	protected void executeAnim(float fraction) {
		//fraction:0-1
		//缩小mainView
//		float scaleValue = 0.8f+0.2f*(1-fraction);//1-0.8f
		ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
		ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
		//移动menuView
		ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
		//放大menuView
		ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
		ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
		//改变menuView的透明度
		ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
		
		//给SlideMenu的背景添加黑色的遮罩效果
		getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction,Color.BLACK,Color.TRANSPARENT),Mode.SRC_OVER);
		
	}

	/**
	 * 打开菜单
	 */
	public void open() {
		viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(DragLayout.this);
	}

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	}

}
