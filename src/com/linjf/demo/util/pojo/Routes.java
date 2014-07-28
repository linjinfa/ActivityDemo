package com.linjf.demo.util.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author linjinfa 331710168@qq.com
 * @date 2014年6月10日
 */
public class Routes implements Serializable{

	private static final long serialVersionUID = 1L;
	private String title;
	private List<RouteSteps> steps;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<RouteSteps> getSteps() {
		return steps;
	}
	public void setSteps(List<RouteSteps> steps) {
		this.steps = steps;
	}

}
