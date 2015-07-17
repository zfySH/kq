package cn.kangeqiu.kq.model;

import java.io.Serializable;

public class MemberInfoModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4982324375963149908L;
	private String id;
	private String icon;
	private String name;
	private int type;// 0：创建人1：普通成员2:增加成员3:删除成员

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
