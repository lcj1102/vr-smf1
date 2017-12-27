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
import com.suneee.smf.smf.dao.CapitalApplicationDao;
import com.suneee.smf.smf.dao.SaleApplicationDao;
import com.suneee.smf.smf.dao.SaleApplicationItemDao;
import com.suneee.smf.smf.model.CapitalApplicationItemVO;
import com.suneee.smf.smf.model.CapitalApplicationVO;
import com.suneee.smf.smf.model.SaleApplicationDO;
import com.suneee.smf.smf.model.SaleApplicationInsertDO;
import com.suneee.smf.smf.model.SaleApplicationItemDO;
import com.suneee.smf.smf.model.SaleApplicationItemVO;
import com.suneee.smf.smf.model.SaleApplicationVO;

/**
 * 
 * @Description: TODO 发货申请service
 * @author: 崔亚强
 * @date: 2017年12月7日 下午3:54:37
 */
@Service("saleApplicationService")
public class SaleApplicationService {

	@Autowired
	private SaleApplicationDao saleApplicationDao;
	@Autowired
	private SaleApplicationItemDao saleApplicationItemDao;
	@Autowired
	private ActivitiUtilService activitiUtilService;
	@Autowired
	private CollectionConfirmService collectionConfirmService;
	@Autowired
	private CapitalApplicationDao capitalApplicationDao;
	
	@Autowired
	private EnterpriseSettlementService enterpriseSettlementService;
	
	//分页查询
	public Page<SaleApplicationVO> selectByPage(SaleApplicationDO bean,Page<SaleApplicationVO> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", bean.getCode());
		map.put("capital_application_code", bean.getCapital_application_code());
		map.put("enterprise_application_name", bean.getEnterprise_application_name());
		map.put("enterprise_payment_name", bean.getEnterprise_payment_name());
		map.put("minAmount", bean.getMinAmount());
		map.put("maxAmount", bean.getMaxAmount());
		map.put("beginDate", bean.getBeginDate());
		map.put("endDate", bean.getEndDate());
		map.put("searchValue", bean.getSearchValue());
		map.put("state", bean.getState());
		map.put("enterpriseid", bean.getEnterpriseid());
		//分页字段
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		//返回的分页数据
		List<SaleApplicationVO> list = saleApplicationDao.selectByPage(map);
		int totalCount = saleApplicationDao.countByPage(map).intValue();
		page.setResults(list);	//待分页数据
        page.setTotalCount(totalCount); // 总条数
        if (totalCount % page.getPageSize() != 0) {
        	page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		}else {
			page.setPageCount(totalCount / page.getPageSize()); //总页数
		}
		return page;
	}
	
	//新增
	public ResultMsg insertDO(SaleApplicationDO bean,SystemUserInfoT userInfo) throws MsgException {
		if (bean == null) {
            return new ResultMsg("5", "数据为空");
        }
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		if (saleApplicationDao.countByCode(bean) > 0) {
			return new ResultMsg("2", "申请单号重复");
		}
		//验证发货明细的最大数量
		CapitalApplicationVO vo = new CapitalApplicationVO();
		vo.setPk_capital_application(bean.getPk_capital_application());
		vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		List<CapitalApplicationItemVO> list = capitalApplicationDao.getItemListById(vo);
		Map<Long,CapitalApplicationItemVO> itemMap = new HashMap<Long, CapitalApplicationItemVO>();
		for (CapitalApplicationItemVO item : list) {
			itemMap.put(item.getPk_capitalapplication_item(), item);
		}
		for (SaleApplicationItemDO item : bean.getItemList()) {
			if (itemMap.get(item.getSourcebillitemid()) != null) {
				if (item.getNumber() > itemMap.get(item.getSourcebillitemid()).getMaxNumber()) {
					return new ResultMsg("0", itemMap.get(item.getSourcebillitemid()).getGoods_name()+"最大发货数量为："+itemMap.get(item.getSourcebillitemid()).getMaxNumber());
				}
			} else {
				return new ResultMsg("0", "保存失败");
			}
		}
		bean.setInputmanid(userInfo.getUserId().longValue());
		bean.setInputmanname(userInfo.getName());
		bean.setBookindate(new Date());
		int m = saleApplicationDao.insertDO(bean);
		if (m != 1) {
			throw new MsgException("0", "保存失败");
		}
		//插入子单数据
		for (SaleApplicationItemDO item : bean.getItemList()) {
			item.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			item.setPk_sale_application(bean.getPk_sale_application());
			int n = saleApplicationItemDao.insertItemDO(item);
			if (n != 1){
				throw  new MsgException("0","保存失败");
			}
		}
		if (Constant.SALEAPPLICATION_STATE_APPROVING.equals(bean.getState())) {
			return startTaskForSaleApplication(bean,userInfo);
		}
		return new ResultMsg("1", "保存成功！");
	}
	
