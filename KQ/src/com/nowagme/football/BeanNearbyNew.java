package com.nowagme.football;

public class BeanNearbyNew {
	

	//记录类型
	public final static int TYPE_PERSON = 0;
	public final static int TYPE_TEAM = 1;
	public final static int TYPE_COURT = 2;
	public final static int TYPE_ACTIVITY = 3;
	
	//活动类型
	//分类：0=挑战（对手已确定）,1=约战（对手未确定），2=队内训练，20=个人活动
	public static final int KIND_CHALLENGE=0;//挑战（对手已确定）
	public static final int KIND_PUBLISH=1;//约战（对手未确定）
	public static final int KIND_TRANNING=2;//队内训练
	public static final int KIND_PERSONAL=20;//个人活动

	/*
	 * “type”:”记录类型 0=球员，1=球队，2球场，3=活动”, 
	 * “id”:”球员/球队/球场/活动编号”, 
	 * “name”:“球员昵称/球队名称/球场名称/个人活动或队内训练的主题”,
	 * ”icon”:”球员头像/球队队徽／球场icon/活动类型(0=挑战（对手已确定），1=约战（对手未确定），2=队内训练，20=个人活动)” ,
	 * "dis":"距离", 
	 * "time":"球员末次定位时间/活动开始时间",
	 * ”age”:”球员年龄／球队人数／球场星级/队内训练或挑战或约战的发起方球队名称”,
	 * ”sex”:”球员性别/球队成份描述/挑战或约战的响应方球队名称”, 
	 * “tag”:”球员标签／球队成份”,
	 * ”addr”:”球员最后加入或者是队长的球队名称/球队常驻地/球场地址/活动地址”
	 */
	/**
	 * “type”:”记录类型 0=球员，1=球队，2球场，3=活动”
	 */
	private int type;
	/**
	 * “id”:”球员/球队/球场/活动编号”
	 */
	private int id;
	/**
	 * “name”:“球员昵称/球队名称/球场名称/个人活动或队内训练的主题”
	 */
	private String name;
	/**
	 * ”icon”:”球员头像/球队队徽／球场icon/活动类型(0=挑战（对手已确定），1=约战（对手未确定），2=队内训练，20=个人活动)” 
	 */
	private String icon;
	
	/**
	 * "dis":"距离"
	 */
	private String dis;
	/**
	 * "time":"球员末次定位时间/活动开始时间"
	 */
	private String time;
	/**
	 * ”age”:”球员年龄／球队人数／球场星级/队内训练或挑战或约战的发起方球队名称”
	 */
	private String age;
	/**
	 * ”sex”:”球员性别/球队成份描述/挑战或约战的响应方球队名称”
	 */
	private String sex;

	/**
	 * “tag”:”球员标签／球队成份/活动参与人数”
	 */
	private String tag;

	/**
	 * ”addr”:”球员最后加入或者是队长的球队名称/球队常驻地/球场地址/活动地址”
	 */
	private String addr;
	

	/**
	 * “state”:”活动状态(0=未结束,1=已结束)”
	 */
	private String state;
	
	/**
	 * “creator”:”活动发起人”
	 */
	private String creator;
	
	

	public String getCreator() {
		return creator;
	}





	public void setCreator(String creator) {
		this.creator = creator;
	}





	public String getState() {
		return state;
	}





	public void setState(String state) {
		this.state = state;
	}





	public int getType() {
		return type;
	}





	public void setType(int type) {
		this.type = type;
	}





	public int getId() {
		return id;
	}





	public void setId(int id) {
		this.id = id;
	}





	public String getName() {
		return name;
	}





	public void setName(String name) {
		this.name = name;
	}





	public String getIcon() {
		return icon;
	}





	public void setIcon(String icon) {
		this.icon = icon;
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





	public String getAge() {
		return age;
	}





	public void setAge(String age) {
		this.age = age;
	}





	public String getSex() {
		return sex;
	}





	public void setSex(String sex) {
		this.sex = sex;
	}





	public String getTag() {
		return tag;
	}





	public void setTag(String tag) {
		this.tag = tag;
	}





	public String getAddr() {
		return addr;
	}





	public void setAddr(String addr) {
		this.addr = addr;
	}


	



	public BeanNearbyNew(int type, int id, String name, String icon,
			String dis, String time, String age, String sex, String tag,
			String addr,String state,String creator) {
		super();
		this.type = type;
		this.id = id;
		this.name = name;
		this.icon = icon;
		this.dis = dis;
		this.time = time;
		this.age = age;
		this.sex = sex;
		this.tag = tag;
		this.addr = addr;
		this.state = state;
		this.creator = creator;
	}

}
