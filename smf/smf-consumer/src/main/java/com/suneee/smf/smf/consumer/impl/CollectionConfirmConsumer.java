package com.suneee.smf.smf.consumer.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.*;
import com.suneee.smf.smf.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Description: ${todo} 收款确认
 * @author: 超杰
 * @date: ${date} ${time}
 * ${tags}
 */
@Service("collectionConfirmConsumer")
public class CollectionConfirmConsumer {
	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;

	private static final Logger log= LoggerFactory.getLogger(CollectionConfirmConsumer.class);
	
	@Autowired
	private CollectionConfirmService collectionConfirmService;
	
    @Autowired
    private CollConfirmErrService collConfirmErrService;

	@Autowired
	private SaleApplicationService saleApplicationService;

	@Autowired
	private CapitalInjectionService capitalInjectionService;


	//TODO 测试环境url
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;

	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	//查询
	@Transactional(readOnly = true)
	public Page<CollectionConfirmVO> queryByPage(Page<CollectionConfirmVO> page, CollectionConfirmVO bean) {
		return collectionConfirmService.queryByPage(page,bean);
	}

	/**
	 * @Description: ${给银行调用收款接口}
	 * @author: 超杰
	 * @date: ${date} ${time}
	 * ${tags}
	 */
	/*public ResultMsg add(CollectionConfirmDO collectionConfirmDO) throws MsgException {
		//通过来源类型查询对应数据
		String sourcebilltype = collectionConfirmDO.getSourcebilltype();
		Long enterpriseid=null;
		if(Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION.equals(sourcebilltype)){
			SaleApplicationDO saleDo=new SaleApplicationDO();
			saleDo.setPk_sale_application(collectionConfirmDO.getSourcebillid());

			SaleApplicationVO doByPrimaryKey = saleApplicationService.getDOByPrimaryKey(saleDo);
			 enterpriseid=doByPrimaryKey.getEnterpriseid();
		}
		if(Constant.COLLECTION_SOURCE_BILL_TYPE_CAPITALINJECTION.equals(sourcebilltype)){
			CapitalInjectionDO capitalInjectionDO = new CapitalInjectionDO();
			capitalInjectionDO.setPk_capital_injection(collectionConfirmDO.getSourcebillid());
			CapitalInjectionVO capitalInjectionVO = capitalInjectionService.getById(capitalInjectionDO);
			enterpriseid=capitalInjectionVO.getEnterpriseid();
		}
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(enterpriseid, Constant.COLLECTION_CODERULE_CODE);
		if ("1".equals(map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				return new ResultMsg("0","生成申请单号失败！！！");
			}
		}
		collectionConfirmDO.setCode((String) map.get("msg"));
		collectionConfirmService.add(collectionConfirmDO,enterpriseid);
		//调用分账结算
		try{
			//判读是否需要生成单号
			ResultMsg msgCode = enterpriseSettlementService.whetherDelivery(collectionConfirmDO.getPk_collection_confirm(),collectionConfirmDO.getEnterpriseid());
			String deliveryAdviceCode = "";
			if("1".equals(msgCode.getCode())){
				//编码后面provider接口
    			Map<String,Object> deliveryAdviceMap = codeRuleRestProvider.submitSingleton(collectionConfirmDO.getEnterpriseid(), Constant.DELIVERYADVICE_CODERULE_CODE);
    			if ("1".equals((String)deliveryAdviceMap.get("code"))) {
    				if ("".equals((String) deliveryAdviceMap.get("msg"))) {
    					throw new MsgException("0", "生成发货通知单号失败！！！");
    				}
    				
    			}else{
    				throw new MsgException("0", "生成发货通知单号失败！！！");
    			}
    			deliveryAdviceCode = (String) deliveryAdviceMap.get("msg");
			}
			
			ResultMsg msg = enterpriseSettlementService.separateAccountingSettlement(collectionConfirmDO.getPk_collection_confirm(),collectionConfirmDO.getEnterpriseid(),deliveryAdviceCode);
			List<Object> collectionConfirmVOs = msg.getData();
			for(Object object : collectionConfirmVOs){
				collConfirmErrService.deleteErrorDOList((CollectionConfirmVO)object); 
			}
		
		}catch (MsgException e){
			//该收款确认单数据来源不是发货申请，不需分账结算！
			if(Constant.ENTERPRISE_SETTLEMENT_ERROR_CODE_TWO.equals(e.getCode())){
				CollectionConfirmVO  collectionConfirmVO = new CollectionConfirmVO();
				collectionConfirmVO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
				collectionConfirmVO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
				//删除对应的收款异常
				collConfirmErrService.deleteErrorDOList(collectionConfirmVO);
			}else {
				//分账结算失败，更新对应的收款异常
				CollectionconfirmErrorVO errorDO = new CollectionconfirmErrorVO();
				errorDO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
				errorDO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
				errorDO.setError_msg(e.getMessage());
				collConfirmErrService.update(errorDO);
			}
		}catch (Exception e){
			log.error(e.getMessage());
			CollectionconfirmErrorVO errorDO = new CollectionconfirmErrorVO();
			errorDO.setPk_collection_confirm(collectionConfirmDO.getPk_collection_confirm());
			errorDO.setEnterpriseid(collectionConfirmDO.getEnterpriseid());
			errorDO.setError_msg("分账结算未知错误异常！");
			collConfirmErrService.update(errorDO);
		}
		return new ResultMsg("1", "保存成功");

	}*/

