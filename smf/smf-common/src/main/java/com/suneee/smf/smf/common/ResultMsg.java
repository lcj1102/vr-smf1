package com.suneee.smf.smf.common;
import java.util.List;

public class ResultMsg {
	
	//返回码
	private String code;
	//返回详情
    private String msg;
    //返回数据
    private List<Object> data;
    //备用字段
    private String html;
    
    public ResultMsg() {
	}
    
    public ResultMsg(String code, String msg) {
    	this.code = code;
		this.msg = msg;
	}
    
    public ResultMsg(String code, String msg, List<Object> data) {
    	this.code = code;
		this.msg = msg;
		this.data = data;
	}
    
    public ResultMsg(String code, String msg, List<Object> data, String html) {
    	this.code = code;
		this.msg = msg;
		this.data = data;
		this.html = html;
	}
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<Object> getData() {
		return data;
	}
	public void setData(List<Object> data) {
		this.data = data;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}
    
}
