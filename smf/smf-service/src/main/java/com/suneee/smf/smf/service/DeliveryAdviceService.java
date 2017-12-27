package com.suneee.smf.smf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.DeliveryAdviceDao;
import com.suneee.smf.smf.dao.DeliveryItemDao;
import com.suneee.smf.smf.model.DeliveryAdviceDO;
import com.suneee.smf.smf.model.DeliveryAdviceVO;
import com.suneee.smf.smf.model.DeliveryItemVO;

@Service("deliveryAdviceService")
public class DeliveryAdviceService {
	@Autowired
	private DeliveryAdviceDao deliveryAdviceDao;
	@Autowired
	private DeliveryItemDao deliveryItemDao;
	@Autowired
	private ActivitiUtilService activitiUtilService;
	// 分页查询
	public Page<DeliveryAdviceVO> queryByPage(Page<DeliveryAdviceVO> page,
			DeliveryAdviceVO deliveryAdviceVO) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			int startNum = (page.getPageNo() - 1) * (page.getPageSize());
			map.put("startNum", startNum);
			map.put("pageSize", page.getPageSize());
			map.put("state", deliveryAdviceVO.getState());
			map.put("searchValue",deliveryAdviceVO.getSearchValue());
			map.put("enterpriseid", deliveryAdviceVO.getEnterpriseid());
			map.put("code", deliveryAdviceVO.getCode());
			map.put("name", deliveryAdviceVO.getName());
			map.put("enterprise_name", deliveryAdviceVO.getEnterprise_name());
			map.put("starttime", deliveryAdviceVO.getStarttime());
			map.put("endtime", deliveryAdviceVO.getEndtime());
			List<DeliveryAdviceVO> list = deliveryAdviceDao.queryByPage(map);
			page.setResults(list); // 待分页的数据
			int totalCount = (int) deliveryAdviceDao.countByExample(map);
			page.setTotalCount(totalCount); // 总条数
			page.setPageCount(totalCount / page.getPageSize() + 1); // 总页数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	//新增
	public ResultMsg add(DeliveryAdviceVO deliveryAdviceVO, SystemUserInfoT userInfo) {
			//验证单号是否重复
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("code",deliveryAdviceVO.getCode());
			map.put("enterpriseid",userInfo.getEnterpriseid());
			map.put("pk_delivery_advice",deliveryAdviceVO.getPk_delivery_advice());
			if (deliveryAdviceDao.countByCode(map) > 0) {
				return new ResultMsg("0", "发货通知单号重复");
			}
			deliveryAdviceVO.setInputmanid(userInfo.getUserId().longValue());
			deliveryAdviceVO.setInputmanname(userInfo.getName());
			deliveryAdviceVO.setBookindate(new Date());
			deliveryAdviceVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			int m = deliveryAdviceDao.insert(deliveryAdviceVO);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新");
			}
			for (DeliveryItemVO item : deliveryAdviceVO.getDeliveryItemList()) {
				item.setPk_delivery_advice(deliveryAdviceVO.getPk_delivery_advice());
				item.setInputmanid(userInfo.getUserId().longValue());
				item.setInputmanname(userInfo.getName());
				item.setBookindate(new Date());
				item.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				deliveryItemDao.insert(item);
			}
			
