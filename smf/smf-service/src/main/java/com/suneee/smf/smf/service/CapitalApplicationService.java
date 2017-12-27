package com.suneee.smf.smf.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.CapitalApplicationDao;
import com.suneee.smf.smf.model.CapitalApplicationDO;
import com.suneee.smf.smf.model.CapitalApplicationItemDO;
import com.suneee.smf.smf.model.CapitalApplicationItemVO;
import com.suneee.smf.smf.model.CapitalApplicationVO;
/**
 * @Description: 资金使用申请
 * @author: 致远
 * @date: 2017年12月1日 下午4:05:10
 */
@Service("capitalApplicationService")
public class CapitalApplicationService {
	
	 private static Logger log = LoggerFactory.getLogger(CapitalApplicationService.class);

	@Autowired
	private CapitalApplicationDao capitalApplicationDao;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;
	
	public Page<CapitalApplicationVO> listByPage(Page<CapitalApplicationVO> page, CapitalApplicationDO bean) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code",bean.getCode());
		map.put("name",bean.getName());
		map.put("enterprise_name",bean.getEnterprise_name());
		map.put("searchValue",bean.getSearchValue());
		map.put("enterpriseid",bean.getEnterpriseid());
		map.put("state",bean.getState());
		
		map.put("beginDate",bean.getStartDate());
		if (bean.getEndDate() != null && !"".equals(bean.getEndDate())) {
			Calendar c = Calendar.getInstance();
			c.setTime(bean.getEndDate());
			c.add(Calendar.DAY_OF_MONTH, 1);
			Date endTime = c.getTime();
			map.put("endDate", endTime);
		}
		
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		List<CapitalApplicationVO> list = capitalApplicationDao.selectByExample1(map);
		page.setResults(list);    //待分页的数据
		int totalCount = (int) capitalApplicationDao.countByExample(map);
		page.setTotalCount(totalCount); // 总条数
		page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		return page;
	}

	public ResultMsg addCapitalApplication(CapitalApplicationDO laDO,SystemUserInfoT userInfo) throws MsgException {
		//保存主表
		//验证编码唯一
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code", laDO.getCode());
		map.put("enterpriseid", userInfo.getEnterpriseid());
		if (capitalApplicationDao.checkCode(map) > 0) {
			throw new MsgException("0", "申请单号重复");
		}
		laDO.setInputmanid(userInfo.getUserId().longValue());
		laDO.setInputmanname(userInfo.getUserName());
		laDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		laDO.setBookindate(new Date());
		laDO.setSale_flag(Constant.CAPITALAPPLICATION_SALE_NEW);
		int m = capitalApplicationDao.addCapitalApplication(laDO);
		if (m != 1) {
			throw new MsgException("0", "保存失败");
		}
		//校验 资金平台的结余资金加上“未记账”状态的收款单金额减去“未记账”状态的放款单金额再减去“未付款”状态的资金使用申请单的申请金额不能小于0。
		//资金平台的结余资金+“未记账”状态的收款单金额-“未记账”状态的放款单金额-“未付款”状态的资金使用申请单的申请金额>=0
		if (capitalApplicationDao.checkCapital(map) < 0) {
			throw new MsgException("0", "资金平台的结余资金超出");
		}
		//保存子表
		if (laDO.getItemList() != null) {
			for (CapitalApplicationItemDO itemDO : laDO.getItemList()) {
				itemDO.setPk_capital_application(laDO.getPk_capital_application());
				itemDO.setInputmanid(userInfo.getUserId().longValue());
				itemDO.setInputmanname(userInfo.getUserName());
				itemDO.setBookindate(new Date());
				itemDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				int m1 = capitalApplicationDao.addCapitalApplicationItem(itemDO);
				if (m1 != 1) {
					throw new MsgException("0", "保存失败");
				}
			}
		}
		if (Constant.CAPITALAPPLICATION_STATE_APPROVING.equals(laDO.getState())) {
			return startTaskForCapitalApplication(laDO,userInfo);
		}
		return new ResultMsg("1","保存成功！");
	}
	//开始审核流程
	public ResultMsg startTaskForCapitalApplication (CapitalApplicationDO laDO, SystemUserInfoT userInfo) {
		String enterpriseId = String.valueOf(laDO.getPk_capital_application());
 		log.info("************"+Constant.CAPITALAPPLICATION_APPROVE_NAME+"*"+Constant.CAPITALAPPLICATION_APPROVE_CODE+"***********************");
 		log.info("************getPk_capital_application:"+enterpriseId+"***********************");
		ResultMsg msg = activitiUtilService.startApprove(Constant.CAPITALAPPLICATION_APPROVE_NAME,enterpriseId, Constant.CAPITALAPPLICATION_APPROVE_CODE, userInfo);
		if ("1".equals(msg.getCode())) {
			// 设置提交时，补充协议的状态:2-待审核
			laDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
			capitalApplicationDao.approve(laDO);
		} else {
			return msg;
		}
		return new ResultMsg("1", "提交成功");
	}
	public CapitalApplicationVO getRestByPrimaryKey(CapitalApplicationVO vo) {
		CapitalApplicationVO caVO = capitalApplicationDao.getRestByPrimaryKey(vo);
		List<CapitalApplicationItemVO> itemList = capitalApplicationDao.selectItemListById(vo);
		caVO.setItemList(itemList);
		return caVO;
	}

	public ResultMsg modifyCapitalApplication(CapitalApplicationDO laDO,String delItemId, SystemUserInfoT userInfo) throws MsgException {
		//保存主表
		//验证编码唯一
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("code", laDO.getCode());
		map.put("pk_capital_application", laDO.getPk_capital_application());
		map.put("enterpriseid", userInfo.getEnterpriseid());
		if (capitalApplicationDao.checkCode(map) > 0) {
			throw new MsgException("0", "申请单号重复");
		}
		laDO.setModiferid(userInfo.getUserId().longValue());
		laDO.setModifername(userInfo.getUserName());
		laDO.setModefydate(new Date());
		laDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		int m = capitalApplicationDao.updateCapitalApplication(laDO);
		if (m != 1) {
			throw new MsgException("0", "保存失败");
		}
		if (capitalApplicationDao.checkCapital(map) < 0) {
			throw new MsgException("0", "资金平台的结余资金超出");
		}
		//保存子表
		if (laDO.getItemList() != null) {
			for (CapitalApplicationItemDO itemDO : laDO.getItemList()) {
				if (itemDO.getPk_capitalapplication_item() != null && false == "".equals(itemDO.getPk_capitalapplication_item())) {
					itemDO.setModiferid(userInfo.getUserId().longValue());
					itemDO.setModifername(userInfo.getUserName());
					itemDO.setModefydate(new Date());
					itemDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
					int m1 = capitalApplicationDao.updateCapitalApplicationItem(itemDO);
					if (m1 != 1) {
						throw new MsgException("0", "保存失败");
					}
				} else {
					itemDO.setPk_capital_application(laDO.getPk_capital_application());
					itemDO.setInputmanid(userInfo.getUserId().longValue());
					itemDO.setInputmanname(userInfo.getUserName());
					itemDO.setBookindate(new Date());
					itemDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
					int m1 = capitalApplicationDao.addCapitalApplicationItem(itemDO);
					if (m1 != 1) {
						throw new MsgException("0", "保存失败");
					}
				}
			}
		}
		//删除子表数据delItemId
		if (false == "".equals(delItemId) && delItemId != null) {
			String[] ids = delItemId.split(",");
			for (String id : ids) {
				if (false == "".equals(id)) {
					CapitalApplicationItemDO itemDO = new CapitalApplicationItemDO();
					itemDO.setPk_capitalapplication_item(Long.valueOf(id));
					itemDO.setCanceldate(new Date());
					itemDO.setCancelid(userInfo.getUserId().longValue());
					itemDO.setCancelname(userInfo.getUserName());
					itemDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
					int m1 = capitalApplicationDao.deleteCapitalApplicationItem(itemDO);
					if (m1 != 1) {
						throw new MsgException("0", "保存失败");
					}
				}
			}
		}
		if (Constant.CAPITALAPPLICATION_STATE_APPROVING.equals(laDO.getState())) {
			return startTaskForCapitalApplication(laDO,userInfo);
		}
		return new ResultMsg("1","修改成功！");
	}

	public int deleteCapitalApplication(CapitalApplicationDO bean,SystemUserInfoT userInfo) throws MsgException {
		bean.setCanceldate(new Date());
		bean.setCancelid(userInfo.getUserId().longValue());
		bean.setCancelname(userInfo.getUserName());
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setState(Constant.CAPITALAPPLICATION_STATE_DEL);
		int m = capitalApplicationDao.deleteCapitalApplication(bean);
		if (m != 1) {
			throw new MsgException("0", "删除失败");
		}
		int m1 = capitalApplicationDao.deleteCapitalApplicationItemById(bean);
		if (m1 < 0) {
			throw new MsgException("0", "删除失败");
		}
		return 1;
	}

	public String getApproveRoleCodes(Long pk_enterprise,SystemUserInfoT userInfo) {
		return activitiUtilService.getApproveRoleCodes(pk_enterprise, Constant.CAPITALAPPLICATION_APPROVE_CODE, userInfo);
	}

	public ResultMsg approveSelect(CapitalApplicationVO modelVO,SystemUserInfoT userInfo) throws MsgException {
		CapitalApplicationDO modelDO = new CapitalApplicationDO();
		modelDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		modelDO.setPk_capital_application(modelVO.getPk_capital_application());
		modelDO.setTime_stamp(modelVO.getTime_stamp());
		ResultMsg msg = new ResultMsg();
		msg.setCode("1");
		
		int taskstatus = 0;
		//获取任务
		if ("agree".equals(modelVO.getType())) { //通过
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(modelVO.getPk_capital_application(),Constant.CAPITALAPPLICATION_APPROVE_CODE,Constant.CAPITALAPPLICATION_APPROVE_LASTTASK);
			if (isLastNode) {// 判断审核流程是否结束
				modelDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVED);
            } else {
				modelDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
            }
		} else if ("disagree".equals(modelVO.getType())){ //退回上一层
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(modelVO.getPk_capital_application(),Constant.CAPITALAPPLICATION_APPROVE_CODE,Constant.CAPITALAPPLICATION_APPROVE_FRISTTASK);
			if (isFristNode) {
				modelDO.setState(Constant.CAPITALAPPLICATION_STATE_NEW);
			} else {
				modelDO.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
			}
		} else if ("end".equals(modelVO.getType())){//终止
			taskstatus = -1;
			modelDO.setState(Constant.CAPITALAPPLICATION_STATE_END);
		} else if ("reject".equals(modelVO.getType())){//驳回
			taskstatus = 2;
			modelDO.setState(Constant.CAPITALAPPLICATION_STATE_NEW);
        } else {
            throw new MsgException("0", "审核参数不正确！");
        }
		int m = capitalApplicationDao.approve(modelDO);
		if (m != 1) {
			throw new MsgException("0", "该数据已失效，请刷新");
		}
		msg.setHtml(String.valueOf(taskstatus));
