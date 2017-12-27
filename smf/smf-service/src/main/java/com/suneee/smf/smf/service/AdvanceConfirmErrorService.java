package com.suneee.smf.smf.service;

import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.dao.AdvanceConfirmErrorDao;
import com.suneee.smf.smf.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 
 * @Description: TODO service
 * @author: changzhaoyu
 * @date: 2017年12月19日 12:54:37
 */
@Service("advanceConfirmErrorService")
public class AdvanceConfirmErrorService {
	
	@Autowired
	private AdvanceConfirmErrorDao advanceConfirmErrorDao;
			
	//分页查询
	public Page<AdvanceConfirmErrorVO> selectByPage(AdvanceConfirmErrorDO bean,Page<AdvanceConfirmErrorVO> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", bean.getCode());
		map.put("enterprise_application_name", bean.getEnterprise_application_name());
		map.put("busi_date", bean.getBusi_date());
		map.put("time_stamp", bean.getTime_stamp());
		map.put("beginDateTS", bean.getBeginDateTS());
		map.put("endDateTS", bean.getEndDateTS());
		map.put("beginDateBD", bean.getBeginDateBD());
		map.put("endDateBD", bean.getEndDateBD());
		map.put("searchValue", bean.getSearchValue());
		map.put("state_new", bean.getState_new());
		map.put("state_end", bean.getState_end());
		map.put("enterpriseid", bean.getEnterpriseid());
		//分页字段
		int startNum = (page.getPageNo()-1)*(page.getPageSize());
		map.put("startNum",startNum);
		map.put("pageSize",page.getPageSize());
		//返回的分页数据
		List<AdvanceConfirmErrorVO> list = advanceConfirmErrorDao.selectByPage(map);
		int totalCount = advanceConfirmErrorDao.countByPage(map).intValue();
		page.setResults(list);	//待分页数据
        page.setTotalCount(totalCount); // 总条数
        if (totalCount % page.getPageSize() != 0) {
        	page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
		}else {
			page.setPageCount(totalCount / page.getPageSize()); //总页数
		}
		return page;
	}
	
	
	//根据code查询
	public AdvanceConfirmErrorVO getByPrimaryKey(AdvanceConfirmErrorDO bean) {
		return advanceConfirmErrorDao.getByPrimaryKey(bean);
	}
	
	//查出所有的放款确认异常数据
    public List<AdvanceConfirmErrorVO> getAllPriamryKey(){
    	return advanceConfirmErrorDao.getAllPriamryKey();
    };
    
    //修改相关的放款异常记录为已处理
    public void updateErrorDOState(AdvanceConfirmVO advanceConfirmVO) {
		advanceConfirmErrorDao.updateErrorDOState(advanceConfirmVO);
	}
    
    //更新放款异常记录
    public void update(AdvanceConfirmErrorVO bean) {
		advanceConfirmErrorDao.update(bean);
	}

	/**
	 * @Description: ${放款异常数据插入}
	 * @author: 超杰
	 * @date: ${date} ${time}
	 * ${tags}
	 */
	public int insertAdvanceconfirmError(AdvanceConfirmDO modelDO) throws MsgException {
		AdvanceConfirmErrorDO errorDO=new AdvanceConfirmErrorDO();
		errorDO.setPk_advance_confirm(modelDO.getPk_advance_confirm());
		errorDO.setCode(modelDO.getCode());
		errorDO.setPk_enterprise_application(modelDO.getPk_enterprise_application());
		errorDO.setEnterprise_application_name(modelDO.getEnterprise_application_name());
		errorDO.setContact(modelDO.getContact());
		errorDO.setContact_number(modelDO.getContact_number());
		errorDO.setApplication_amount(modelDO.getApplication_amount());
		errorDO.setAdvances_amount(modelDO.getAdvances_amount());
		errorDO.setPk_currency(modelDO.getPk_currency());
		errorDO.setCurrency_name(modelDO.getCurrency_name());
		errorDO.setApplication_date(modelDO.getApplication_date());
		errorDO.setBusi_date(modelDO.getBusi_date());
		errorDO.setPayment_method(modelDO.getPayment_method());
		errorDO.setPayment_billno(modelDO.getPayment_billno());
		errorDO.setBank_account(modelDO.getBank_account());
		errorDO.setBank_deposit(modelDO.getBank_deposit());
		errorDO.setSourcebilltype(modelDO.getSourcebilltype());
		errorDO.setSourcebillid(modelDO.getSourcebillid());
		errorDO.setState(Constant.ADVANCECONFIRMERROR_STATE_DEAL);
		errorDO.setInputmanid(modelDO.getInputmanid());
		errorDO.setInputmanname(modelDO.getInputmanname());
		errorDO.setBookindate(new Date());
		errorDO.setEnterpriseid(modelDO.getEnterpriseid());
		errorDO.setTime_stamp(modelDO.getTime_stamp());
		int m = advanceConfirmErrorDao.insert(errorDO);
		if (m != 1) {
			throw new MsgException("0", "插入放款确认异常失败");
		}
		return 1;
	}

	//删除放款异常
	public void deleteErrorDO(AdvanceConfirmVO advanceConfirmVO) {
		advanceConfirmErrorDao.deleteErrorDO(advanceConfirmVO);
	}

	//获取放款通知
	public AdvanceConfirmDO getAllAdvanceByPk(AdvanceConfirmErrorVO bean) {
		return advanceConfirmErrorDao.getAllAdvanceByPk(bean.getPk_advance_confirm());
	}
}
