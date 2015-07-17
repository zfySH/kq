package com.easemob.applib.model;

public class TeamFightModel {
	private String id;
	private String name;
	private String content;
	private String hint;
	private int drawable = 0;
	private boolean isEdit = true;
	private String type;

	private String lat;
	private String lng;
	private String map_address;
	private String map_name;

	public TeamFightModel(String type, String name, String content) {
		this(type, name, content, 0, true, null, "");
	}

	public TeamFightModel(String type, String name, String content,
			boolean isEdit, String hint, String lat, String lng,
			String map_address, String map_name) {
		this(type, name, content, 0, isEdit, null, hint);
		this.lat = lat;
		this.lng = lng;
		this.map_address = map_address;
		this.map_name = map_name;
	}

	public TeamFightModel(String type, String name, String content,
			boolean isEdit, String hint) {
		this(type, name, content, 0, isEdit, null, hint);

	}

	public TeamFightModel(String type, String name, boolean isEdit, String id) {
		this(type, name, "", 0, isEdit, id, "");
	}

	public TeamFightModel(String type, String name, String content,
			int drawable, boolean isEdit) {
		this(type, name, content, drawable, isEdit, null, "");
	}

	public TeamFightModel(String type, String name, String content,
			int drawable, boolean isEdit, String id, String hint) {
		this.type = type;
		this.name = name;
		this.content = content;
		this.drawable = drawable;
		this.id = id;
		this.hint = hint;
		this.setEdit(isEdit);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
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

	public String getMap_address() {
		return map_address;
	}

	public void setMap_address(String map_address) {
		this.map_address = map_address;
	}

	public String getMap_name() {
		return map_name;
	}

	public void setMap_name(String map_name) {
		this.map_name = map_name;
	}
}
