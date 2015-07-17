package com.nowagme.football;

public class BeanMyTeam {

	private String title;

	// zhu add
	private String id;
	private String name;
	private String faceimg;
	private String component;
	private String membercount;
	private String place1;
	private String place2;

	private String address;
	private String captain_team_td;
	private String component_desc;

	private String latitude;
	private String longitude;
	
	private String dis;


	public String getDis() {
		return dis;
	}

	public void setDis(String dis) {
		this.dis = dis;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getPlace1() {
		return place1;
	}

	public void setPlace1(String place1) {
		this.place1 = place1;
	}

	public String getPlace2() {
		return place2;
	}

	public void setPlace2(String place2) {
		this.place2 = place2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFaceimg() {
		return faceimg;
	}

	public void setFaceimg(String faceimg) {
		this.faceimg = faceimg;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getMembercount() {
		return membercount;
	}

	public void setMembercount(String membercount) {
		this.membercount = membercount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BeanMyTeam(String title) {
		this.title = title;
	}

	public BeanMyTeam() {
	}

	public String getCaptain_team_td() {
		return captain_team_td;
	}

	public void setCaptain_team_td(String captain_team_td) {
		this.captain_team_td = captain_team_td;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComponent_desc() {
		return component_desc;
	}

	public void setComponent_desc(String component_desc) {
		this.component_desc = component_desc;
	}

}
