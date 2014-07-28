package com.linjf.demo.util.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author linjinfa 331710168@qq.com
 * @date 2014年6月10日
 */
public class RouteSteps implements Serializable{

	private static final long serialVersionUID = 1L;
	private String groupname;
	private List<RouteGroup> group;
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public List<RouteGroup> getGroup() {
		return group;
	}
	public void setGroup(List<RouteGroup> group) {
		this.group = group;
	}
}
