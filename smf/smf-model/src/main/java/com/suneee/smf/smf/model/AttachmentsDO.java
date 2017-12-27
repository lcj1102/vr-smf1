package com.suneee.smf.smf.model;

import java.util.Date;

public class AttachmentsDO {
	private Long pk_attachments;
	private Long sourcebillid;
	private String sourcebilltype;
	private String code;
	private String name;
	private String url;
	private String type;
	private String size;
	private String state;
	private Long inputmanid;
	private String inputmanname;
	private Date bookindate;
	private Long modiferid;
	private String modifername;
	private Date modefydate;
	private Long cancelid;
	private String cancelname;
	private Date canceldate;
	private Long enterpriseid;
	private Date time_stamp;
	public Long getPk_attachments() {
		return pk_attachments;
	}
	public void setPk_attachments(Long pk_attachments) {
		this.pk_attachments = pk_attachments;
	}
	public Long getSourcebillid() {
		return sourcebillid;
	}
	public void setSourcebillid(Long sourcebillid) {
		this.sourcebillid = sourcebillid;
	}
	public String getSourcebilltype() {
		return sourcebilltype;
	}
	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Long getInputmanid() {
		return inputmanid;
	}
	public void setInputmanid(Long inputmanid) {
		this.inputmanid = inputmanid;
	}
	public String getInputmanname() {
		return inputmanname;
	}
	public void setInputmanname(String inputmanname) {
		this.inputmanname = inputmanname;
	}
	public Date getBookindate() {
		return bookindate;
	}
	public void setBookindate(Date bookindate) {
		this.bookindate = bookindate;
	}
	public Long getModiferid() {
		return modiferid;
	}
	public void setModiferid(Long modiferid) {
		this.modiferid = modiferid;
	}
	public String getModifername() {
		return modifername;
	}
	public void setModifername(String modifername) {
		this.modifername = modifername;
	}
	public Date getModefydate() {
		return modefydate;
	}
	public void setModefydate(Date modefydate) {
		this.modefydate = modefydate;
	}
	public Long getCancelid() {
		return cancelid;
	}
	public void setCancelid(Long cancelid) {
		this.cancelid = cancelid;
	}
	public String getCancelname() {
		return cancelname;
	}
	public void setCancelname(String cancelname) {
		this.cancelname = cancelname;
	}
	public Date getCanceldate() {
		return canceldate;
	}
	public void setCanceldate(Date canceldate) {
		this.canceldate = canceldate;
	}
	public Long getEnterpriseid() {
		return enterpriseid;
	}
	public void setEnterpriseid(Long enterpriseid) {
		this.enterpriseid = enterpriseid;
	}
	public Date getTime_stamp() {
		return time_stamp;
	}
	public void setTime_stamp(Date time_stamp) {
		this.time_stamp = time_stamp;
	}

}
