package com.nowagme.util;

public class WebCallResultUtil {

	/**
	 * call if right.
	 * true is right,false is error when call.
	 */
	private boolean callRight;
	/**
	 * response text.
	 */
	private String responseText;
	
	/**
	 * 构造函数.
	 */
	public WebCallResultUtil(){}
			
	/**
	 * 构造函数.
	 * @param callRight
	 * @param responseText
	 */
	public WebCallResultUtil(boolean callRight,String responseText){
		this.callRight = callRight;
		this.responseText = responseText;
	}
	
	public boolean isCallRight() {
		return callRight;
	}



	public void setCallRight(boolean callRight) {
		this.callRight = callRight;
	}



	public String getResponseText() {
		return responseText;
	}



	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
