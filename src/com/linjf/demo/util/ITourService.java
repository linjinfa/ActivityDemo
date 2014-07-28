package com.linjf.demo.util;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;

import com.linjf.demo.util.pojo.Scenic;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月2日 下午3:27:25 
 */
public interface ITourService {
	
	public final static String ROOT_URL = "http://tours.api.ihuilian.com";
	
	@GET(ServerInfo.LANDSCAPES)
	void landscapes(Callback<Scenic> callback);
	
	@GET(ServerInfo.LANDSCAPES)
	String landscapes() throws RetrofitError;
	
	@GET(ServerInfo.LANDSCAPES)
	List<Scenic> getLandscapes() throws RetrofitError;
	
}
