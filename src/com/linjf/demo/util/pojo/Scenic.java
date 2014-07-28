package com.linjf.demo.util.pojo;

import java.io.Serializable;

/** 
 * @author linjinfa 331710168@qq.com 
 * @version 创建时间：2014年7月2日 下午4:46:52 
 */
public class Scenic implements Serializable{

	private static final long serialVersionUID = 1L;
	private String province_pinyin;
	private String brief_intro;
	public String getProvince_pinyin() {
		return province_pinyin;
	}
	public void setProvince_pinyin(String province_pinyin) {
		this.province_pinyin = province_pinyin;
	}
	public String getBrief_intro() {
		return brief_intro;
	}
	public void setBrief_intro(String brief_intro) {
		this.brief_intro = brief_intro;
	}

}
