package com.nowagme.football;

public class FirstFragmentParam {
	
	public FirstFragmentParam(String name, int count) {
		this.name = name;
		this.count = count;
	}
	
	private String name;
	private int count;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	

}
