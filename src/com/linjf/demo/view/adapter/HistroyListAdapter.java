package com.linjf.demo.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linjf.demo.R;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月25日 上午11:07:46 
 */
public class HistroyListAdapter extends BaseAdapter{

	private List<String> dataList = new ArrayList<String>();
	private Drawable leftDrawable;
	private Context context;
	
	public HistroyListAdapter(Context context) {
		super();
		this.context = context;
		leftDrawable = context.getResources().getDrawable(R.drawable.icon_histroy);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public String getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<String> getDataList() {
		return dataList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			
			convertView = new LinearLayout(context); 
			convertView.setMinimumHeight(80);
			
			viewHolder.txtView = new TextView(context);;
			viewHolder.txtView.setTextSize(17);
			viewHolder.txtView.setTextColor(Color.parseColor("#a3a39f"));
			viewHolder.txtView.setCompoundDrawablePadding(20);
			
			LinearLayout.LayoutParams lpLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			lpLayoutParams.leftMargin = 30;
			viewHolder.txtView.setLayoutParams(lpLayoutParams);
			((LinearLayout)convertView).addView(viewHolder.txtView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(getCount()!=0 && position==getCount()-1){
			viewHolder.txtView.setGravity(Gravity.CENTER);
			viewHolder.txtView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}else{
			viewHolder.txtView.setGravity(Gravity.CENTER_VERTICAL);
			viewHolder.txtView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
		}
		viewHolder.txtView.setText(dataList.get(position));
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtView;
	}

}
