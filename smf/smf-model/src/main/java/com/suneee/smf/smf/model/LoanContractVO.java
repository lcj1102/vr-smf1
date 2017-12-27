package com.suneee.smf.smf.model;

import java.util.Date;

public class LoanContractVO 
{
	private Long pk_loan_contract;//长期借款合同主键
	private String code;//长期借款合同号
	private String name;//长期借款合同名称
	private Long pk_enterprise;//借款企业主键
	private String enterprise_code;//借款企业编码
	private String enterprise_name;//借款企业名称
	private Date busi_date;//合同签订日期
	private Double loan_limit;//借款限额
	private Long pk_currency;//币种主键
	private String currency_name;//币种名称
	private Double interest_rate;//年化利率(%)
	private String address;//借款企业注册地址
	private String credit_code;//借款企业统一社会信用代码
	private String contact;//借款企业联系人
	private String contact_number;//借款企业联系电话
	private String state;//状态
	private Long inputmanid;//登记人主键
	private String inputmanname;//登记人姓名
	private Date bookindate;//登记日期
	private Long modiferid;//修改人主键
	private String modifername;//修改人姓名
	private Date modefydate;//最后修改日期
	private Long cancelid;//作废人主键
	private String cancelname;//作废人姓名
	private Date canceldate;//作废日期
	private Long enterpriseid;//业务组织主键
	private Date time_stamp;//时间戳
	
	private Date starttime;//时间戳
	private Date endtime;//时间戳
	
	/**
	 * 关键字查询
	 */
	private String searchValue;
	
	/**
	 * 用以区别保存操作（save）、保存并提交操作（submit）
	 */
	private String saveType;
	
	
	/**
	 * 审核类型：通过（agree）、退回（disagree）、拒绝（refuse）、终止（end）
	 */
	private String type;
	/**
	 * 审核意见
	 */
	private String option;
	
	
	public Long getPk_loan_contract()
	{
		return pk_loan_contract;
	}
	public void setPk_loan_contract(Long pk_loan_contract)
	{
		this.pk_loan_contract = pk_loan_contract;
	}
	public String getCode()
	{
		return code;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public Long getPk_enterprise()
	{
		return pk_enterprise;
	}
	public void setPk_enterprise(Long pk_enterprise)
	{
		this.pk_enterprise = pk_enterprise;
	}
	public String getEnterprise_name()
	{
		return enterprise_name;
	}
	public void setEnterprise_name(String enterprise_name)
	{
		this.enterprise_name = enterprise_name;
	}
	public Date getBusi_date()
	{
		return busi_date;
	}
	public void setBusi_date(Date busi_date)
	{
		this.busi_date = busi_date;
	}
	public Double getLoan_limit()
	{
		return loan_limit;
	}
	public void setLoan_limit(Double loan_limit)
	{
		this.loan_limit = loan_limit;
	}
	public Long getPk_currency()
	{
		return pk_currency;
	}
	public void setPk_currency(Long pk_currency)
	{
		this.pk_currency = pk_currency;
	}
	public String getCurrency_name()
	{
		return currency_name;
	}
	public void setCurrency_name(String currency_name)
	{
		this.currency_name = currency_name;
	}
	public Double getInterest_rate()
	{
		return interest_rate;
	}
	public void setInterest_rate(Double interest_rate)
	{
		this.interest_rate = interest_rate;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String address)
	{
		this.address = address;
	}
	public String getCredit_code()
	{
		return credit_code;
	}
	public void setCredit_code(String credit_code)
	{
		this.credit_code = credit_code;
	}
	public String getContact()
	{
		return contact;
	}
	public void setContact(String contact)
	{
		this.contact = contact;
	}
	public String getContact_number()
	{
		return contact_number;
	}
	public void setContact_number(String contact_number)
	{
		this.contact_number = contact_number;
	}
	public String getState()
	{
		return state;
	}
	public void setState(String state)
	{
		this.state = state;
	}
	public Long getInputmanid()
	{
		return inputmanid;
	}
	public void setInputmanid(Long inputmanid)
	{
		this.inputmanid = inputmanid;
	}
	public String getInputmanname()
	{
		return inputmanname;
	}
	public void setInputmanname(String inputmanname)
	{
		this.inputmanname = inputmanname;
	}
	public Date getBookindate()
	{
		return bookindate;
	}
	public void setBookindate(Date bookindate)
	{
		this.bookindate = bookindate;
	}
	public Long getModiferid()
	{
		return modiferid;
	}
	public void setModiferid(Long modiferid)
	{
		this.modiferid = modiferid;
	}
	public String getModifername()
	{
		return modifername;
	}
	public void setModifername(String modifername)
	{
		this.modifername = modifername;
	}
	public Date getModefydate()
	{
		return modefydate;
	}
	public void setModefydate(Date modefydate)
	{
		this.modefydate = modefydate;
	}
	public Long getCancelid()
	{
		return cancelid;
	}
	public void setCancelid(Long cancelid)
	{
		this.cancelid = cancelid;
	}
	public String getCancelname()
	{
		return cancelname;
	}
	public void setCancelname(String cancelname)
	{
		this.cancelname = cancelname;
	}
	public Date getCanceldate()
	{
		return canceldate;
	}
	public void setCanceldate(Date canceldate)
	{
		this.canceldate = canceldate;
	}
	public Long getEnterpriseid()
	{
		return enterpriseid;
	}
	public void setEnterpriseid(Long enterpriseid)
	{
		this.enterpriseid = enterpriseid;
	}
	public Date getTime_stamp()
	{
		return time_stamp;
	}
	public void setTime_stamp(Date time_stamp)
	{
		this.time_stamp = time_stamp;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getOption()
	{
		return option;
	}
	public void setOption(String option)
	{
		this.option = option;
	}
	public Date getStarttime()
	{
		return starttime;
	}
	public void setStarttime(Date starttime)
	{
		this.starttime = starttime;
	}
	public Date getEndtime()
	{
		return endtime;
	}
	public void setEndtime(Date endtime)
	{
		this.endtime = endtime;
	}
	public String getSearchValue()
	{
		return searchValue;
	}
	public void setSearchValue(String searchValue)
	{
		this.searchValue = searchValue;
	}
	public String getSaveType()
	{
		return saveType;
	}
	public void setSaveType(String saveType)
	{
		this.saveType = saveType;
	}
	public String getEnterprise_code()
	{
		return enterprise_code;
	}
	public void setEnterprise_code(String enterprise_code)
	{
		this.enterprise_code = enterprise_code;
	}
	
}
