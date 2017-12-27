package com.suneee.smf.smf.consumer.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.smf.smf.api.provider.CollectionConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import com.suneee.smf.smf.model.CollectionconfirmErrorDO;
import com.suneee.smf.smf.model.CollectionconfirmErrorVO;
import com.suneee.smf.smf.service.CollConfirmErrService;
import com.suneee.smf.smf.service.EnterpriseSettlementService;

/**
 * 
 * @Description: TODO consumer
 * @author: yunhe
 * @date: 2017年12月8日 14:54:00
 */
@Service("collConfirmErrConsumer")
public class CollConfirmErrConsumer {

	private static final Logger log = LoggerFactory
			.getLogger(CollConfirmErrConsumer.class);

	@Autowired
	private CollConfirmErrService collConfirmErrService;

	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;
	
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	
	//测试环境url
	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.CollectionConfirmProvider")
  	private CollectionConfirmProvider collectionConfirmProvider;
	

	// 分页查询
	@Transactional(readOnly = true)
	public Page<CollectionconfirmErrorVO> selectByPage(
			CollectionconfirmErrorDO bean, Page<CollectionconfirmErrorVO> page) {
		return collConfirmErrService.selectByPage(bean, page);
	}

	// 根据code查询
	@Transactional(readOnly = true)
	public CollectionconfirmErrorVO getByPrimaryKey(
			CollectionconfirmErrorDO bean) {
		return collConfirmErrService.getByPrimaryKey(bean);
	}

	@Transactional(readOnly = true)
	public List<CollectionconfirmErrorVO> getAllPriamryKey() {
		return collConfirmErrService.getAllPriamryKey();
	};

	// 根据收款确认异常主键 查出收款确认主键

	public ResultMsg getSmfCollConfirmBypk() throws MsgException, InterruptedException {
		log.info("********************进入收款确认异常处理*****************");
		//查出所有来源于发货通知的收款确认异常 
		List<String> enterAll = collConfirmErrService.selDisEnterAndApp();
		
		for(String enter :enterAll){
			String [] enterArray = enter.split("_");
			log.info("进入发货收款确认异常");
			//根据对应的业务组织id 和发货申请的企业主键  查出对应的一条收款确认异常的数据 
			CollectionconfirmErrorVO collDO =  collConfirmErrService.selCollErrorByEnter(Integer.parseInt(enterArray[0]),Integer.parseInt(enterArray[1]));
			//根据收款确认异常的对象获得收款确认单
			CollectionConfirmDO collectionConfirmDO  = collConfirmErrService.getAllCollByPk(collDO);
			//调用分账结算的接口
			collectionConfirmProvider.add(collectionConfirmDO,Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_TIMING);
		}
		
		// 查出所有来源于资金注入的收款确认异常
		List<CollectionconfirmErrorVO> list = collConfirmErrService.getAllPriamryKey();
		// 根据异常主键查出对应的收款确认单
		for (CollectionconfirmErrorVO bean : list) {
			CollectionConfirmDO collectionConfirmDO  = collConfirmErrService.getAllCollByPk(bean);
			collectionConfirmProvider.add(collectionConfirmDO,Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_TIMING);
		}
		
		return new ResultMsg("1", "处理成功");
		
	};
	
}