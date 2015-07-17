package com.nowagme.football;

import cn.kangeqiu.kq.R;

public class BeanSysMsg {

	// me_type
	public final static int TYPE_JOIN_TEAM = 0;// 申请加入球队
	public final static int TYPE_ATTENTION_PLAYER = 1;// 关注某人
	public final static int TYPE_MAKE_DUEL = 2;// 发起约战
	public final static int TYPE_ENROLL_DUEL = 3;// 约战报名
	public final static int TYPE_CONFIRM_DUEL = 4;// 确认比分
	public final static int TYPE_REFUSE_DUEL = 5;// 拒绝比分
	public final static int TYPE_ACCEPT_DUEL = 6;// 同意比分
	public final static int TYPE_TRANNING = 7;// 收到队长发起队内训练活动的通知
	public final static int TYPE_DUEL_RESPONSE = 8;// 队长响应约战后约战球队双方收到的系统通知
	public final static int TYPE_MATCH_CANCEL = 9;// 活动取消后，参与活动的成员收到的系统通知
	public final static int TYPE_JOIN_TEAM_OK = 10;// 欢迎加入球队
	public final static int TYPE_WELLCOME = 11;// 欢迎加入猎豹足球.
	public final static int TYPE_TEAM_FIRED = 12;// 从球队中开除球员.
	public final static int TYPE_DUEL_RESPONSE_CAPTAIN = 13;// 响应约战后约战发起人收到的系统消息

	// me_state
	public final static int STATE_NEW = 0;// 新
	public final static int STATE_SENT = 1;// 服务器已发送
	public final static int STATE_READED = 2;// 客户端已阅读
	public final static int STATE_DELETED = 3;// 客户端删除该消息
	public final static int STATE_DONE = 4;// 消息已处理

	// 系统消息的图标
	public final static int[] SYSTEM_MSG_ICONS = { R.drawable.logo,
			R.drawable.abc_system_msg_1, R.drawable.abc_system_msg_2,
			R.drawable.abc_system_msg_3 };
	/**
	 * 消息ID
	 */
	private int id;
	/**
	 * 消息正文
	 */
	private String body;
	/**
	 * 消息类型
	 */
	private int type;

	/**
	 * 消息关键字
	 */
	private String key;

	/**
	 * 消息状态
	 */
	private int state;
	/**
	 * 消息时间
	 */
	private String time;
	/**
	 * 消息发送者
	 */
	private int sendor;

	/**
	 * 消息发送者头像
	 */
	private String sendor_icon;

	/**
	 * 组
	 */
	private int group;

	/**
	 * 标题
	 */
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public String getSendor_icon() {
		return sendor_icon;
	}

	public void setSendor_icon(String sendor_icon) {
		this.sendor_icon = sendor_icon;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getSendor() {
		return sendor;
	}

	public void setSendor(int sendor) {
		this.sendor = sendor;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public BeanSysMsg(int id, String body, int type, String key, int state,
			String time, int sendor, String sendor_icon, int group, String title) {
		this.id = id;
		this.body = body;
		this.type = type;
		this.key = key;
		this.state = state;
		this.time = time;
		this.sendor = sendor;
		this.sendor_icon = sendor_icon;
		this.group = group;
		this.title = title;
	}

	public BeanSysMsg() {

	}

}
