package com.linjf.demo.util;

import android.util.SparseArray;
import android.view.View;

/**
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月24日 下午12:52:40
 */
@SuppressWarnings("unchecked")
public class ViewHolderUtil {

	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