			if (Constant.DELIVERYADVICE_STATE_APPROVING.equals(deliveryAdviceVO.getState())) {
				return startTaskForDeliveryAdvice(deliveryAdviceVO,userInfo);
			}
			return new ResultMsg("1", "保存成功！");
		}
	
	//分账结算调用新增收货通知
	public ResultMsg insert(DeliveryAdviceVO deliveryAdviceVO) throws MsgException {
			//验证单号是否重复
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("code",deliveryAdviceVO.getCode());
			map.put("enterpriseid",deliveryAdviceVO.getEnterpriseid());
			map.put("pk_delivery_advice",deliveryAdviceVO.getPk_delivery_advice());
			if (deliveryAdviceDao.countByCode(map) > 0) {
				return new ResultMsg("0", "发货通知单号重复");
			}
			deliveryAdviceVO.setBookindate(new Date());
			int m = deliveryAdviceDao.insert(deliveryAdviceVO);
			if (m != 1) {
				throw new MsgException("0", "保存失败");
			}
			for (DeliveryItemVO item : deliveryAdviceVO.getDeliveryItemList()) {
				item.setPk_delivery_advice(deliveryAdviceVO.getPk_delivery_advice());
				item.setInputmanid(deliveryAdviceVO.getInputmanid());
				item.setInputmanname(deliveryAdviceVO.getInputmanname());
				item.setBookindate(new Date());
				item.setEnterpriseid(deliveryAdviceVO.getEnterpriseid());
				int i = deliveryItemDao.insert(item);
				if(i != 1){
					throw new MsgException("0", "保存失败");
				}
			}
			
			return new ResultMsg("1", "保存成功！");		
	}
	
	//根据主键id查询
	public DeliveryAdviceVO getRestByPrimaryKey(DeliveryAdviceDO deliveryAdviceDO) {
		return deliveryAdviceDao.getRestByPrimaryKey(deliveryAdviceDO);
	}
	
	//更新数据
	public ResultMsg modify(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo,String delIds) {
		//验证单号是否重复
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code",deliveryAdviceVO.getCode());
		map.put("enterpriseid",userInfo.getEnterpriseid());
		map.put("pk_delivery_advice",deliveryAdviceVO.getPk_delivery_advice());
		if (deliveryAdviceDao.countByCode(map) > 0) {
			return new ResultMsg("0", "发货通知单号重复");
		}
		deliveryAdviceVO.setModiferid(userInfo.getUserId().longValue());
		deliveryAdviceVO.setModifername(userInfo.getName());
		deliveryAdviceVO.setModefydate(new Date());
		deliveryAdviceVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		int m = deliveryAdviceDao.update(deliveryAdviceVO);
		if (m != 1) {
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		//删除子表数据
		if(delIds != null && !"".equals(delIds)){
			String[] ids = delIds.split(",");
			for (String id : ids) {
				DeliveryItemVO deliveryItemVO = new DeliveryItemVO();
				deliveryItemVO.setPk_delivery_item(Long.valueOf(id));
				deliveryItemVO.setCancelid(userInfo.getUserId().longValue());
				deliveryItemVO.setCancelname(userInfo.getUserName());
				deliveryItemVO.setCanceldate(new Date());
				deliveryItemVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				deliveryItemDao.deleteById(deliveryItemVO);
			}
		}
		
		//对子表进行新增或者删除
		List<DeliveryItemVO> deliveryItemList = deliveryAdviceVO.getDeliveryItemList();
		if(deliveryItemList != null && deliveryItemList.size() > 0){
			for (DeliveryItemVO deliveryItemVO : deliveryItemList) {
				//新增子表数据
				if(deliveryItemVO.getPk_delivery_item() == null){
					deliveryItemVO.setPk_delivery_advice(deliveryAdviceVO.getPk_delivery_advice());
					deliveryItemVO.setInputmanid(userInfo.getUserId().longValue());
					deliveryItemVO.setInputmanname(userInfo.getName());
					deliveryItemVO.setBookindate(new Date());
					deliveryItemVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
					deliveryItemDao.insert(deliveryItemVO);
				}else{
					if(deliveryItemVO.getUpdateFlag() != null){
						//更新子表数据
						deliveryItemVO.setModiferid(userInfo.getUserId().longValue());
						deliveryItemVO.setModifername(userInfo.getName());
						deliveryItemVO.setModefydate(new Date());
						deliveryItemVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
						deliveryItemDao.updateById(deliveryItemVO);
					}
				}
			}
		}
		if (Constant.DELIVERYADVICE_STATE_APPROVING.equals(deliveryAdviceVO.getState())) {
			return startTaskForDeliveryAdvice(deliveryAdviceVO,userInfo);
		}
		return new ResultMsg("1", "保存成功！");
	}
	
	//删除
	public int delete(DeliveryAdviceVO bean, SystemUserInfoT userInfo) {
		bean.setCanceldate(new Date());
		bean.setCancelid(userInfo.getUserId().longValue());
		bean.setCancelname(userInfo.getName());
		bean.setState(Constant.DELIVERYADVICE_STATE_DELETE);
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return deliveryAdviceDao.delete(bean);
	}
	//审核
	public ResultMsg approve(DeliveryAdviceVO bean, SystemUserInfoT userInfo, String sessionId) {
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		msg.setMsg("审核成功");
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(bean.getType())) { //通过
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(bean.getPk_delivery_advice(),Constant.DELIVERYADVICE_APPROVE_CODE,Constant.DELIVERYADVICE_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				bean.setState(Constant.DELIVERYADVICE_STATE_APPROVED);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			} else {
				bean.setState(Constant.DELIVERYADVICE_STATE_APPROVING);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			}
			int m = deliveryAdviceDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("disagree".equals(bean.getType())){ //退回上一层
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(bean.getPk_delivery_advice(),Constant.DELIVERYADVICE_APPROVE_CODE,Constant.DELIVERYADVICE_APPROVE_FRISTTASK);
			if (isFristNode) {
				bean.setState(Constant.DELIVERYADVICE_STATE_NEW);
			} else {
				bean.setState(Constant.DELIVERYADVICE_STATE_APPROVING);
			}
			int m = deliveryAdviceDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("end".equals(bean.getType())){//终止
			taskstatus = -1;
			bean.setState(Constant.DELIVERYADVICE_STATE_END);
			int m = deliveryAdviceDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("reject".equals(bean.getType())){//驳回
			taskstatus = 2;
			bean.setState(Constant.DELIVERYADVICE_STATE_NEW);
			int m = deliveryAdviceDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else {
			msg.setCode("0");
			msg.setMsg("审核参数不正确！");
			return msg;
		}
		activitiUtilService.approve(Constant.DELIVERYADVICE_APPROVE_CODE, String.valueOf(bean.getPk_delivery_advice()), bean.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
	}
	//时间戳验证
	public boolean checkDeliveryAdviceTimeStamp(DeliveryAdviceDO deliveryAdviceDO){
		return deliveryAdviceDao.checkDeliveryAdviceTimeStamp(deliveryAdviceDO)<1;
	}
	public String getApproveRoleCodes(Long id, SystemUserInfoT userInfo) {
		return activitiUtilService.getApproveRoleCodes(id, Constant.DELIVERYADVICE_APPROVE_CODE, userInfo);
	}
	//提交审核
	public ResultMsg submitModel(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo) {
		deliveryAdviceVO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return startTaskForDeliveryAdvice(deliveryAdviceVO,userInfo);
	}
	//开启流程
	private ResultMsg startTaskForDeliveryAdvice(DeliveryAdviceVO deliveryAdviceVO, SystemUserInfoT userInfo) {
		String deliveryAdviceId = String.valueOf(deliveryAdviceVO.getPk_delivery_advice());
		ResultMsg msg = activitiUtilService.startApprove(Constant.DELIVERYADVICE_APPROVE_NAME,deliveryAdviceId, Constant.DELIVERYADVICE_APPROVE_CODE, userInfo);
		if ("1".equals(msg.getCode())) {
			// 设置提交时，补充协议的状态:2-待审核
			deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_APPROVING);
			deliveryAdviceDao.approve(deliveryAdviceVO);
		} else {
			return msg;
		}
		return new ResultMsg("1", "提交成功");
	}
	//查看审核流程
	public ResultMsg approveLog(DeliveryAdviceVO deliveryAdviceVO,SystemUserInfoT userInfo) {
		return activitiUtilService.getFlowPlan(Constant.DELIVERYADVICE_APPROVE_CODE,String.valueOf(deliveryAdviceVO.getPk_delivery_advice()));
	}
	
}