//		activitiUtilService.approve(Constant.CAPITALAPPLICATION_APPROVE_CODE, String.valueOf(modelVO.getPk_capital_application()), modelVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
		return msg;
	}

	public ResultMsg approveLog(CapitalApplicationVO modelVO,SystemUserInfoT userInfo) {
		return activitiUtilService.getFlowPlan(Constant.CAPITALAPPLICATION_APPROVE_CODE,String.valueOf(modelVO.getPk_capital_application()));
	}

	public int submitModel(CapitalApplicationDO bean, SystemUserInfoT userInfo) throws MsgException {
		bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		bean.setState(Constant.CAPITALAPPLICATION_STATE_APPROVING);
		int m = capitalApplicationDao.submitModel(bean);
		if (m != 1) {
			throw new MsgException("0", "提交失败！！！");
		}
		ResultMsg msg = startTaskForCapitalApplication(bean,userInfo);
		if ("1".equals(msg.getCode())) {
			return 1;
		} else {
			throw new MsgException("0", "提交失败！！！");
		}
	}

	public List<CapitalApplicationVO> getAllList(String keyword,Long enterpriseId, SystemUserInfoT userInfo) {
		CapitalApplicationDO vo = new CapitalApplicationDO();
		vo.setPk_enterprise(enterpriseId);
		vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
		vo.setCode(keyword);
		vo.setState(Constant.CAPITALAPPLICATION_STATE_APPROVED);
		return capitalApplicationDao.getAllList(vo);
	}

	public CapitalApplicationVO getCapitalApplicationByCode(CapitalApplicationDO vo) {
		CapitalApplicationVO caVO = capitalApplicationDao.getCapitalApplicationByCode(vo);
		List<CapitalApplicationItemVO> itemList = capitalApplicationDao.selectItemListById(caVO);
		caVO.setItemList(itemList);
		return caVO;
	}
	
	/*
	 * 获取细单中的数量为可发货最大数量
	 */
	public CapitalApplicationVO selectCapitalApplicationByCode(CapitalApplicationDO vo) {
		CapitalApplicationVO caVO = capitalApplicationDao.getCapitalApplicationByCode(vo);
		List<CapitalApplicationItemVO> itemList = capitalApplicationDao.getItemListById(caVO);
		caVO.setItemList(itemList);
		return caVO;
	}
	/*
	 * 获取资金使用申请的货物总数量
	 */
	public double sumApplicationItemsNumber(Long pk_capital_application ,Long enterpriseId){
		return capitalApplicationDao.sumApplicationItemsNumber(pk_capital_application,enterpriseId);
	}
	
	/*
	 * 更新资金使用申请的状态
	 */
	public int updateCapitalApplicationSaleFlag(CapitalApplicationDO laDO){
		return capitalApplicationDao.updateCapitalApplicationSaleFlag(laDO);
	}
	
}
