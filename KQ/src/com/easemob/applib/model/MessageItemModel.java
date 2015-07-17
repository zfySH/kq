package com.easemob.applib.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageItemModel {
	private String id;
	private String name;
	private String content;
	private int drawable = 0;
	private boolean isShow = true;
	private String type;
	private List<Map<String, String>> photos = new ArrayList<Map<String, String>>();

	private String iconUrl;

	private String lat;
	private String lng;

	public MessageItemModel(String type, String name,
			List<Map<String, String>> photos, String icon) {
		this(type, name, "", 0, true, null);
		this.setPhotos(photos);
		this.setIconUrl(icon);
	}

	public MessageItemModel(String type, String name, String address,
			String icon) {
		this(type, name, address, 0, true, null);
		this.setIconUrl(icon);
	}

	public MessageItemModel(String type, String name, String content) {
		this(type, name, content, 0, true, null);
	}

	public MessageItemModel(String type, String name, String content,
			boolean isShow) {
		this(type, name, content, 0, isShow, null);

	}

	public MessageItemModel(String type, String name, boolean isShow, String id) {
		this(type, name, "", 0, isShow, id);
	}

	public MessageItemModel(String type, String name, String content,
			int drawable, boolean isShow) {
		this(type, name, content, drawable, isShow, null);
	}

	public MessageItemModel(String type, String name, String content,
			int drawable, boolean isShow, String id) {
		this.type = type;
		this.name = name;
		this.content = content;
		this.drawable = drawable;
		this.id = id;
		this.setShow(isShow);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Map<String, String>> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Map<String, String>> photos) {
		this.photos = photos;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

}
