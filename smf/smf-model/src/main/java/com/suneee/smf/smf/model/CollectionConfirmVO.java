package com.suneee.smf.smf.model;

import java.util.Date;

/**
 * Created by suneee on 2017/9/13.
 */
public class CollectionConfirmVO {
    //收款确认单主键
    private Long pk_collection_confirm;
    //收款确认单号
    private String code;
    //付款企业主键
    private Long pk_enterprise_payment;
    //付款企业名称
    private String enterprise_payment_name;
    //付款企业联系人
    private String contact;
    //付款企业联系电话
    private String contact_number;
    //付款方式
    private String payment_method;
    //付款凭证号
    private String  payment_billno;
    //付款银行账号
    private String  payment_account;
    //付款银行
    private String  payment_bank;
    //实际收款金额
    private Double collection_amount;
    //币种主键
    private Long pk_currency;
    //币种名称
    private String currency_name;
    //确认收款日期
    private Date busi_date;
    //来源单据类型
    private String sourcebilltype;
    //来源单据主表主键
    private Long sourcebillid;
    //状态
    private String state;
    //登记人主键
    private Long inputmanid;
    //登记人姓名
    private String inputmanname;
    //登记日期
    private Date bookindate;
    //修改人主键
    private Long modiferid;
    //修改人姓名
    private String  modifername;
    //最后修改日期
    private Date modefydate;
    //作废人主键
    private Long cancelid;
    //作废人姓名
    private String cancelname;
    //作废日期
    private Date canceldate;
    //业务组织主键
    private Long enterpriseid;
    //时间戳
    private Date time_stamp;

    private String type;

    private String option;

    private String searchValue;

    //开始日期
    private Date beginDate;
    //结束日期
    private Date endDate;
    //最低查询放款金额
    private Double beginAmount;
    //最高查询放款金额
    private Double endAmount;

    //归还本金
    private Double repayment_principal;
    //归还利息
    private Double repayment_interest;
    //物流费用
    private Double logistics_cost;
    //监管费用
    private Double supervision_cost;
    //溢出金额
    private Double overflow_amount;
    //记账标识
    private String booked_flag;

    public Long getPk_collection_confirm() {
        return pk_collection_confirm;
    }

    public void setPk_collection_confirm(Long pk_collection_confirm) {
        this.pk_collection_confirm = pk_collection_confirm;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getPk_enterprise_payment() {
        return pk_enterprise_payment;
    }

    public void setPk_enterprise_payment(Long pk_enterprise_payment) {
        this.pk_enterprise_payment = pk_enterprise_payment;
    }

    public String getEnterprise_payment_name() {
        return enterprise_payment_name;
    }

    public void setEnterprise_payment_name(String enterprise_payment_name) {
        this.enterprise_payment_name = enterprise_payment_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getPayment_billno() {
        return payment_billno;
    }

    public void setPayment_billno(String payment_billno) {
        this.payment_billno = payment_billno;
    }

    public String getPayment_account() {
        return payment_account;
    }

    public void setPayment_account(String payment_account) {
        this.payment_account = payment_account;
    }

    public String getPayment_bank() {
        return payment_bank;
    }

    public void setPayment_bank(String payment_bank) {
        this.payment_bank = payment_bank;
    }

    public Double getCollection_amount() {
        return collection_amount;
    }

    public void setCollection_amount(Double collection_amount) {
        this.collection_amount = collection_amount;
    }

    public Long getPk_currency() {
        return pk_currency;
    }

    public void setPk_currency(Long pk_currency) {
        this.pk_currency = pk_currency;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public Date getBusi_date() {
        return busi_date;
    }

    public void setBusi_date(Date busi_date) {
        this.busi_date = busi_date;
    }

    public String getSourcebilltype() {
        return sourcebilltype;
    }

    public void setSourcebilltype(String sourcebilltype) {
        this.sourcebilltype = sourcebilltype;
    }

    public Long getSourcebillid() {
        return sourcebillid;
    }

    public void setSourcebillid(Long sourcebillid) {
        this.sourcebillid = sourcebillid;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Double getBeginAmount() {
        return beginAmount;
    }

    public void setBeginAmount(Double beginAmount) {
        this.beginAmount = beginAmount;
    }

    public Double getEndAmount() {
        return endAmount;
    }

    public void setEndAmount(Double endAmount) {
        this.endAmount = endAmount;
    }

    public Double getRepayment_principal() {
        return repayment_principal;
    }

    public void setRepayment_principal(Double repayment_principal) {
        this.repayment_principal = repayment_principal;
    }

    public Double getRepayment_interest() {
        return repayment_interest;
    }

    public void setRepayment_interest(Double repayment_interest) {
        this.repayment_interest = repayment_interest;
    }

    public Double getLogistics_cost() {
        return logistics_cost;
    }

    public void setLogistics_cost(Double logistics_cost) {
        this.logistics_cost = logistics_cost;
    }

    public Double getSupervision_cost() {
        return supervision_cost;
    }

    public void setSupervision_cost(Double supervision_cost) {
        this.supervision_cost = supervision_cost;
    }

    public Double getOverflow_amount() {
        return overflow_amount;
    }

    public void setOverflow_amount(Double overflow_amount) {
        this.overflow_amount = overflow_amount;
    }

    public String getBooked_flag() {
        return booked_flag;
    }

    public void setBooked_flag(String booked_flag) {
        this.booked_flag = booked_flag;
    }
}
