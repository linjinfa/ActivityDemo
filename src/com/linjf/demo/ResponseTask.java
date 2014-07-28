package com.linjf.demo;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.HEAD;
import retrofit.http.POST;
import retrofit.http.PUT;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linjf.demo.util.HLGsonConverter;
import com.linjf.demo.util.ITourService;
import com.linjf.demo.util.pojo.Scenic;


/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月3日 下午4:09:00 
 */
public class ResponseTask implements Runnable{
	
	private String rootUrl = ITourService.ROOT_URL;
	private String url;
	/**
	 * 返回值类型
	 */
	private Type returnType = TypeToken.get(String.class).getType();
	private Map<String,String> headerMap;
	
	public ResponseTask(String rootUrl, String url, Type returnType) {
		super();
		this.rootUrl = rootUrl;
		this.url = url;
		this.returnType = returnType;
	}
	
	public ResponseTask(String url, Type returnType) {
		super();
		this.url = url;
		this.returnType = returnType;
	}

	@Override
	public void run() {
		try {
			Method iMethod = null;
			for(Method method : ITourService.class.getMethods()){
				if(method.getDeclaringClass() != ITourService.class)
					continue;
				String urlValue = getUrlByAnnotation(method);
				if(urlValue==null || "".equals(urlValue)){
					throw new RuntimeException("url地址为空!!");
				}
				if(urlValue.equalsIgnoreCase(url) && returnType!=null && method.getGenericReturnType().toString().equals(returnType.toString())){
					iMethod = method;
					break;
				}
			}
			if(iMethod==null){
				throw new RuntimeException("找不到地址 "+(rootUrl+url)+" 对应的接口!!");
			}
			
			RestAdapter restAdapter = new RestAdapter.Builder().setRequestInterceptor(new RequestInterceptor() {
				@Override
				public void intercept(RequestFacade requestFacade) {
					if(headerMap!=null){
						for(Map.Entry<String, String> entry : headerMap.entrySet()){
							requestFacade.addHeader(entry.getKey(), entry.getValue());
						}
					}
				}
			}).setLogLevel(LogLevel.NONE).setConverter(new HLGsonConverter(new Gson(),returnType.toString().equals("class java.lang.String"))).setEndpoint(rootUrl).build();
			ITourService tourService = restAdapter.create(ITourService.class);
			
			Object value = iMethod.invoke(tourService);
System.out.println("value=======> "+value);
		} catch (RetrofitError e) {
System.out.println("异常====》"+e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
System.out.println("aaaaaaaaaaa======== "+e.getCause()+"  "+((RetrofitError)e.getCause()).isNetworkError());
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取注解的url地址
	 * @param method
	 * @return
	 */
	private String getUrlByAnnotation(Method method){
		Annotation[] annotations = method.getAnnotations();
		if(annotations!=null && annotations.length!=0){
			for(Annotation annotation : annotations){
				if(annotation instanceof GET){
					return ((GET)annotation).value();
				}else if(annotation instanceof POST){
					return ((POST)annotation).value();
				}else if(annotation instanceof HEAD){
					return ((HEAD)annotation).value();
				}else if(annotation instanceof PUT){
					return ((PUT)annotation).value();
				}else if(annotation instanceof DELETE){
					return ((DELETE)annotation).value();
				}
			}
		}
		return null;
	}

	private void test(){
		try {
			long t1 = System.currentTimeMillis();
			RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new HLGsonConverter(new Gson(),false)).setLogLevel(LogLevel.FULL).setEndpoint(ITourService.ROOT_URL).build();
			ITourService tourService = restAdapter.create(ITourService.class);
			List<Scenic> scenicList = tourService.getLandscapes();
			long t2 = System.currentTimeMillis();
			System.out.println(" 消耗事假====》  "+(t2-t1));
//			System.out.println("k=aaaaa=======> "+tourService.landscapes());;
		} catch (RetrofitError e) {
			System.out.println("异常====》"+e);
			e.printStackTrace();
//			System.out.println("asdasdasdassdasdasd=======>  "+e.isNetworkError()+"  "+e.getMessage()+"  "+e.getResponse().getStatus());
//			try {
//				String ss = InputStreamUtil.InputStreamTOStringUTF8(e.getResponse().getBody().in());
//				System.out.println("kkkkk======>  "+ss);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
		}

	}
	
}
