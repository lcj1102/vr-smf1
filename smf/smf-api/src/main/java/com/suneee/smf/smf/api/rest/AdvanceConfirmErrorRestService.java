package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
/**
 * @Description: 放款确认异常处理接口方法
 * @author: changzhaoyu
 * @date: 2017年12月21日 下午4:08:11
 */
public interface AdvanceConfirmErrorRestService {

	public ResultMsg selectByPage(String code, String enterprise_application_name,
			String time_stamp, String beginDateTS, String endDateTS,
			String busi_date, String beginDateBD, String endDateBD,
			String searchValue, String state_new, String state_end, String length, String pageNum,String draw,
			String sessionId);

	public ResultMsg getByPrimaryKey(String code,
			String sessionId);
	
	public ResultMsg dealAdvanceConfirmError();
}
