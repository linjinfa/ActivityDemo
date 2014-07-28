package com.linjf.demo.util.pojo;

import java.io.Serializable;

/**
 * 
 * @author linjinfa 331710168@qq.com
 * @date 2014年6月11日
 */
public class RouteGroup implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String center;
	private String mustgo;
	private String groupname;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getMustgo() {
		return mustgo;
	}
	public void setMustgo(String mustgo) {
		this.mustgo = mustgo;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

}
