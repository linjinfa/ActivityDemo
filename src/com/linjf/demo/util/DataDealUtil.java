package com.linjf.demo.util;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.linjf.demo.util.pojo.Routes;


/**
 * 数据处理
 * @author linjinfa 331710168@qq.com
 * @date 2014年6月7日
 */
public class DataDealUtil {
	
	public final static String DB_BPLIST_FILENAME = "db.bplist";
//	public final static String PORTAL_GUIDES_FILENAME = "portal.guides.json";

	private DataDealUtil() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * 路线Routes数据
	 * @param context
	 * @return
	 */
	public static List<Routes> getRoutes(Context context){
		return readListData(context, DB_BPLIST_FILENAME, "routes", new TypeToken<List<Routes>>(){}.getType());
	}
	
	/**
	 * 读取Json数据并解析
	 * @param context
	 * @param fileName
	 * @param jsonTarget
	 * @param type	new TypeToken<List<T>>(){}.getType()
	 * @param cls
	 * @return
	 */
	public static <T> List<T> readListData(Context context,String fileName,String jsonTarget,Type type){
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			JsonUtil<T> jsonUtil = new JsonUtil<T>(jsonTarget, type);
			List<T> dataList = jsonUtil.readArrayJsonByStream(inputStream);
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取Json数据并解析
	 * @param context
	 * @param fileName
	 * @param jsonTarget
	 * @param cls
	 * @return
	 */
	public static <T> T readObjData(Context context,String fileName,String jsonTarget,Class<?> cls){
		try {
			InputStream inputStream = context.getAssets().open(fileName);
			JsonUtil<T> jsonUtil = new JsonUtil<T>(jsonTarget, cls);
			T t = jsonUtil.readObjectJsonStr(inputStream);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取坐标
	 * @param coorStr
	 * @return
	 */
	public static float[] getCoordinate(String coorStr){
		if(TextUtils.isEmpty(coorStr)){
			return null;
		}
		coorStr = coorStr.replaceAll("[{]", "").replaceAll("[}]", "");
		String coorStrs[] = coorStr.split(",");
		if(coorStrs!=null && coorStrs.length==2){
			return new float[]{Float.parseFloat(coorStrs[0]),Float.parseFloat(coorStrs[1])};
		}
		return null;
	}
	
}
