package com.linjf.demo.view;

import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linjf.demo.R;
import com.linjf.demo.view.adapter.HistroyListAdapter;

/**
 * 搜索View
 * 
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月24日 下午4:18:21
 */
public class SearchView extends RelativeLayout implements OnClickListener {

	private EditText searchEditTxt;
	private TextView histroyTipTxtView;
	private View histroyFrameLay;
	private ListView histroyListView;
	private Button searchBtn;
	private Button cancelBtn;
	private int mTop;
	private SharedPreferences preferences;

	public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SearchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SearchView(Context context) {
		super(context);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		preferences = getContext().getSharedPreferences("_SearchXML", Activity.MODE_PRIVATE);
		
		LayoutInflater.from(getContext()).inflate(R.layout.search_view, this);
		View searchRelay = findViewById(R.id.searchRelay);
		View searchFramLay = findViewById(R.id.searchFramLay);
		
		searchEditTxt = (EditText) findViewById(R.id.searchEditTxt);
		histroyTipTxtView = (TextView) findViewById(R.id.histroyTipTxtView);
		histroyFrameLay = findViewById(R.id.histroyFrameLay);
		histroyListView = (ListView) findViewById(R.id.histroyListView);
		searchBtn = (Button) findViewById(R.id.searchBtn);
		cancelBtn = (Button) findViewById(R.id.cancelBtn);
		searchBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		initHistroyData();
		searchEditTxt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()!=0 && !"".equals(s.toString())){
					cancelBtn.setText("确定");
				}else{
					cancelBtn.setText("取消");
				}
			}
		});
	}
	
	/**
	 * 初始化历史数据
	 */
	private void initHistroyData(){
		if(isInEditMode()){
			return;
		}
		HistroyListAdapter adapter = new HistroyListAdapter(getContext());
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				HistroyListAdapter adapter = (HistroyListAdapter) histroyListView.getAdapter();
				if(adapter.getCount()==0){
					histroyTipTxtView.setVisibility(View.VISIBLE);
				}else{
					histroyTipTxtView.setVisibility(View.GONE);
				}
			}
		});
		histroyListView.setAdapter(adapter);
		histroyListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				HistroyListAdapter adapter = (HistroyListAdapter) parent.getAdapter();
				if(position==adapter.getCount()-1){
					adapter.getDataList().clear();
					adapter.notifyDataSetChanged();
					preferences.edit().remove("searchHistroy").commit();
				}else{
					searchEditTxt.setText(adapter.getItem(position));
					searchEditTxt.setSelection(searchEditTxt.getText().length());
				}
			}
		});
		String histroyStr = preferences.getString("searchHistroy", "");
		if(!"".equals(histroyStr)){
			String[] histroys = histroyStr.split(",");
			adapter.getDataList().addAll(Arrays.asList(histroys));
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 保存历史记录
	 */
	private void saveHIstroy(){
		HistroyListAdapter adapter = (HistroyListAdapter) histroyListView.getAdapter();
		if(!adapter.getDataList().contains(searchEditTxt.getText().toString())){
			if(adapter.getCount()==0){
				adapter.getDataList().add("清空搜索记录");
			}
			adapter.getDataList().add(0,searchEditTxt.getText().toString());
			adapter.notifyDataSetChanged();
			String str = "";
			for(int i=0;i<adapter.getCount();i++){
				str +=adapter.getDataList().get(i);
				if(i!=adapter.getCount()-1){
					str +=",";
				}
			}
			Editor editor = preferences.edit();
			editor.putString("searchHistroy", str);
			editor.commit();
		}
	}
	
	/**
	 * 显示历史记录
	 */
	private void showHistroyListView(){
		
	}
	
	/**
	 * 显示SearchView
	 */
	private void showSearchView(){
		if (mTop == 0) {
			mTop = getTop();
		}
		ViewGroup.MarginLayoutParams lpLayoutParams = (MarginLayoutParams) getLayoutParams();
		if(lpLayoutParams.topMargin==-mTop){
			return ;
		}
		ScrollAnim scrollAnim = new ScrollAnim(0, -mTop);
		scrollAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				searchBtn.setVisibility(View.INVISIBLE);
				cancelBtn.setVisibility(View.VISIBLE);
				histroyFrameLay.setVisibility(View.VISIBLE);
				searchEditTxt.requestFocus();
				showSoftInput(searchEditTxt);
			}
		});
		scrollAnim.setDuration(200);
		startAnimation(scrollAnim);
	}
	
	/**
	 * 隐藏SearchView
	 */
	private void hiddenSearchView(){
		ViewGroup.MarginLayoutParams lpLayoutParams = (MarginLayoutParams) getLayoutParams();
		if(lpLayoutParams.topMargin==0){
			return ;
		}
		ScrollAnim scrollAnim = new ScrollAnim(-mTop, 0);
		scrollAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				histroyFrameLay.setVisibility(View.GONE);
				searchBtn.setVisibility(View.VISIBLE);
				hiddenSoftInput(searchEditTxt);
				searchEditTxt.clearFocus();
			}
		});
		scrollAnim.setDuration(200);
		startAnimation(scrollAnim);
	}
	
	/**
	 * 显示软键盘 并绑定到指定View
	 * @param context
	 * @param view
	 */
	private void showSoftInput(View view){
		InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(view, 0);
	}
	
	/**
	 * 通过绑定的View 隐藏软键盘
	 * @param context
	 */
	private void hiddenSoftInput(View view){
		if(view.getWindowToken()!=null){
			InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.searchBtn:
			showSearchView();
			break;
		case R.id.cancelBtn:
			if("确定".equals(cancelBtn.getText().toString())){
				saveHIstroy();
			}else{
				hiddenSearchView();
			}
			break;
		}
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
//			layout(getLeft(), top, getRight(), top + getMeasuredHeight());
			
			LinearLayout.LayoutParams lpLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			lpLayoutParams.topMargin = top;
			setLayoutParams(lpLayoutParams);
		}

	}

}
