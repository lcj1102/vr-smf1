package com.suneee.smf.smf.common;

public class MsgException extends Exception{
	
	//
	private static final long serialVersionUID = -1492202972773707220L;
	
	private String code;//异常代码
	private String message;//异常信息
	
	public MsgException(String code,String message){
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
