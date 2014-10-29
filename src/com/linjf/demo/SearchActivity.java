package com.linjf.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.linjf.demo.view.ListView3D;
import com.linjf.demo.view.adapter.ListView3DAdapter;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月24日 下午3:08:38 
 */
public class SearchActivity extends Activity{
	
	private TextView titleTxtView;
	private ListView3D listView3D;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		titleTxtView = (TextView) findViewById(R.id.titleTxtView);
		listView3D = (ListView3D) findViewById(R.id.listView3D);
	}
	
	private void initData() {
		listView3D.setAdapter(new ListView3DAdapter(this));
	}
	
	public void fillListDataClick(View view){
		initData();
	}

	public void searchClick(View view){
		
	}

}
