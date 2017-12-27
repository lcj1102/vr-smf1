package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;

/**
 * 
 * @Description: 企业计息api
 * @author: 张礼佳
 * @date: 2017年12月8日 上午9:32:19
 */
public interface InterestCalculationRestService {

	/**
	 * 
	 * @Title: 查询企业计息
	 * @Description: 多条件分页查询企业计息数据
	 * @return
	 * @return: ResultMsg
	 */
	ResultMsg interestCalculationList(String searchValue, String name,
			String state, String beginDate, String endDate, String length,
			String pageNum, String draw, String sessionId);

	public void autoCalculateInterest( );

}
