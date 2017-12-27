package com.suneee.smf.smf.consumer.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.model.CapitalBalanceDO;
import com.suneee.smf.smf.model.CapitalBalanceVO;
import com.suneee.smf.smf.service.CapitalBalanceService;
/**
 * @Description: 资金结余历史表
 * @author: 致远
 * @date: 2017年12月11日 下午3:14:14
 */
@Service("capitalBalanceConsumer")
public class CapitalBalanceConsumer {
	
	@Autowired
	private CapitalBalanceService capitalBalanceService;

	
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void refreshBalanceTask() throws MsgException {
		capitalBalanceService.refreshBalanceTask();
	}

  	//分页查询
    @Transactional(readOnly = true)
    public Page<CapitalBalanceVO> capiBalanceList(CapitalBalanceDO bean,Page<CapitalBalanceVO> page) {
        return capitalBalanceService.selectByPage(bean, page);
    }
    //查询所有
    @Transactional(readOnly = true)
    public Page<CapitalBalanceVO> balanceList(CapitalBalanceDO bean,Page<CapitalBalanceVO> page) {
    	return capitalBalanceService.balanceList(bean, page);
    }
}
