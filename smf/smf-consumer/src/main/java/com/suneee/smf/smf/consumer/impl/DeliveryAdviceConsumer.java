package com.suneee.smf.smf.consumer.impl;

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
import com.suneee.scn.system.api.provider.RoleProvider;
import com.suneee.scn.system.model.vo.RoleInfo;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.DeliveryAdviceDO;
import com.suneee.smf.smf.model.DeliveryAdviceList;
import com.suneee.smf.smf.model.DeliveryAdviceVO;
import com.suneee.smf.smf.model.DeliveryItemDO;
import com.suneee.smf.smf.model.DeliveryItemVO;
import com.suneee.smf.smf.service.DeliveryAdviceService;
import com.suneee.smf.smf.service.DeliveryItemService;
/**
 * 
 * @Description:发货通知cousumer
 * @author:daiaojie
 * @date: 2017年12月4日 上午9:52:51
 */
@Service("deliveryAdviceConsumer")
public class DeliveryAdviceConsumer {
	private static final Logger log=LoggerFactory.getLogger(DeliveryAdviceConsumer.class);
	@Autowired
	private DeliveryAdviceService deliveryAdviceService;
	@Autowired
	private DeliveryItemService deliveryItemService;
	//TODO 测试环境url
	@Reference(version="1.0",url="dubbo://172.16.36.68:20917/com.suneee.scn.system.api.provider.RoleProvider")
	private RoleProvider roleProvider;
	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;
	/**
	 * 
	 * @Title: queryByPage 
	 * @Description: 发货通知查询列表
	 * @param page
	 * @param deliveryAdviceVO
	 * @return: Page<DeliveryAdviceVO>
	 */
	@Transactional(readOnly = true)
	public Page<DeliveryAdviceVO> queryByPage(Page<DeliveryAdviceVO> page,
			DeliveryAdviceVO deliveryAdviceVO) {
		return deliveryAdviceService.queryByPage(page,deliveryAdviceVO);
	}
	/**
	 * 
	 * @Title: add 
	 * @Description: 新增数据
	 * @param deliveryAdviceVO
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg add(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo) {
		if (deliveryAdviceVO == null) {
            return new ResultMsg("0", "数据为空");
        }
		Map<String,Object> map = codeRuleRestProvider.submitSingleton(userInfo.getEnterpriseid().longValue(), Constant.DELIVERYADVICE_CODERULE_CODE);
		if ("1".equals(map.get("code"))) {
			if ("".equals((String) map.get("msg"))) {
				return new ResultMsg("0","生成申请单号失败！！！");
			}
			deliveryAdviceVO.setCode((String) map.get("msg"));
			return deliveryAdviceService.add(deliveryAdviceVO,userInfo);
		}
		return new ResultMsg("0","新增失败");
	}
	/**
	 * 
	 * @Title: getRestByPrimaryKey 
	 * @Description: 根据主表id查询数据
	 * @param deliveryAdviceDO
	 * @return
	 * @return: DeliveryAdviceVO
	 */
	@Transactional(readOnly = true)
	public DeliveryAdviceVO getRestByPrimaryKey(DeliveryAdviceDO deliveryAdviceDO) {
		DeliveryItemDO deliveryItemDO = new DeliveryItemDO();
		deliveryItemDO.setPk_delivery_advice(deliveryAdviceDO.getPk_delivery_advice());
		deliveryItemDO.setEnterpriseid(deliveryAdviceDO.getEnterpriseid());
		List<DeliveryItemVO> deliveryItemList = deliveryItemService.queryListByDeliveryAdviceId(deliveryItemDO);
		DeliveryAdviceVO deliveryAdviceVO = deliveryAdviceService.getRestByPrimaryKey(deliveryAdviceDO);
		deliveryAdviceVO.setDeliveryItemList(deliveryItemList);
		return deliveryAdviceVO; 
	}
	/**
	 * 
	 * @Title: modify 
	 * @Description: 编辑后更新数据
	 * @param deliveryAdviceVO
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg modify(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo,String delIds) {
		deliveryAdviceVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return deliveryAdviceService.modify(deliveryAdviceVO,userInfo,delIds);
	}
	
	/**
	 * 
	 * @Title: delete 
	 * @Description: 删除数据
	 * @param deliveryAdviceList
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg delete(DeliveryAdviceList deliveryAdviceList, SystemUserInfoT userInfo) {
		int count = 0;
		for (DeliveryAdviceVO bean : deliveryAdviceList.getDeliveryAdviceList()) {
			int m = deliveryAdviceService.delete(bean,userInfo);
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
	
	/**
	 * 
	 * @Title: approve 
	 * @Description: 审核
	 * @param insertDO
	 * @param userInfo
	 * @param sessionId
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approve(DeliveryAdviceList deliveryAdviceList, SystemUserInfoT userInfo, String sessionId) {
		DeliveryAdviceVO bean = deliveryAdviceList.getDeliveryAdviceList().get(0);
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return deliveryAdviceService.approve(bean,userInfo,sessionId);
	}
	/**
	 * 
	 * @Title: checkApproveById 
	 * @Description: 校验是否有审核权限
	 * @param deliveryAdviceVO
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg checkApproveById(DeliveryAdviceVO deliveryAdviceVO, SystemUserInfoT userInfo) {
		DeliveryAdviceDO bean = new DeliveryAdviceDO();
		bean.setPk_delivery_advice(deliveryAdviceVO.getPk_delivery_advice());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(deliveryAdviceVO.getTime_stamp());
		if(deliveryAdviceService.checkDeliveryAdviceTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		String roleName = deliveryAdviceService.getApproveRoleCodes(deliveryAdviceVO.getPk_delivery_advice(),userInfo);
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
	/**
	 * 
	 * @Title: submitModel 
	 * @Description: 提交流程
	 * @param deliveryAdviceVO
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg submitModel(DeliveryAdviceVO deliveryAdviceVO,
			SystemUserInfoT userInfo) {
		return deliveryAdviceService.submitModel(deliveryAdviceVO,userInfo);
	}
	/**
	 * 
	 * @Title: approveLog 
	 * @Description: 查看审核流程
	 * @param deliveryAdviceVO
	 * @param userInfo
	 * @return
	 * @return: ResultMsg
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg approveLog(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo) {
		DeliveryAdviceDO bean = new DeliveryAdviceDO();
		bean.setPk_delivery_advice(deliveryAdviceVO.getPk_delivery_advice());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setTime_stamp(deliveryAdviceVO.getTime_stamp());
		if(deliveryAdviceService.checkDeliveryAdviceTimeStamp(bean)){
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return deliveryAdviceService.approveLog(deliveryAdviceVO,userInfo);
	}
}
