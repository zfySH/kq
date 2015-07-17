package com.easemob.applib.model;

public class TeamFightItemModel {
	private String code;
	private String name;

	public TeamFightItemModel(String code, String name) {
		this.setCode(code);
		this.setName(name);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
