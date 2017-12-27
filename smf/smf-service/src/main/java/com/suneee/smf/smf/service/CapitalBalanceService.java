package com.suneee.smf.smf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.dao.AdvanceConfirmDao;
import com.suneee.smf.smf.dao.CapitalBalanceDao;
import com.suneee.smf.smf.dao.CapitalBalanceNowDao;
import com.suneee.smf.smf.dao.CollectionConfirmDao;
import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;
import com.suneee.smf.smf.model.CapitalBalanceDO;
import com.suneee.smf.smf.model.CapitalBalanceNowDO;
import com.suneee.smf.smf.model.CapitalBalanceVO;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
/**
 * @Description: 资金结余历史表
 * @author: 致远
 * @date: 2017年12月11日 下午3:12:26
 */
@Service("capitalBalanceService")
public class CapitalBalanceService {
	
	@Autowired
	private CapitalBalanceDao capitalBalanceDao;
	
	@Autowired
	private CapitalBalanceNowDao capitalBalanceNowDao;

	@Autowired
	private AdvanceConfirmDao advanceConfirmDao;
	
	@Autowired
	private CollectionConfirmDao collectionConfirmDao;
	
	/**
	 * @Title: refreshBalanceTask 
	 * @author:致远
	 * @Description: 定时任务计算资金结余
	 * @throws MsgException
	 * @return: void
	 */
	public void refreshBalanceTask() throws MsgException {
		//1 查询资金结余
		List<CapitalBalanceNowDO> balanceList = capitalBalanceNowDao.selectNow();
		if (balanceList.isEmpty()) {
			throw new MsgException("0", "资金结余无数据");
		}
		//2 统计无记账标识的收、付款单 计算
		//放款列表
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("booked_flag", Constant.CAPITALBALANCE_FOR_COLLECTIONCONFIRM_DEAL);
		List<AdvanceConfirmVO> advanceConfirmList = advanceConfirmDao.selectForBalance(map);
		//收款列表
		List<CollectionConfirmVO> collectionConfirmList = collectionConfirmDao.selectForBalance(map);
		double advanceAmount = 0.0;
		double collectionAmount = 0.0;
		for (AdvanceConfirmVO advanceConfirm : advanceConfirmList) {
			if (advanceConfirm.getAdvances_amount() != null) {
				advanceAmount = advanceAmount + advanceConfirm.getAdvances_amount();
			}
		}
		for (CollectionConfirmVO collectionConfirm : collectionConfirmList) {
			if (collectionConfirm.getCollection_amount() != null) {
				collectionAmount = collectionAmount + collectionConfirm.getCollection_amount();
			}
		}
		//3 更新结余表的结余金额      
		CapitalBalanceNowDO balanceNowDO = balanceList.get(0);
		balanceNowDO.setAmount(collectionAmount-advanceAmount+balanceNowDO.getAmount());
		int m = capitalBalanceNowDao.updateBalanceNow(balanceNowDO);
		if (m != 1) {
			throw new MsgException("0", "更新结余失败！！！");
		}
		//4 资金结余历史添加记录 （最新）
		CapitalBalanceDO balanceDO = new CapitalBalanceDO();
		balanceDO.setAmount(balanceNowDO.getAmount());
		balanceDO.setBusi_time(new Date());
		balanceDO.setCurrency_name(balanceNowDO.getCurrency_name());
		balanceDO.setEnterpriseid(balanceNowDO.getEnterpriseid());
		balanceDO.setPk_currency(balanceNowDO.getPk_currency());
		balanceDO.setState(Constant.CAPITALBALANCE_STATE_NEW);
		balanceDO.setTime_stamp(new Date());
		int m1 = capitalBalanceDao.insertCapitalBalance(balanceDO);
		if (m1 != 1) {
			throw new MsgException("0", "更新结余失败！！！");
		}
		//5 更新收、付款单记账标识为“已记账”
		for (AdvanceConfirmVO advanceConfirm : advanceConfirmList) {
			AdvanceConfirmDO confirmDO = new AdvanceConfirmDO();
			confirmDO.setPk_advance_confirm(advanceConfirm.getPk_advance_confirm());
			confirmDO.setBooked_flag(Constant.CAPITALBALANCE_FOR_COLLECTIONCONFIRM_FINISH);
			confirmDO.setEnterpriseid(advanceConfirm.getEnterpriseid());
			confirmDO.setTime_stamp(advanceConfirm.getTime_stamp());
			int a = advanceConfirmDao.updateForBalance(confirmDO);
			if (a != 1) {
				throw new MsgException("0", "更新放款失败！！！");
			}
		}
		for (CollectionConfirmVO collectionConfirm : collectionConfirmList) {
			CollectionConfirmDO confirmDO = new CollectionConfirmDO();
			confirmDO.setPk_collection_confirm(collectionConfirm.getPk_collection_confirm());
			confirmDO.setBooked_flag(Constant.CAPITALBALANCE_FOR_COLLECTIONCONFIRM_FINISH);
			confirmDO.setEnterpriseid(collectionConfirm.getEnterpriseid());
			confirmDO.setTime_stamp(collectionConfirm.getTime_stamp());
			int a = collectionConfirmDao.updateForBalance(confirmDO);
			if (a != 1) {
				throw new MsgException("0", "更新收款失败！！！");
			}
		}
	}

	//分页查询
	public Page<CapitalBalanceVO> selectByPage(CapitalBalanceDO bean,Page<CapitalBalanceVO> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("beginDate", bean.getBeginDate());
		map.put("endDate", bean.getEndDate());
		map.put("type", bean.getType());
		map.put("enterpriseid", bean.getEnterpriseid());
		//分页字段
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		//返回的分页数据
		List<CapitalBalanceVO> list = capitalBalanceDao.selectByPage(map);
		int totalCount = capitalBalanceDao.countByPage(map).intValue();
		page.setResults(list);	//待分页数据
        page.setTotalCount(totalCount); // 总条数
        if (totalCount % page.getPageSize() != 0) {
        	page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		}else {
			page.setPageCount(totalCount / page.getPageSize()); //总页数
		}
		return page;
	}
	//查询所有
	public Page<CapitalBalanceVO> balanceList(CapitalBalanceDO bean,Page<CapitalBalanceVO> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseid", bean.getEnterpriseid());
		map.put("beginDate", bean.getBeginDate());
		map.put("endDate", bean.getEndDate());
		map.put("type", bean.getType());
		//返回的分页数据
		List<CapitalBalanceVO> list = capitalBalanceDao.balanceList(map);
		page.setResults(list);	//待分页数据
		return page;
	}
}
