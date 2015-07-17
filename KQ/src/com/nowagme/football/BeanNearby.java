package com.nowagme.football;

public class BeanNearby {

	public final static int TYPE_PERSON = 0;
	public final static int TYPE_TEAM = 1;
	public final static int TYPE_COURT = 2;
	
	public final static int TYPE_COURT_002 = 3;

	/**
	 * 数据类型. 0=球员,1=球队,2=球场
	 */
	private int type;
	/**
	 * 个人、球场、球队的ID
	 */
	private int id;
	/**
	 * 个人、球场、球队的头像.
	 */
	private String faceimg;
	/**
	 * 个人、球场、球队的名称.
	 */
	private String name;
	/**
	 * 个人、球场、球队离我的距离.
	 */
	private String dis;
	/**
	 * 个人、球场、球队离我的距离的时间点
	 */
	private String time;
	/**
	 * 球员的年龄
	 */
	private int age;
	/**
	 * 球员的性别
	 */
	private int sex;

	/**
	 * 球员的场上位置
	 */
	private String place;

	/**
	 * 球场的地址
	 */
	private String courtAddr;
	/**
	 * 球场的星级
	 */
	private int courtStar;

	/**
	 * 球员的球队名称.
	 */
	private String playerTeamName;
	/**
	 * 球员的球队队徽.
	 */
	private String playerTeamFaceimg;

	/**
	 * 球队的人数.
	 */
	private int teamMemberCount;
	/**
	 * 球队的构成.
	 */
	private String component;
	/**
	 * 球队的常出没地址1.
	 */
	private String teamPlace1;
	/**
	 * 球队的常出没地址2.
	 */
	private String teamPlace2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTeamPlace1() {
		return teamPlace1;
	}

	public void setTeamPlace1(String teamPlace1) {
		this.teamPlace1 = teamPlace1;
	}

	public String getTeamPlace2() {
		return teamPlace2;
	}

	public void setTeamPlace2(String teamPlace2) {
		this.teamPlace2 = teamPlace2;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public int getTeamMemberCount() {
		return teamMemberCount;
	}

	public void setTeamMemberCount(int teamMemberCount) {
		this.teamMemberCount = teamMemberCount;
	}

	public String getCourtAddr() {
		return courtAddr;
	}

	public void setCourtAddr(String courtAddr) {
		this.courtAddr = courtAddr;
	}

	public int getCourtStar() {
		return courtStar;
	}

	public void setCourtStar(int courtStar) {
		this.courtStar = courtStar;
	}

	public String getDis() {
		return dis;
	}

	public void setDis(String dis) {
		this.dis = dis;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getPlayerTeamName() {
		return playerTeamName;
	}

	public void setPlayerTeamName(String playerTeamName) {
		this.playerTeamName = playerTeamName;
	}

	public String getPlayerTeamFaceimg() {
		return playerTeamFaceimg;
	}

	public void setPlayerTeamFaceimg(String playerTeamFaceimg) {
		this.playerTeamFaceimg = playerTeamFaceimg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getFaceimg() {
		return faceimg;
	}

	public void setFaceimg(String faceimg) {
		this.faceimg = faceimg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BeanNearby(int id, int type, String name, String faceimg,
			String playerTeamName, String playerTeamFaceimg, int age, int sex,
			String place, String dis, String time, String courtAddr,
			int courtStar, int teamMemberCount, String component,
			String teamPlace1, String teamPlace2) {
		this.id = id;
		this.faceimg = faceimg;
		this.type = type;
		this.name = name;
		this.playerTeamName = playerTeamName;
		this.playerTeamFaceimg = playerTeamFaceimg;
		this.age = age;
		this.sex = sex;
		this.place = place;
		this.dis = dis;
		this.time = time;
		this.courtAddr = courtAddr;
		this.courtStar = courtStar;
		this.teamMemberCount = teamMemberCount;
		this.component = component;
		this.teamPlace1 = teamPlace1;
		this.teamPlace2 = teamPlace2;
	}

	public BeanNearby() {
	}

}
