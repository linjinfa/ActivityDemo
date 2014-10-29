package com.linjf.demo.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.linjf.demo.R;

/**
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月14日 上午11:36:40
 */
public class SimpleMapView extends ViewGroup implements SensorEventListener{
	
	/**
	 * 缩放scale最大值
	 */
	private final float MAX_SCALE = 1.5f;
	/**
	 * 缩放scale最小值
	 */
	private final float MIN_SCALE = 0.8f;
	
	private SensorManager sensorManager;
	private Sensor accelerSensor;
	private FrameLayout childFrameLayout;
	private View imageBitmapView;
	private Drawable drawable;
	private GestureDetector gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	private Scroller scroller;
	private PointF scalePointF = new PointF();
	private float mMatrixValues[] = new float[9];
	private Matrix cavasMatrix = new Matrix();
	/**
	 * 是否正在执行Scale动画
	 */
	private boolean isScaleAniming = false;
	/**
	 * 是否正在执行Scroll动画
	 */
	private boolean isScrollAniming = false;
	/**
	 * layout之后scroll
	 */
	private boolean isLayoutScroll = false;
	/**
	 * layout之后执行动画
	 */
	private boolean isLayoutAnim = false;
	/**
	 * 需要执行动画的View
	 */
	private List<View> animViewList = new ArrayList<View>();
	/**
	 * 滚动到的坐标
	 */
	private float scrollX,scrollY;
	/**
	 * scroll时的回调监听
	 */
	private AnimationListener scrollAnimationListener;
	/**
	 * 是否启用重力感应
	 */
	private boolean isEnableAccelerometer = false;
	
