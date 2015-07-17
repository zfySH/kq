package com.nowagme.football;

public class DemoBean {
	
	
	

	private String title;

	/**
	 * 是否选中
	 */
	private boolean hasSelected = false;
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	

	public boolean isHasSelected() {
		return hasSelected;
	}

	public void setHasSelected(boolean hasSelected) {
		this.hasSelected = hasSelected;
	}

	public DemoBean(String title,boolean hasSelected) {
		this.title = title;
		this.hasSelected = hasSelected;
	}

	public DemoBean() {
	}

}
