package com.suneee.smf.smf.provider.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.smf.smf.api.provider.CollectionConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.*;
import com.suneee.smf.smf.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service//指明dubbo服务
public class CollectionConfirmProviderImpl implements CollectionConfirmProvider {

	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;

	@Autowired
	private CollectionConfirmService collectionConfirmService;

	@Autowired
	private CollConfirmErrService collConfirmErrService;

	@Autowired
	private SaleApplicationService saleApplicationService;

	@Autowired
	private CapitalInjectionService capitalInjectionService;

	private static final Logger log= LoggerFactory.getLogger(CollectionConfirmProviderImpl.class);

	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;



	@Override
	public ResultMsg add(CollectionConfirmDO collectionConfirmDO,String pushType){

		if(Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
			try{
				//通过来源类型查询对应数据
				String sourcebilltype = collectionConfirmDO.getSourcebilltype();
				Long sourcebillid = collectionConfirmDO.getSourcebillid();
				Long enterpriseid=null;//企业组织ID
				Double totalmoney = 0d;//应收款总金额
				List<CollectionConfirmVO> collectionConfirmVOs = new ArrayList<>();
				//推送来源发货申请，获取组织ID，应收款总金额
				if(Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION.equals(sourcebilltype)){
					SaleApplicationDO saleDo=new SaleApplicationDO();
					saleDo.setPk_sale_application(collectionConfirmDO.getSourcebillid());

					SaleApplicationVO doByPrimaryKey = saleApplicationService.getDOByPrimaryKey(saleDo);
					enterpriseid=doByPrimaryKey.getEnterpriseid();
					totalmoney = doByPrimaryKey.getAmount();
					//查找对应的累计收款单
					collectionConfirmVOs = collectionConfirmService.queryCollectionBySource(sourcebillid,sourcebilltype,enterpriseid);


				}
				//推送来源资金注入，获取组织ID，应收款总金额
				if(Constant.COLLECTION_SOURCE_BILL_TYPE_CAPITALINJECTION.equals(sourcebilltype)){
					CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
					capitalInjectionDO.setPk_capital_injection(collectionConfirmDO.getSourcebillid());
					CapitalInjectionVO capitalInjectionVO = capitalInjectionService.getById(capitalInjectionDO);
					enterpriseid=capitalInjectionVO.getEnterpriseid();
					totalmoney = capitalInjectionVO.getAmount();
					//查找对应的累计收款单
					collectionConfirmVOs = collectionConfirmService.queryCollectionBySource(sourcebillid,sourcebilltype,enterpriseid);

				}

				double totalCollection = 0d;//当前收到的总金额
				for(CollectionConfirmVO collectionConfirmVO : collectionConfirmVOs){
					totalCollection += collectionConfirmVO.getCollection_amount();
				}
				//判断该单是否已经推送过,判断条件：当前收到的总金额 < 应收款总金额 ，即为没有推送过 （以后改为银行凭证号判断）
				if(totalCollection < totalmoney){
					//获取收款单号
					String collectionConfirmCode = getNewCode(enterpriseid, Constant.COLLECTION_CODERULE_CODE, "收款确认");
					collectionConfirmDO.setCode(collectionConfirmCode);
					
					//插入收款确认单，收款确认异常
					collectionConfirmService.add(collectionConfirmDO,enterpriseid);
					log.info("收确认插入成功============================");
				}else{
					return new ResultMsg("1", "该单已经推送过收款确接口！");
				}

			}catch(Exception e){
				e.printStackTrace();
				return new ResultMsg("0",e.getMessage());
			}
		}
        
		if(Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
			//调用分账结算
			new Thread(new Runnable() {
				@Override
				public void run() {
					split(collectionConfirmDO,pushType);
				}
			}).start();
		}else{
			split(collectionConfirmDO,pushType);
		}
		

		log.info("收款确认Peovider调用成功============================");
		return new ResultMsg("1", "保存成功");
	}

