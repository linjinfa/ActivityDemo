package com.linjf.demo.view.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.linjf.demo.R;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月26日 下午1:24:05 
 */
public class ListView3DAdapter extends BaseAdapter{
	
	private List<String> dataList = new ArrayList<String>();
	private LayoutInflater inflater;
	
	public ListView3DAdapter(Context context) {
		super();
		inflater = LayoutInflater.from(context);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		for(int i=0;i<20;i++){
			dataList.add("林金发 "+i);
		}
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_3d_item, null);
			viewHolder.txtView = (TextView) convertView.findViewById(R.id.txtView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.txtView.setText(dataList.get(position));
		return convertView;
	}
	
	class ViewHolder{
		public TextView txtView;
	}

	
}
