package com.suneee.smf.smf.consumer.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.smf.smf.api.provider.AdvanceConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmErrorDO;
import com.suneee.smf.smf.model.AdvanceConfirmErrorVO;
import com.suneee.smf.smf.service.AdvanceConfirmErrorService;
import com.suneee.smf.smf.service.AdvanceConfirmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * 
 * @Description: TODO consumer
 * @author: changzhaoyu
 * @date: 2017年12月19日 12:54:00
 */

@Service("advanceConfirmErrorConsumer")
public class AdvanceConfirmErrorConsumer {

	private static final Logger log = LoggerFactory
			.getLogger(AdvanceConfirmErrorConsumer.class);
	
	@Autowired
	private AdvanceConfirmErrorService advanceConfirmErrorService;
	@Autowired
	private AdvanceConfirmService advanceConfirmService;
	//测试环境url
	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.AdvanceConfirmProvider")
	private AdvanceConfirmProvider advanceConfirmProvider;
	
	// 分页查询
	@Transactional(readOnly = true)
	public Page<AdvanceConfirmErrorVO> selectByPage(
			AdvanceConfirmErrorDO bean, Page<AdvanceConfirmErrorVO> page) {
		return advanceConfirmErrorService.selectByPage(bean, page);
	}

	// 根据code查询
	@Transactional(readOnly = true)
	public AdvanceConfirmErrorVO getByPrimaryKey(
			AdvanceConfirmErrorDO bean) {
		return advanceConfirmErrorService.getByPrimaryKey(bean);
	}
	
	// 定时任务调用放款异常处理方法
	public ResultMsg getSmfAdvanceConfirmBypk() throws MsgException {
		// 查出所有的异常主键
		List<AdvanceConfirmErrorVO> list = advanceConfirmErrorService.getAllPriamryKey();
		// 根据异常主键查出对应的收款确认单
		for (AdvanceConfirmErrorVO bean : list) {
			AdvanceConfirmDO advanceConfirmDO  = advanceConfirmErrorService.getAllAdvanceByPk(bean);
			advanceConfirmProvider.add(advanceConfirmDO,Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_TIMING);
		}
		return new ResultMsg("0", "处理成功");
	};
}
