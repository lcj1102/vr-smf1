package com.suneee.smf.smf.consumer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.model.EnterpriseSettlementDO;
import com.suneee.smf.smf.model.EnterpriseSettlementVO;
import com.suneee.smf.smf.service.EnterpriseSettlementService;

/**
 * 
 * @Description: 企业结算
 * @author: 张礼佳
 * @date: 2017年12月15日 下午3:59:26
 */
@Service("enterpriseSettlementComsumer")
public class EnterpriseSettlementComsumer {

	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;

	/**
	 * 
	 * @Title: listByPage
	 * @Description: 分页查询分账结算
	 * @param page
	 * @param bean
	 * @return
	 * @return: Page<EnterpriseSettlementVO>
	 */
	public Page<EnterpriseSettlementVO> listByPage(
			Page<EnterpriseSettlementVO> page, EnterpriseSettlementVO bean) {
		return enterpriseSettlementService.listByPage(page, bean);
	}

	/**
	 * 
	 * @Title: queryById 
	 * @Description: 根据id查询企业结息详情
	 * @param id
	 * @return
	 * @return: EnterpriseSettlementVO
	 */
	public EnterpriseSettlementVO queryById(String id, Long enterpriseid) {
		return enterpriseSettlementService.queryById(id, enterpriseid);
	}

}
