package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;

/**
 * 
 * @Description: 分账结算
 * @author: 张礼佳
 * @date: 2017年12月15日 下午4:04:36
 */
public interface EnterpriseSettlementRestService {

	/**
	 * 
	 * @Title: 查询分账结算
	 * @Description: 多条件分页查询企业计息数据
	 * @return
	 * @return: ResultMsg
	 */
	ResultMsg enterpriseSettlementList(String searchValue, String name,
			String enterprise_name, String beginDate, String endDate,
			String minAmount, String maxAmount, String length, String pageNum,
			String draw, String sessionId);

	/**
	 * 
	 * @Title: getEnterpriseSettlement
	 * @Description: 根据id获取企业结息详情
	 * @param id
	 * @param sessionId
	 * @return
	 * @return: ResultMsg
	 */
	ResultMsg getEnterpriseSettlement(String id, String sessionId);
}
