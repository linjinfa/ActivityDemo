package com.linjf.demo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月18日 下午4:14:29 
 */
public class LineView extends View{
	
	private Paint paint = new Paint();
	/**
	 * 是否左上角
	 */
	private boolean isLeftTop = false;
	/**
	 * 是否右上角
	 */
	private boolean isRightTop = false;
	private float startX = 0;
	private float startY = 0;
	private float endX = 0;
	private float endY = 0;

	public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public LineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LineView(Context context) {
		super(context);
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		setWillNotDraw(false);
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(10);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setAntiAlias(true);
	}
	
	/**
	 * 设置paint的alpha值	the alpha value is outside of the range [0..255]
	 * @param alpha
	 */
	public void setPaintAlphaF(int alpha){
		paint.setAlpha(alpha);
		invalidate();
	}
	
	/**
	 * 起点是否是左上角
	 * @param isLeftTop
	 * @param isRightTop
	 */
	public void setIsStartFromLeftTop(boolean isLeftTop){
		this.isLeftTop = isLeftTop;
		this.isRightTop = !isLeftTop;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isLeftTop){
			startX = 0;
			startY = 0;
		}else{
			startX = 0;
			startY = getMeasuredHeight();
		}
		if(isRightTop){
			endX = getMeasuredWidth();
			endY = 0;
		}else{
			endX = getMeasuredWidth();
			endY = getMeasuredHeight();
		}
		canvas.drawLine(startX, startY, endX, endY, paint);
	}

}
