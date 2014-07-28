package com.linjf.demo.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.linjf.demo.R;

/**
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月25日 下午3:07:15
 */
public class SearchWindow {

	private Context context;
	private WindowManager windowManager;
	private View contentView;

	public SearchWindow(Context context) {
		super();
		this.context = context;
		windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		contentView = LayoutInflater.from(context).inflate(
				R.layout.search_view2, null);
		Button cancelBtn = (Button) contentView.findViewById(R.id.cancelBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				windowManager.removeView(contentView);
			}
		});
	}

	private WindowManager.LayoutParams createPopupLayout() {
		WindowManager.LayoutParams windowParameters = new WindowManager.LayoutParams();
		windowParameters.gravity = Gravity.TOP | Gravity.LEFT;
		windowParameters.height = WindowManager.LayoutParams.MATCH_PARENT;
		windowParameters.width = WindowManager.LayoutParams.MATCH_PARENT;
		windowParameters.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		windowParameters.format = PixelFormat.TRANSLUCENT;
		return windowParameters;
	}

	public void show() {
		WindowManager.LayoutParams lpLayoutParams = createPopupLayout();
		windowManager.addView(contentView, lpLayoutParams);
		ScrollAnim scrollAnim = new ScrollAnim(0, 0);
		scrollAnim.setDuration(500);
		contentView.startAnimation(scrollAnim);
	}
	
	class ScrollAnim extends Animation {

		private int fromTop;
		private int toTop;

		public ScrollAnim(int fromTop, int toTop) {
			super();
			this.fromTop = fromTop;
			this.toTop = toTop;
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			int top = (int) (fromTop + (toTop - fromTop) * interpolatedTime);
			
			WindowManager.LayoutParams lpLayoutParams = (WindowManager.LayoutParams) contentView.getLayoutParams();
			lpLayoutParams.y = 100;
			windowManager.updateViewLayout(contentView, lpLayoutParams);
		}

	}

}
