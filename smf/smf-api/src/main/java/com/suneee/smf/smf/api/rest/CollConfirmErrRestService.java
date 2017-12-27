package com.suneee.smf.smf.api.rest;


import com.suneee.smf.smf.common.ResultMsg;


/**
 * 
 * @Description: TODO 发货申请后台接口方法
 * @author: 崔亚强
 * @date: 2017年12月7日 下午4:08:27
 */
public interface CollConfirmErrRestService {

	public ResultMsg selectByPage(String code, String enterprise_payment_name,
			String time_stamp, String beginDateTS, String endDateTS,
			String busi_date, String beginDateBD, String endDateBD,String notFirstLoadingFlag,
			String searchValue, String state_new, String state_end, String length, String pageNum,
			String sessionId);

	public ResultMsg getByPrimaryKey(String code,
			String sessionId);
	
	
    //根据收款确认异常主键 查出收款确认主键
	public ResultMsg getSmfCollConfirmBypk();

}
