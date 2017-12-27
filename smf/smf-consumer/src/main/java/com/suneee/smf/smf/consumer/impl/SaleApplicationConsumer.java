package com.suneee.smf.smf.consumer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.scn.scf.api.provider.EnterpriseRestProvider;
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.api.provider.CollectionConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.SaleApplicationDO;
import com.suneee.smf.smf.model.SaleApplicationInsertDO;
import com.suneee.smf.smf.model.SaleApplicationVO;
import com.suneee.smf.smf.service.ActivitiUtilService;
import com.suneee.smf.smf.service.SaleApplicationService;

/**
 * 
 * @Description: TODO 发货申请consumer
 * @author: 崔亚强
 * @date: 2017年12月7日 下午3:54:00
 */
@Service("saleApplicationConsumer")
public class SaleApplicationConsumer {

	private static final Logger log=LoggerFactory.getLogger(SaleApplicationConsumer.class);

    @Autowired
    private SaleApplicationService saleApplicationService;
    
    @Autowired
	private ActivitiUtilService activitiUtilService;
    
    //测试环境url
  	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
  	private RoleProvider roleProvider;
  	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
  	//测试环境url
  	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.EnterpriseRestProvider")   
  	private EnterpriseRestProvider enterpriseRestProvider;

  	//测试环境url
  	@Reference(url="dubbo://172.16.36.68:25017/com.suneee.smf.smf.api.provider.CollectionConfirmProvider")
  	private CollectionConfirmProvider collectionConfirmProvider;


  	//分页查询
    @Transactional(readOnly = true)
    public Page<SaleApplicationVO> selectByPage(SaleApplicationDO bean,Page<SaleApplicationVO> page) {
        return saleApplicationService.selectByPage(bean, page);
    }
    
