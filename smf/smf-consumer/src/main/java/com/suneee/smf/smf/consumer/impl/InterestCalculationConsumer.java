package com.suneee.smf.smf.consumer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.model.InterestCalculationDO;
import com.suneee.smf.smf.model.InterestCalculationVO;
import com.suneee.smf.smf.service.InterestCalculationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @Description: 企业计息
 * @author: 张礼佳
 * @date: 2017年12月8日 上午11:01:05
 */
@Service("interestCalculationConsumer")
public class InterestCalculationConsumer {

	@Autowired
	private InterestCalculationService interestCalculationService;

	public Page<InterestCalculationVO> listByPage(
			Page<InterestCalculationVO> page, InterestCalculationDO bean) {
		return interestCalculationService.listByPage(page, bean);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void add() {
		interestCalculationService.add();
	}
}