	//作废
	public ResultMsg delete(SaleApplicationInsertDO insertDO, SystemUserInfoT userInfo) {
		int count = 0;
		for (SaleApplicationDO bean : insertDO.getInsertDO()) {
			bean.setCanceldate(new Date());
			bean.setCancelid(userInfo.getUserId().longValue());
			bean.setCancelname(userInfo.getName());
			bean.setState(Constant.SALEAPPLICATION_STATE_DELETE);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			int m = saleApplicationDao.delete(bean);
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
	
	//审核
	public ResultMsg approve(SaleApplicationDO bean, SystemUserInfoT userInfo, String sessionId) throws MsgException, InterruptedException {
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(bean.getType())) { //通过
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(bean.getPk_sale_application(),Constant.SALEAPPLICATION_APPROVE_CODE,Constant.SALEAPPLICATION_APPROVE_LASTTASK);
			if (isLastNode) {
				// 判断审核流程是否结束
				bean.setState(Constant.SALEAPPLICATION_STATE_APPROVED);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				msg.setHtml(Constant.APPROVE_SUCCESS);


				//企业结算表数据加锁
				enterpriseSettlementService.lockEnterpriseSettlement(bean.getPk_enterprise_application(), bean.getEnterpriseid());
				//修改企业结算单物流费和监管费
				try {
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("logistics_cost", bean.getLogistics_cost());
					map.put("supervision_cost", bean.getSupervision_cost());
					map.put("pk_enterprise", bean.getPk_enterprise_application());
					map.put("enterpriseid", userInfo.getEnterpriseid().longValue());
					saleApplicationDao.altCost(map);
					//解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(bean.getPk_enterprise_application(), bean.getEnterpriseid());
				} catch (Exception e) {
					//解锁
					enterpriseSettlementService.unlockEnterpriseSettlement(bean.getPk_enterprise_application(), bean.getEnterpriseid());
					e.printStackTrace();
					throw new MsgException("0", "修改企业结算单物流费和监管费时，企业结算表被锁！");
				}

			} else {
				bean.setState(Constant.SALEAPPLICATION_STATE_APPROVING);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			}
			int m = saleApplicationDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("disagree".equals(bean.getType())){ //退回上一层
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(bean.getPk_sale_application(),Constant.SALEAPPLICATION_APPROVE_CODE,Constant.SALEAPPLICATION_APPROVE_FRISTTASK);
			if (isFristNode) {
				bean.setState(Constant.SALEAPPLICATION_STATE_NEW);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			} else {
				bean.setState(Constant.SALEAPPLICATION_STATE_APPROVING);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			}
			int m = saleApplicationDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("end".equals(bean.getType())){//终止
			taskstatus = -1;
			bean.setState(Constant.SALEAPPLICATION_STATE_END);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			int m = saleApplicationDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("reject".equals(bean.getType())){//驳回
			taskstatus = 2;
			bean.setState(Constant.SALEAPPLICATION_STATE_NEW);
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			int m = saleApplicationDao.approve(bean);
			if (m != 1) {
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else {
			msg.setCode("0");
			msg.setMsg("审核参数不正确！");
			return msg;
		}

		msg.setHtml(String.valueOf(taskstatus));
		//activitiUtilService.approve(Constant.SALEAPPLICATION_APPROVE_CODE, String.valueOf(bean.getPk_sale_application()), bean.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
	}


	
	//根据id查询
	public SaleApplicationVO getDOByPrimaryKey(SaleApplicationDO bean) {
		SaleApplicationItemDO saleApplicationItemDO = new SaleApplicationItemDO();
		if (bean.getEnterpriseid() != null) {
			saleApplicationItemDO.setEnterpriseid(bean.getEnterpriseid());
		}
		saleApplicationItemDO.setPk_sale_application(bean.getPk_sale_application());;
		List<SaleApplicationItemVO> itemList = saleApplicationItemDao.queryListBySaleApplicationId(saleApplicationItemDO);
		SaleApplicationVO saleApplicationVO = saleApplicationDao.getDOByPrimaryKey(bean);
		saleApplicationVO.setItemList(itemList);
		return saleApplicationVO;
	}
	
	//修改
	public ResultMsg update(SaleApplicationDO saleApplicationDO,SystemUserInfoT userInfo) {
		//验证发货明细的最大数量
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pk_sale_application", saleApplicationDO.getPk_sale_application());
		map.put("pk_capital_application", saleApplicationDO.getPk_capital_application());
		map.put("enterpriseid", userInfo.getEnterpriseid().longValue());
		List<CapitalApplicationItemVO> list = capitalApplicationDao.getItemMaxById(map);
		Map<Long,CapitalApplicationItemVO> itemMap = new HashMap<Long, CapitalApplicationItemVO>();
		for (CapitalApplicationItemVO item : list) {
			itemMap.put(item.getPk_capitalapplication_item(), item);
		}
		for (SaleApplicationItemDO item : saleApplicationDO.getItemList()) {
			if (itemMap.get(item.getSourcebillitemid()) != null) {
				if (item.getNumber() > itemMap.get(item.getSourcebillitemid()).getMaxNumber()) {
					return new ResultMsg("0", itemMap.get(item.getSourcebillitemid()).getGoods_name()+"最大发货数量为："+itemMap.get(item.getSourcebillitemid()).getMaxNumber());
				}
			} else {
				return new ResultMsg("0", "保存失败");
			}
		}
		if (saleApplicationDao.countByCode(saleApplicationDO) > 0) {
			return new ResultMsg("5", "发货申请单号重复");
		}
		saleApplicationDO.setModiferid(userInfo.getUserId().longValue());
		saleApplicationDO.setModifername(userInfo.getName());
		saleApplicationDO.setModefydate(new Date());
		saleApplicationDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		int m = saleApplicationDao.update(saleApplicationDO);
		if (m != 1) {
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		SaleApplicationItemDO saleApplicationItemDO = new SaleApplicationItemDO();
		saleApplicationItemDO.setPk_sale_application(saleApplicationDO.getPk_sale_application());
		saleApplicationItemDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		saleApplicationItemDao.deleteItemBySaleApplicationId(saleApplicationItemDO);

		for (SaleApplicationItemDO item : saleApplicationDO.getItemList()) {
			item.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			item.setPk_sale_application(saleApplicationDO.getPk_sale_application());
			saleApplicationItemDao.insertItemDO(item);
		}

		if (Constant.SALEAPPLICATION_STATE_APPROVING.equals(saleApplicationDO.getState())) {
			return startTaskForSaleApplication(saleApplicationDO, userInfo);
		}
		return new ResultMsg("1", "保存成功！");
	}
	
	//提交
	public ResultMsg submitModel(SaleApplicationDO saleApplicationDO,SystemUserInfoT userInfo) {
		saleApplicationDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		return startTaskForSaleApplication(saleApplicationDO, userInfo);
	}
	
	//查看审核流程
	public ResultMsg approveLog(SaleApplicationVO saleApplicationVO,SystemUserInfoT userInfo) {
		return activitiUtilService.getFlowPlan(Constant.SALEAPPLICATION_APPROVE_CODE,String.valueOf(saleApplicationVO.getPk_sale_application()));
	}
	
	//时间戳验证
	public boolean checkSaleApplicationTimeStamp(SaleApplicationDO saleApplicationDO){
		return saleApplicationDao.checkSaleApplicationTimeStamp(saleApplicationDO)<1;
	}
	public String getApproveRoleCodes(Long id, SystemUserInfoT userInfo) {
		return activitiUtilService.getApproveRoleCodes(id, Constant.SALEAPPLICATION_APPROVE_CODE, userInfo);
	}
	
	//开启流程
	private ResultMsg startTaskForSaleApplication(SaleApplicationDO saleApplicationDO, SystemUserInfoT userInfo) {
		String saleApplicationId = String.valueOf(saleApplicationDO.getPk_sale_application());
		ResultMsg msg = activitiUtilService.startApprove(Constant.SALEAPPLICATION_APPROVE_NAME,saleApplicationId, Constant.SALEAPPLICATION_APPROVE_CODE, userInfo);
		if ("1".equals(msg.getCode())) {
			// 设置提交时，补充协议的状态:2-待审核
			saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_APPROVING);
			saleApplicationDao.approve(saleApplicationDO);
		} else {
			return msg;
		}
		return new ResultMsg("1", "提交成功");
	}
	
	//修改状态为已发货
	public ResultMsg altState(SaleApplicationDO saleApplicationDO) {
		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_SEND);
		int m = saleApplicationDao.altState(saleApplicationDO);
		if (m != 1) {
			return new ResultMsg("0", "该数据已失效，请刷新！");
		}
		return new ResultMsg("1", "保存成功！");
	}
	
	//根据资金使用申请主键查询发货申请表中已发货状态的数量 
	public double countSendNumber(long pk_capital_application,long enterpriseid) {
		SaleApplicationDO saleApplicationDO = new SaleApplicationDO();
		saleApplicationDO.setPk_capital_application(pk_capital_application);
		saleApplicationDO.setEnterpriseid(enterpriseid);
		saleApplicationDO.setState(Constant.SALEAPPLICATION_STATE_SEND);
		return saleApplicationDao.countSendNumber(saleApplicationDO);
	}
}