	//通过主键查询数据
	@Transactional(readOnly = true)
	public CollectionConfirmVO getRestByPrimaryKey(CollectionConfirmVO vo, SystemUserInfoT userInfoT) {
		return collectionConfirmService.getRestByPrimaryKey(vo,userInfoT);
	}



	
	/*//编辑
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modify(CollectionConfirmDO collectionConfirmDO, SystemUserInfoT userInfo) {
		if (collectionConfirmDO == null) {
			return new ResultMsg("0", "数据为空");
		}

		collectionConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		
		if(collectionConfirmService.checkCreditQuotaTimeStamp(collectionConfirmDO)){
			
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return collectionConfirmService.modify(collectionConfirmDO,userInfo);

	}
	
	//删除
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg delete(CollectionConfirmInsertDO insertDO, SystemUserInfoT userInfo) {
		int count = 0;
		for (CollectionConfirmDO bean : insertDO.getInsertDO()) {
			int m = collectionConfirmService.delete(bean,userInfo);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
    		count += m;
    	}
    	if (count > 0) {
    		return new ResultMsg("1", "删除成功！！");
    	} 
    	return new ResultMsg("0", "删除失败！！");
		
	}

	@Transactional(readOnly = true)
	public ResultMsg checkApproveById(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) {
		 CollectionConfirmDO bean = new CollectionConfirmDO();
		bean.setPk_collection_confirm(confirmVO.getPk_collection_confirm());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(confirmVO.getTime_stamp());
		if(collectionConfirmService.checkCreditQuotaTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		String roleName = collectionConfirmService.getApproveRoleCodes(confirmVO.getPk_collection_confirm(),userInfo);
		log.info("*****************调用roleProvider***************************");
		String userRoleCodes = roleProvider.getRoleByUser(userInfo.getUserName());
		log.info("*****************调用roleProvider结果***************************"+userRoleCodes);
		String[] strs = userRoleCodes.split(",");
		boolean hasRole = false;
		for (String str : strs) {
			RoleInfo role = roleProvider.getRoleByRoleId(Integer.valueOf(str));
			if (roleName.equals(role.getRoleCode())) {
				hasRole = true;
			}
		}
		log.info("*****************hasRole结果***************************"+hasRole);
		if (hasRole) {
			return new ResultMsg("1", "可以审批");
		} else {
			return new ResultMsg("0", "该用户不能审批该数据！");
		}
	}
	//查看流程
	public ResultMsg approveLog(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) {
		CollectionConfirmDO collectionConfirmDO = new CollectionConfirmDO();
		collectionConfirmDO.setPk_collection_confirm(confirmVO.getPk_collection_confirm());
		collectionConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		collectionConfirmDO.setTime_stamp(confirmVO.getTime_stamp());
		if(collectionConfirmService.checkCreditQuotaTimeStamp(collectionConfirmDO)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return collectionConfirmService.approveLog(confirmVO,userInfo);
	}
	
	//审核
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approveCollectionConfirmDO(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo)throws MsgException {

		CollectionConfirmDO bean = new CollectionConfirmDO();
		bean.setPk_collection_confirm(confirmVO.getPk_collection_confirm());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(confirmVO.getTime_stamp());
		if(collectionConfirmService.checkCreditQuotaTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}

		return collectionConfirmService.approveCollectionConfirmDO(confirmVO,userInfo);
	}
	//提交进入流程
	@Transactional(readOnly = false,rollbackFor = Exception.class)
	public ResultMsg submitModel(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) {
		return collectionConfirmService.submitModel(confirmVO, userInfo);
	}*/
}
