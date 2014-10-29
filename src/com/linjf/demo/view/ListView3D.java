package com.linjf.demo.view;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月26日 下午12:37:14
 */
public class ListView3D extends AdapterView<Adapter> {

	private Adapter mAdapter;
	private int firstChildTop;
	private int scrolledDistance;
	private int lastItemPosition = -1;
	private int firstItemPosition;
	private Point downPoint = new Point();
	private Point movePoint = new Point();
	private int mTouchSlop;
	private boolean isTouchCancel = false;
	private final LinkedList<View> mCachedItemViews = new LinkedList<View>();

	public ListView3D(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ListView3D(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ListView3D(Context context) {
		super(context);
		init();
	}

	private void init() {
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
System.out.println("mTouchSlop========>  " + mTouchSlop);
	}

	@Override
	public Adapter getAdapter() {
		return null;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		this.mAdapter = adapter;
		isTouchCancel = false;
		firstChildTop = 0;
		scrolledDistance = 0;
		lastItemPosition = -1;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setSelection(int position) {

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
System.out.println("d---------======onMeasure " + getChildCount());
		if (mAdapter == null || isTouchCancel) {
			return;
		}
		if (getChildCount() == 0) {
			fillListDown(0);
		}else{
			removeNonVisibleViews();
			fillList();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
System.out.println("=========onLayout " + getChildCount());
		if (mAdapter == null || isTouchCancel) {
			return;
		}
		// if(getChildCount()==0){
		// fillListDown();
		// }
		positioinItems();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getChildCount() == 0) {
			return false;
		}
		isTouchCancel = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downPoint.set((int) event.getX(), (int) event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
System.out.println("hhhhh================>  "+getChildCount());
			movePoint.set((int) event.getX(), (int) event.getY());
			if (startScrollIfNeeded()) {
				requestLayout();
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			isTouchCancel = true;
			firstChildTop = getChildAt(0).getTop();
			break;
		}
		return true;
	}

	/**
	 * 是否满足滚动条件
	 * 
	 * @param y
	 *            当前触摸点Y轴的值
	 * @return true 可以滚动
	 */
	private boolean startScrollIfNeeded() {
		// 不同，此处模拟AbsListView实现
		scrolledDistance = movePoint.y - downPoint.y;
		// 只有移动一定距离之后才认为目的是想让ListView滚动
		if (Math.abs(scrolledDistance) > mTouchSlop) {
			return true;
		}
		return false;
	}
	
	/**
	 * 删除当前已经移除可视范围的Item View
	 */
	private void removeNonVisibleViews(){
		if(getChildCount()>0){
			View firstChildView = getChildAt(0);
			if(firstChildView.getBottom()<-firstChildView.getMeasuredHeight()){
				firstItemPosition++;
				firstChildTop = -firstChildView.getMeasuredHeight()-scrolledDistance;
				removeViewInLayout(firstChildView);
			}
		}
		if(getChildCount()>0){
			View lastChildView = getChildAt(getChildCount()-1);
			if(lastChildView.getTop()>getHeight()+lastChildView.getMeasuredHeight()){
				lastItemPosition--;
				removeViewInLayout(lastChildView);
			}
		}
	}

	/**
	 * 底部填充子视图
	 */
	private void fillListDown(int bottomEdge) {
		int height = getHeight();
		if(getChildCount()!=0){
			height +=getChildAt(getChildCount()-1).getMeasuredHeight();
		}
		while (bottomEdge < height && lastItemPosition < mAdapter.getCount() - 1) {
			lastItemPosition++;
			View childView = mAdapter.getView(lastItemPosition, getCachedView(), this);
			addAndMeasureChild(childView,-1);
			bottomEdge += childView.getMeasuredHeight();
		}
	}
	
	/**
	 * 顶部填充子视图
	 * @param topEdge
	 */
	private void fillListUp(int topEdge){
		if(topEdge>-getChildAt(0).getMeasuredHeight() && firstItemPosition>0){
			firstItemPosition--;
			View childView = mAdapter.getView(firstItemPosition, getCachedView(), this);
			addAndMeasureChild(childView,0);
			firstChildTop = -(childView.getMeasuredHeight()+Math.abs(topEdge))-scrolledDistance;
		}
	}
	
	/**
	 * 填充子视图
	 */
	private void fillList() {
		final int bottomEdge = getChildAt(getChildCount() - 1).getBottom();
		fillListDown(bottomEdge);
		
		final int topEdge = getChildAt(0).getTop();
		fillListUp(topEdge);
	}

	/**
	 * 
	 * @param childView
	 */
	private void addAndMeasureChild(View childView,int index) {
		LayoutParams lpLayoutParams = childView.getLayoutParams();
		if (lpLayoutParams == null) {
			lpLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(childView, index, lpLayoutParams);
		final int itemWidth = getWidth();
		// 位运算 | itemWidth表示添加此当前值
		childView.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	/**
	 * 对所有子视图进行layout操作，取得所有子视图正确的位置
	 */
	private void positioinItems() {
		int top = firstChildTop + scrolledDistance;
		for (int i = 0; i < getChildCount(); i++) {
			View childView = getChildAt(i);

			int childHeight = childView.getMeasuredHeight();

			childView.layout(0, top, getWidth(), top + childHeight);
			top += childHeight;
		}
	}

	/**
	 * 获取一个可以复用的Item View
	 * @return view 可以复用的视图或者null
	 */
	private View getCachedView() {
		if (mCachedItemViews.size() != 0) {
			return mCachedItemViews.removeFirst();
		}
		return null;
	}

}
