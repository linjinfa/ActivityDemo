package com.linjf.demo.util;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

import com.google.gson.Gson;

/**
 * @author linjinfa 331710168@qq.com
 * @version 创建时间：2014年7月2日 下午4:18:36
 */
public class HLGsonConverter extends GsonConverter {
	
	private boolean isString;
	
	public HLGsonConverter(Gson gson, String encoding, boolean isString) {
		super(gson, encoding);
		this.isString = isString;
	}

	public HLGsonConverter(Gson gson,boolean isString) {
		super(gson);
		this.isString = isString;
	}

	@Override
	public Object fromBody(TypedInput body, Type type)
			throws ConversionException {

		try {
			if(isString){
				return InputStreamUtil.InputStreamTOStringUTF8(body.in());
			}
System.out.println("atype=========>  "+type);
			JsonUtil jsonUtil = new JsonUtil("data", type);
			return jsonUtil.readArrayJsonByStream(body.in());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
