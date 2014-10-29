package com.linjf.demo;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年8月4日 下午2:18:38 
 */
@Rest(rootUrl="http://www.baidu.com",converters={})
public interface RestServiceDemo {
	
	@Get(value="/asd/asd")
	String getHH();
}