	public SimpleMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SimpleMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SimpleMapView(Context context) {
		super(context);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		initSensor();
		childFrameLayout = new FrameLayout(getContext());
		
		imageBitmapView = new View(getContext());
		imageBitmapView.setTag("imageBitmapView");
		childFrameLayout.addView(imageBitmapView);

		setBackgroundColor(Color.BLUE);
		
		addView(childFrameLayout);
		gestureDetector = new GestureDetector(getContext(), new GestureListenerImpl());
		scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListenerImpl());
		scroller = new Scroller(getContext());
	}
	
	/**
	 * 初始化Sensor
	 */
	private void initSensor(){
		if(isEnableAccelerometer){
			if(sensorManager==null)
				sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
			if(sensorManager!=null){
				accelerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				sensorManager.registerListener(this, accelerSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}else{
				isEnableAccelerometer = false;
			}
		}
	}
	
	/**
	 * 销毁Sensor
	 */
	private void destroySensor(){
		if(sensorManager!=null && accelerSensor!=null){
			sensorManager.unregisterListener(this, accelerSensor);
			sensorManager = null;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int measureWidth = measureWidth(widthMeasureSpec);
		int measureHeight = measureHeight(heightMeasureSpec);
		if(drawable!=null){
			measureChildren(MeasureSpec.makeMeasureSpec(drawable.getIntrinsicWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(drawable.getIntrinsicHeight(), MeasureSpec.EXACTLY));
		}else{
			measureChildren(widthMeasureSpec, heightMeasureSpec);
		}
		setMeasuredDimension(measureWidth, measureHeight);
	}

	/**
	 * 计算宽度
	 * @param pWidthMeasureSpec
	 * @return
	 */
	private int measureWidth(int pWidthMeasureSpec) {
		int result = 0;
		int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
		int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

		switch (widthMode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = widthSize;
			break;
		}
		return result;
	}

	/**
	 * 计算高度
	 * @param pHeightMeasureSpec
	 * @return
	 */
	private int measureHeight(int pHeightMeasureSpec) {
		int result = 0;

		int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
		int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

		switch (heightMode) {
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = heightSize;
			break;
		}
		return result;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// 记录总高度
		int mTotalHeight = 0;
		// 遍历所有子视图
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childView = getChildAt(i);
			// 获取在onMeasure中计算的视图尺寸
			int measureHeight = childView.getMeasuredHeight();
			int measuredWidth = childView.getMeasuredWidth();
			
			childView.layout(l, mTotalHeight, measuredWidth, mTotalHeight + measureHeight);
			mTotalHeight += measureHeight;
		}
		if(isLayoutScroll){
			isLayoutScroll = false;
			scrollToCenterAnim(scrollX, scrollY, scrollAnimationListener);
		}
		layoutAnim();
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			scrollTo(scroller.getCurrX(), scroller.getCurrY());
			invalidate();
		}
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.concat(cavasMatrix);
		super.dispatchDraw(canvas);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		destroySensor();
		super.onDetachedFromWindow();
	}

	/**
	 * layout之后执行动画
	 */
	private void layoutAnim(){
		if(isLayoutAnim){
			List<View> removeViewList = new ArrayList<View>();
			for(int i=0;i<animViewList.size();i++){
				View view = animViewList.get(i);
				removeViewList.add(view);
				view.startAnimation(getViewVisibleAnim(200*i));
			}
			animViewList.removeAll(removeViewList);
		}
	}
	
	/**
	 * 在图上添加子View
	 * @param txt
	 * @param x
	 * @param y
	 * @param isAnim 在view显示的时候执行动画
	 * @return
	 */
	public View addSubTipView(String txt,Drawable drawable,float x,float y){
		TextView txtView = new TextView(getContext());
		if(txt!=null && !"".equals(txt)){
			txtView.setText(txt);
		}
		txtView.setTextColor(Color.WHITE);
		txtView.setBackgroundDrawable(drawable);
		txtView.setGravity(Gravity.CENTER);
		return addSubTipView(txtView,x,y,25,drawable!=null?drawable.getIntrinsicHeight():0);
	}
	
	/**
	 * 在图上添加子View
	 * @param txt
	 * @param x
	 * @param y
	 * @param isAnim 在view显示的时候执行动画
	 * @return
	 */
	public View addSubTipView(String txt,int resId,float x,float y,boolean isAnim){
		return addSubTipView(txt,getResources().getDrawable(R.drawable.rect_bg_normal),x,y);
	}
	
	/**
	 * 在图上添加子View
	 * @param view
	 * @param x
	 * @param y
	 * @param isAnim
	 * @return
	 */
	public View addSubTipView(View view,float x,float y,float leftSpace,float topSpace){
		if(isLayoutAnim){
			animViewList.add(view);
		}
		FrameLayout.LayoutParams lpParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		lpParams.gravity = Gravity.TOP|Gravity.LEFT;
		lpParams.leftMargin = (int) (x - leftSpace);
		lpParams.topMargin = (int) (y - topSpace);
		view.setLayoutParams(lpParams);
		childFrameLayout.addView(view);
		return view;
	}
	
	/**
	 * 子View的显示动画
	 * @return
	 */
	private Animation getViewVisibleAnim(long startOffset){
		ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1f);
		scaleAnimation.setInterpolator(new OvershootInterpolator(2f));
		scaleAnimation.setStartOffset(startOffset);
		scaleAnimation.setDuration(500);
		return scaleAnimation;
	}
	
	/**
	 * 添加路线View
	 * @return
	 */
	public LineView addPathView(float fromX,float fromY,float toX,float toY){
		LineView lineView = new LineView(getContext());
		FrameLayout.LayoutParams lpParams = new FrameLayout.LayoutParams((int) (Math.abs(fromX-toX)+4), (int) (Math.abs(fromY-toY)+4));
		lpParams.gravity = Gravity.TOP|Gravity.LEFT;
		lpParams.leftMargin = (int) (Math.min(fromX, toX)+4);
		lpParams.topMargin = (int) (Math.min(fromY, toY)-10);
		lineView.setLayoutParams(lpParams);
		
		float leftY;
		float rightY;
		if(fromX<toX){
			leftY = fromY;
			rightY = toY;
		}else{
			leftY = toY;
			rightY = fromY;
		}
		
		lineView.setIsStartFromLeftTop(leftY<=rightY);
		
		childFrameLayout.addView(lineView);
		return lineView;
	}
	
	/**
	 * addView后是否执行view的显示动画
	 * @param isAddViewAnim
	 */
	public void setAddViewAnim(boolean isAddViewAnim){
		this.isLayoutAnim = isAddViewAnim;
	}
	
	/**
	 * addView后是否执行view的显示动画
	 */
	public boolean isAddViewAnim(){
		return isLayoutAnim;
	}
	
	/**
	 * 是否启用重力感应
	 * @return
	 */
	public boolean isEnableAccelerometer() {
		return isEnableAccelerometer;
	}

	/**
	 * 设置是否重力感应
	 * @param isEnableAccelerometer
	 */
	public void setEnableAccelerometer(boolean isEnableAccelerometer) {
		this.isEnableAccelerometer = isEnableAccelerometer;
		initSensor();
	}

	/**
	 * 设置scale	在 MIN_SCALE 与 MAX_SCALE之间
	 * @param scale
	 */
	public void setScale(float scale){
		if(scale>=MIN_SCALE && scale<=MAX_SCALE){
			cavasMatrix.setScale(scale, scale);
			invalidate();
		}
	}
	
	/**
	 * 设置scale并执行动画
	 * @param toScale
	 * @param scaleAnimListener
	 */
	public void scaleAnim(final float toScale,final ScaleAnimListener scaleAnimListener){
		stopFling();
		scalePointF.x = getScrollX()+getWidth()/2;
		scalePointF.y = getScrollY()+getHeight()/2;
		calScalePoint(toScale);
		ScaleAnim scaleAnim = new ScaleAnim(getCurrScale(), toScale, scalePointF.x, scalePointF.y);
		scaleAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if(scaleAnimListener!=null){
					scaleAnimListener.onScaleStart(getCurrScale());
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				if(scaleAnimListener!=null){
					scaleAnimListener.onScaleEnd(toScale);
				}
			}
		});
		scaleAnim.setInterpolator(new AnticipateOvershootInterpolator());
		scaleAnim.setDuration(300);
		startAnimation(scaleAnim);
	}
	
	/**
	 * 将中心滚动到指定的坐标
	 * @param x
	 * @param y
	 */
	public void scrollToCenterAnim(float x,float y,AnimationListener animationListener){
		stopFling();
		this.scrollX = x;
		this.scrollY = y;
		this.scrollAnimationListener = animationListener;
		if(getWidth()==0 || getHeight()==0){
			isLayoutScroll = true;
		}else{
			PointF originPoint = getOriginPoint();
			PointF rightCorPoint = getRightCor();
			
			float mX = x*getCurrScale()+originPoint.x;
			float toX = mX-getWidth()/2;
			if(toX>rightCorPoint.x){
				toX = rightCorPoint.x;
			}else if(toX<originPoint.x){
				toX = getOriginX();
			}
			
			float mY = y*getCurrScale()+originPoint.y;
			float toY = mY-getHeight()/2;
			if(toY>rightCorPoint.y){
				toY = rightCorPoint.y;
			}else if(toY<originPoint.y){
				toY = originPoint.y;
			}
			scrollToAnim(getScrollX(), getScrollY(), toX, toY,animationListener);
		}
	}
	
	/**
	 * 从(fromX,fromY)动画滚动到(toX,toY)
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 */
	private void scrollToAnim(float fromX, float fromY, float toX, float toY,AnimationListener animationListener){
		ScrollDisAnim scrollDisAnim = new ScrollDisAnim(fromX,fromY,toX,toY);
		scrollDisAnim.setAnimationListener(animationListener);
		scrollDisAnim.setInterpolator(new DecelerateInterpolator(2));
		scrollDisAnim.setDuration(500);
		startAnimation(scrollDisAnim);
	}

	/**
	 * 设置图片
	 * @param bitmap
	 */
	public void setImageBitmap(Bitmap bitmap){
		if(bitmap==null){
			return ;
		}
		this.drawable = new BitmapDrawable(getResources(), bitmap);
		imageBitmapView.setBackgroundDrawable(drawable);
	}
	
	/**
	 * 清楚所有的子View
	 */
	public void clearAllView(){
		childFrameLayout.removeAllViews();
		childFrameLayout.addView(imageBitmapView);
	}
	
	/**
	 * 获取当前的Scale值
	 * @return
	 */
	public float getCurrScale(){
		return getCurrScale(cavasMatrix);
	}
	
	/**
	 * 根据Matrix获取Scale值
	 * @param matrix
	 * @return
	 */
	private float getCurrScale(Matrix matrix){
		matrix.getValues(mMatrixValues);
		return Math.max(mMatrixValues[0], mMatrixValues[4]);
	}
	
	/**
	 * 缩放后的ScrollX
	 * @return
	 */
	private float getScaleScrollX(){
		return getScrollX()-getOriginX();
	}
	
	/**
	 * 缩放后的ScrollY
	 * @return
	 */
	private float getScaleScrollY(){
		return getScrollY()-getOriginY();
	}
	
	/**
	 * 获取原点坐标
	 * @return
	 */
	private PointF getOriginPoint(){
		return getOriginPoint(cavasMatrix);
	}
	
	/**
	 * 根据Matrix获取原点坐标
	 * @param matrix
	 * @return
	 */
	private PointF getOriginPoint(Matrix matrix){
		RectF rect = new RectF();
		matrix.mapRect(rect);
		return new PointF(rect.left, rect.top);
	}
	
	/**
	 * 原点X坐标
	 * @return
	 */
	private float getOriginX(){
		return getOriginPoint().x;
	}
	
	/**
	 * 原点Y坐标
	 * @return
	 */
	private float getOriginY(){
		return getOriginPoint().y;
	}
	
	/**
	 * 右下角与可视范围重合时左上角的坐标
	 * @return
	 */
	private PointF getRightCor(){
		return getRightCor(cavasMatrix);
	}
	
	/**
	 * 根据Matrix获取右下角与可视范围重合时左上角的坐标
	 * @return
	 */
	private PointF getRightCor(Matrix matrix){
		PointF originPoint = getOriginPoint(matrix);
		//最右边与可视宽度重合时的坐标
		float rightX = getScaleWidth(matrix)-getWidth()+originPoint.x;
		//最底部与可是高度重合时的坐标
		float bottomY = getScaleHeight(matrix)-getHeight()+originPoint.y;
		return new PointF(rightX, bottomY);
	}
	
	/**
	 * scale后的宽度
	 * @return
	 */
	private float getScaleWidth(){
		return getScaleWidth(cavasMatrix);
	}
	
	/**
	 * 根据Matrix获取Scale后的宽度
	 * @param matrix
	 * @return
	 */
	private float getScaleWidth(Matrix matrix){
		return childFrameLayout.getMeasuredWidth()*getCurrScale(matrix);
	}
	
	/**
	 * scale后的高度
	 * @return
	 */
	private float getScaleHeight(){
		return getScaleHeight(cavasMatrix);
	}
	
	/**
	 * 根据Matrix获取Scale后的高度
	 * @param matrix
	 * @return
	 */
	private float getScaleHeight(Matrix matrix){
		return childFrameLayout.getMeasuredHeight()*getCurrScale(matrix);
	}
	
	/**
	 * 拖拽的图片超出范围恢复状态
	 */
	private void restoreScrollAnim(){
		isScrollAniming = true;
		PointF originPoint = getOriginPoint();
		PointF rightBottomPoint = getRightCor();
		if(getScaleScrollX()<0 || getScaleScrollY()<0 || (getScaleScrollX()+getWidth())>getScaleWidth() || (getScaleScrollY()+getHeight())>getScaleHeight()){
			float fromX = getScrollX();
			float fromY = getScrollY();
			float toX = getScaleScrollX()<0?originPoint.x:fromX;
			float toY = getScaleScrollY()<0?originPoint.y:fromY;
			
			toX = (getScaleScrollX()+getWidth())>getScaleWidth()?rightBottomPoint.x:toX;
			toY = (getScaleScrollY()+getHeight())>getScaleHeight()?rightBottomPoint.y:toY;
			
			ScrollDisAnim scrollDisAnim = new ScrollDisAnim(fromX,fromY,toX,toY);
			scrollDisAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					isScrollAniming = false;
				}
			});
			scrollDisAnim.setInterpolator(new DecelerateInterpolator(2));
			scrollDisAnim.setDuration(300);
			startAnimation(scrollDisAnim);
		}else{
			isScrollAniming = false;
		}
	}
	
	/**
	 * scale超过范围时恢复效果
	 * @return
	 */
	private void restoreScaleAnim(){
		isScaleAniming = true;
		float scale = getCurrScale();
		if(getCurrScale()>MAX_SCALE){
			scale = MAX_SCALE;
		}else if(getCurrScale()<MIN_SCALE){
			scale = MIN_SCALE;
		}
		
		calScalePoint(scale);
		
		ScaleAnim scaleAnim = new ScaleAnim(getCurrScale(),scale,scalePointF.x,scalePointF.y);
		scaleAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				isScaleAniming = false;
				restoreScrollAnim();
			}
		});
		scaleAnim.setInterpolator(new AnticipateOvershootInterpolator());
		scaleAnim.setDuration(300);
		startAnimation(scaleAnim);
	}
	
	/**
	 * 计算经过Scale之后的缩放点的坐标
	 * @param scale
	 */
	private void calScalePoint(float scale){
		Matrix tmpScaleMatrix = new Matrix(cavasMatrix);
		tmpScaleMatrix.setScale(scale, scale, scalePointF.x,scalePointF.y);
		PointF tmpPointF = getOriginPoint(tmpScaleMatrix);
		if(scalePointF.x<tmpPointF.x){
			scalePointF.x = tmpPointF.x;
		}
		if(scalePointF.y<tmpPointF.y){
			scalePointF.y = tmpPointF.y;
		}
		PointF rightCorPointF = getRightCor(tmpScaleMatrix);
		if(scalePointF.x>(rightCorPointF.x+getWidth())){
			scalePointF.x = rightCorPointF.x+getWidth();
		}
		if(scalePointF.y>(rightCorPointF.y+getHeight())){
			scalePointF.y = rightCorPointF.y+getHeight();
		}
	}
	
	/**
	 * 停止Fling
	 */
	private void stopFling(){
		if (!scroller.isFinished()) {
			scroller.abortAnimation();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				destroySensor();
			}
			return scaleGestureDetector.onTouchEvent(event) | gestureDetector.onTouchEvent(event);
		} finally{
			if(event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP){
				if(!isScaleAniming){
					restoreScrollAnim();
				}
				initSensor();
			}
		}
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(scroller.isFinished() && !isScrollAniming && !isScaleAniming){
			int disX = (int)event.values[SensorManager.DATA_X]*3;
			int disY = (int)event.values[SensorManager.DATA_Y]*3;
			PointF rightPointF = getRightCor();
			PointF originPointF = getOriginPoint();
			if(getScrollX()+disX<originPointF.x){
				disX = (int) (originPointF.x-getScrollX());
			}else if(getScrollX()+disX>rightPointF.x){
				disX = (int) rightPointF.x-getScrollX();
			}
			if(getScrollY()+disY<originPointF.y){
				disY = (int) (originPointF.y-getScrollY());
			}else if(getScrollY()+disY>rightPointF.y){
				disY = (int) rightPointF.y-getScrollY();
			}
			scrollBy(disX, disY);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	/**
	 * 手势监听
	* @author linjinfa 331710168@qq.com 
	* @version 创建时间：2014年7月15日 下午5:41:19
	 */
	private class GestureListenerImpl extends SimpleOnGestureListener{
		
		@Override
		public boolean onDown(MotionEvent e) {
			stopFling();
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			float scale = getCurrScale()+1f;
			scalePointF.set(e.getX()+getScrollX(),e.getY()+getScrollY());
			ScaleAnim scaleAnim = new ScaleAnim(getCurrScale(),scale,scalePointF.x,scalePointF.y);
			scaleAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					restoreScaleAnim();
				}
			});
			scaleAnim.setDuration(100);
			startAnimation(scaleAnim);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(getScaleScrollX()<-getWidth()/4 || getScaleScrollY()<-getHeight()/4 || (getWidth()-(getScaleWidth()-getScaleScrollX()))>getWidth()/4 || (getHeight()-(getScaleHeight()-getScaleScrollY()))>getHeight()/4){
				distanceX /=2;
				distanceY /=2;
			}else{
				distanceX /=1.2;
				distanceY /=1.2;
			}
			scrollBy((int)(distanceX), (int)(distanceY));
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			PointF originPoint = getOriginPoint();
			if(getScaleScrollX()>0 && getScaleScrollX()<getScaleWidth()-getHeight() && getScaleScrollY()>0 && getScaleScrollY()<getScaleHeight()-getHeight()){
				PointF rightBottomPoint = getRightCor();
				scroller.fling((int)getScrollX(), (int)getScrollY(), (int)-velocityX, (int)-velocityY, (int)originPoint.x, (int)rightBottomPoint.x, (int)originPoint.y, (int)rightBottomPoint.y);
				invalidate();
			}
			return true;
		}
	}
	
	/**
	 * 缩放手势监听
	* @author linjinfa 331710168@qq.com
	* @version 创建时间：2014年7月15日 下午6:44:21
	 */
	private class ScaleGestureListenerImpl extends SimpleOnScaleGestureListener{
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			scalePointF.set(detector.getFocusX()+getScrollX(),detector.getFocusY()+getScrollY());
			return true;
		}
		 
		public boolean onScale(ScaleGestureDetector detector) {
			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float scale = detector.getScaleFactor();
			if(span!=0){
				cavasMatrix.postScale(scale, scale,scalePointF.x,scalePointF.y);
				invalidate();
			}
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			restoreScaleAnim();
		}

	}
	
	/**
	 * scale恢复动画效果
	* @author linjinfa 331710168@qq.com 
	* @version 创建时间：2014年7月17日 上午11:50:41
	 */
	private class ScaleAnim extends Animation {
		
		private float fromScale;
		private float toScale;
		private float centerX;
		private float centerY;
		
		public ScaleAnim(float fromScale, float toScale, float centerX,
				float centerY) {
			super();
			this.fromScale = fromScale;
			this.toScale = toScale;
			this.centerX = centerX;
			this.centerY = centerY;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			if(fromScale!=toScale){
				float scale = fromScale + (toScale - fromScale) * interpolatedTime;
				cavasMatrix.setScale(scale, scale, centerX, centerY);
				invalidate();
			}else{
				this.cancel();
			}
		}

	}
	
	/**
	 * 拖拽超出范围回滚效果
	 * @author linjinfa 331710168@qq.com 
	 * @version 创建时间：2014年7月15日 下午2:03:53
	 */
	private class ScrollDisAnim extends Animation{
		
		private float fromX = 0;
		private float fromY = 0;
		private float toX = 0;
		private float toY = 0;
		
		public ScrollDisAnim(float fromX, float fromY, float toX, float toY) {
			super();
			this.fromX = fromX;
			this.fromY = fromY;
			this.toX = toX;
			this.toY = toY;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			scrollTo((int)(fromX + (toX - fromX) *interpolatedTime), (int)(fromY + (toY - fromY)*interpolatedTime));
		}
		
	}
	
	/**
	 * 缩放Scale监听
	* @author linjinfa 331710168@qq.com 
	* @version 创建时间：2014年7月21日 下午3:23:10
	 */
	public interface ScaleAnimListener{
		/**
		 * scale动画开始
		 * @param fromScale
		 */
		void onScaleStart(float fromScale);
		/**
		 * scale动画结束
		 * @param endScale
		 */
		void onScaleEnd(float endScale);
	}

}