	private void split(CollectionConfirmDO collectionConfirmDO,String pushType){
		try{
			//正常推送过来，需加延迟，等调用方流程走完
			if(Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
				Thread.sleep(20000);
			}


			//判读是否需要生成单号
			//ResultMsg msgCode = enterpriseSettlementService.whetherDelivery(collectionConfirmDO.getPk_collection_confirm(),collectionConfirmDO.getEnterpriseid());
			String deliveryAdviceCode = getNewCode(collectionConfirmDO.getEnterpriseid(), Constant.DELIVERYADVICE_CODERULE_CODE, "发货通知");
			String advanceConfirmCode = getNewCode(collectionConfirmDO.getEnterpriseid(), Constant.ADVANCE_CONFIRM_CODERULE_CODE, "放款确认");

			//调用分账结算
			ResultMsg msg = enterpriseSettlementService.separateAccountingSettlement(collectionConfirmDO.getPk_collection_confirm(),collectionConfirmDO.getEnterpriseid(),deliveryAdviceCode,advanceConfirmCode);
			//分账结算利润拆分处理过的收款单
			List<Object> collectionConfirmVOs = msg.getData();
			for(Object object : collectionConfirmVOs){
				//正常推送的单子，直接删除收款确认异常
				if(Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
					collConfirmErrService.deleteErrorDOList((CollectionConfirmVO)object);
				}else{
					//定时任务推送的单子，更新收款去人异常
					CollectionConfirmVO collectionConfirmVO = (CollectionConfirmVO) object;
					CollectionconfirmErrorVO errorVO = new CollectionconfirmErrorVO();
					errorVO.setPk_collection_confirm(collectionConfirmVO.getPk_collection_confirm());
					errorVO.setEnterpriseid(collectionConfirmVO.getEnterpriseid());
					errorVO.setState(Constant.COLLECTIONCONFIRMERROR_STATE_FINISH);
					collConfirmErrService.updateErrorState(errorVO);
				}
			}
			log.info("FOR删除收确认异常成功============================");
		}catch (MsgException e){
			try{
				//该收款确认单数据来源不是发货申请，不需分账结算！
				if(Constant.ENTERPRISE_SETTLEMENT_ERROR_CODE_TWO.equals(e.getCode())){
					CollectionConfirmVO  collectionConfirmVO = new CollectionConfirmVO();
					collectionConfirmVO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
					collectionConfirmVO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
					//删除对应的收款异常
					collConfirmErrService.deleteErrorDOList(collectionConfirmVO);
					log.info("资金注入删除收确认异常成功============================");
				}else {
					//分账结算失败，更新对应的收款异常
					CollectionconfirmErrorVO errorDO = new CollectionconfirmErrorVO();
					errorDO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
					errorDO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
					errorDO.setError_msg(e.getMessage());
					collConfirmErrService.update(errorDO);
					log.info("分账结算失败更新收确认异常成功============================");
				}
			}catch (Exception ee){
				ee.printStackTrace();
				log.info("MsgException更新收款异常失败============================");
			}

		}catch (Exception e){
			e.printStackTrace();
			try{
				CollectionconfirmErrorVO errorDO = new CollectionconfirmErrorVO();
				errorDO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
				errorDO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
				errorDO.setError_msg("分账结算未知错误异常！");
				collConfirmErrService.update(errorDO);
				log.info("分账结算未知错误更新收确认异常成功============================");
			}catch (Exception ee){
				ee.printStackTrace();
				log.info("Exception更新收款异常失败============================");
			}
		}
	}

	private String getNewCode(Long enterpriseId, String codeRule, String nodeName) throws MsgException{

		//编码provider接口
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(enterpriseId, codeRule);
		if ("1".equals((String)map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				throw new MsgException("0", "生成" + nodeName + "单号失败！！！");
			}

		}else{
			throw new MsgException("0", "生成" + nodeName + "单号失败！！！");
		}
		return (String) map.get("msg");
	}

}
