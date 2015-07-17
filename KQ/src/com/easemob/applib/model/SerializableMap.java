package com.easemob.applib.model;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8154624579743237904L;
	private String id;
	private String icon;
	private String nickname;
	private String sex;
	private String age;
	private Map<String, String> province;
	private Map<String, String> city;
	private String edit_state;
	private String content_count;
	private String attention_count;
	private String fun_count;
	private String score;
	private String attention_state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Map<String, String> getProvince() {
		return province;
	}

	public void setProvince(Map<String, String> province) {
		this.province = province;
	}

	public Map<String, String> getCity() {
		return city;
	}

	public void setCity(Map<String, String> city) {
		this.city = city;
	}

	public String getEdit_state() {
		return edit_state;
	}

	public void setEdit_state(String edit_state) {
		this.edit_state = edit_state;
	}

	public String getContent_count() {
		return content_count;
	}

	public void setContent_count(String content_count) {
		this.content_count = content_count;
	}

	public String getAttention_count() {
		return attention_count;
	}

	public void setAttention_count(String attention_count) {
		this.attention_count = attention_count;
	}

	public String getFun_count() {
		return fun_count;
	}

	public void setFun_count(String fun_count) {
		this.fun_count = fun_count;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getAttention_state() {
		return attention_state;
	}

	public void setAttention_state(String attention_state) {
		this.attention_state = attention_state;
	}

}