    //新增
    @Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg insertDO(SaleApplicationDO bean,SystemUserInfoT userInfo) throws MsgException {
    	//生成并设置发货申请单单号
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.SALEAPPLICATION_CODERULE_CODE);
		ResultMsg msg = new ResultMsg();
		msg.setCode((String)map.get("code"));
		msg.setMsg((String)map.get("msg"));
		if ("1".equals(msg.getCode())) {
			bean.setCode(msg.getMsg());
		} else {
			return msg;
		}
		return saleApplicationService.insertDO(bean, userInfo);
	}
    
    //作废
    @Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg delete(SaleApplicationInsertDO insertDO, SystemUserInfoT userInfo) {
    	return saleApplicationService.delete(insertDO, userInfo);
	}
    
    //校验是否有审批权限
    @Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg checkApproveById(SaleApplicationVO saleApplicationVO, SystemUserInfoT userInfo) {
		SaleApplicationDO bean = new SaleApplicationDO();
		bean.setPk_sale_application(saleApplicationVO.getPk_sale_application());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(saleApplicationVO.getTime_stamp());
		if(saleApplicationService.checkSaleApplicationTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		String roleName = saleApplicationService.getApproveRoleCodes(saleApplicationVO.getPk_sale_application(),userInfo);
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
    

    //审批
    @Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approve(SaleApplicationDO bean, SystemUserInfoT userInfo, String sessionId) throws MsgException, InterruptedException {

    	ResultMsg approveMsg =  saleApplicationService.approve(bean,userInfo,sessionId);
		if("0".equals(approveMsg.getCode())){
			return approveMsg;
		}

		long start = 0L;
    	if ("agree".equals(bean.getType())) {
    		boolean isLastNode = activitiUtilService.isNode(bean.getPk_sale_application(),Constant.SALEAPPLICATION_APPROVE_CODE,Constant.SALEAPPLICATION_APPROVE_LASTTASK);
			if (isLastNode) {
				SaleApplicationVO savo = saleApplicationService.getDOByPrimaryKey(bean);
	        	//收款确认
				CollectionConfirmDO confirmDO = new CollectionConfirmDO();
				confirmDO.setCode("001");
				confirmDO.setPk_enterprise_payment(savo.getPk_enterprise_payment());
				confirmDO.setEnterprise_payment_name(savo.getEnterprise_payment_name());
				confirmDO.setContact(savo.getContact());
				confirmDO.setContact_number(savo.getContact_number());
				confirmDO.setPayment_billno("FKPZ");
				confirmDO.setPayment_account("401123456");
				confirmDO.setPayment_bank("招商银行");
				confirmDO.setPayment_method("银行扣款");
				confirmDO.setCollection_amount(savo.getAmount()/4);
				confirmDO.setPk_currency(savo.getPk_currency());
				confirmDO.setCurrency_name(savo.getCurrency_name());
				confirmDO.setSourcebilltype(Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION);
				confirmDO.setSourcebillid(savo.getPk_sale_application());
				confirmDO.setState(Constant.COLLECTION_STATE_APPROVED);
				confirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				confirmDO.setRepayment_principal(0d);
				confirmDO.setRepayment_interest(0d);
				confirmDO.setLogistics_cost(0d);
				confirmDO.setSupervision_cost(0d);
				confirmDO.setOverflow_amount(0d);
				confirmDO.setInputmanid(savo.getInputmanid());
				confirmDO.setBookindate(new Date());
				confirmDO.setInputmanname(savo.getInputmanname());
				confirmDO.setBusi_date(new Date());
				confirmDO.setBooked_flag("未记账");
				log.info("调用收款确认provider==================================");
				start = System.currentTimeMillis();
				ResultMsg providerMsg = collectionConfirmProvider.add(confirmDO, Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL);
				log.info("调用收款确认成功==================================");
				log.info("调用收款确认成功后返回数据:"+providerMsg.toString()+"===============");
				if("0".equals(providerMsg.getCode())){
					throw new MsgException("0",providerMsg.getMsg());
				}
			}
    	}

		activitiUtilService.approve(Constant.SALEAPPLICATION_APPROVE_CODE, String.valueOf(bean.getPk_sale_application()), bean.getOption(), String.valueOf(userInfo.getUserId()), userInfo, Integer.parseInt(approveMsg.getHtml()));
    	long diff = start - System.currentTimeMillis();
    	log.info("本地需要睡眠时间================================" + diff);
    	log.info("本地需要睡眠时间================================" + diff);
    	log.info("本地需要睡眠时间================================" + diff);
    	log.info("本地需要睡眠时间================================" + diff);
		return new ResultMsg("1", "审核成功！");
	}
    
    //根据id唯一查询
    @Transactional(readOnly = true)
    public SaleApplicationVO getDOByPrimaryKey(SaleApplicationDO bean) {
		return saleApplicationService.getDOByPrimaryKey(bean);
	}
    
    //修改
    @Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg update(SaleApplicationDO bean, SystemUserInfoT userInfo) {
    	return saleApplicationService.update(bean,userInfo);
	}
    
    //提交
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg submitModel(SaleApplicationDO saleApplicationDO,SystemUserInfoT userInfo) {
		return saleApplicationService.submitModel(saleApplicationDO,userInfo);
	}
	
	//查看审核流程
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approveLog(SaleApplicationVO saleApplicationVO,SystemUserInfoT userInfo) {
		SaleApplicationDO bean = new SaleApplicationDO();
		bean.setPk_sale_application(saleApplicationVO.getPk_sale_application());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(saleApplicationVO.getTime_stamp());
		if(saleApplicationService.checkSaleApplicationTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return saleApplicationService.approveLog(saleApplicationVO,userInfo);
	}
	
	@Transactional(readOnly = true)
    public List<EnterpriseVO> getEnterpriseByName(String keyword,Long enterpriseid) {
		return enterpriseRestProvider.selectByName(keyword, enterpriseid,Constant.ENTERPRISE_OPERATION);
	}
	
	@Transactional(readOnly = true)
	public List<EnterpriseVO> selectEnterpriseByName(String keyword,Long enterpriseid) {
		return enterpriseRestProvider.selectByName(keyword, enterpriseid,null);
	}
	
	@Transactional(readOnly = true)
    public EnterpriseVO getEnterpriseByCode(String code,Long enterpriseid) {
		return enterpriseRestProvider.selectByCode(code, enterpriseid);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg reCollection(SaleApplicationDO saleApplicationDO,SystemUserInfoT userInfo, String sessionId) throws MsgException {
		SaleApplicationVO savo = saleApplicationService.getDOByPrimaryKey(saleApplicationDO);
    	//收款确认
		CollectionConfirmDO confirmDO = new CollectionConfirmDO();
		confirmDO.setCode("001");
		confirmDO.setPk_enterprise_payment(savo.getPk_enterprise_payment());
		confirmDO.setEnterprise_payment_name(savo.getEnterprise_payment_name());
		confirmDO.setContact(savo.getContact());
		confirmDO.setContact_number(savo.getContact_number());
		confirmDO.setPayment_billno("FKPZ");
		confirmDO.setPayment_account("401123456");
		confirmDO.setPayment_bank("招商银行");
		confirmDO.setPayment_method("银行扣款");
		confirmDO.setCollection_amount(savo.getAmount()/4);
		confirmDO.setPk_currency(savo.getPk_currency());
		confirmDO.setCurrency_name(savo.getCurrency_name());
		confirmDO.setSourcebilltype(Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION);
		confirmDO.setSourcebillid(savo.getPk_sale_application());
		confirmDO.setState(Constant.COLLECTION_STATE_APPROVED);
		confirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		confirmDO.setRepayment_principal(0d);
		confirmDO.setRepayment_interest(0d);
		confirmDO.setLogistics_cost(0d);
		confirmDO.setSupervision_cost(0d);
		confirmDO.setOverflow_amount(0d);
		confirmDO.setInputmanid(savo.getInputmanid());
		confirmDO.setBookindate(new Date());
		confirmDO.setInputmanname(savo.getInputmanname());
		confirmDO.setBusi_date(new Date());
		confirmDO.setBooked_flag("未记账");
		log.info("调用收款确认provider==================================");
		ResultMsg providerMsg = collectionConfirmProvider.add(confirmDO, Constant.COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL);
		log.info("调用收款确认成功==================================");
		log.info("调用收款确认成功后返回数据:"+providerMsg.toString()+"===============");
		if("0".equals(providerMsg.getCode())){
			throw new MsgException("0",providerMsg.getMsg());
		}
		return providerMsg;
	}
	
}
