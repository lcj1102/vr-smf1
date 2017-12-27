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
import com.suneee.smf.smf.dao.CollConfirmErrDao;
import com.suneee.smf.smf.dao.CollectionConfirmDao;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import com.suneee.smf.smf.model.CollectionconfirmErrorDO;
import com.suneee.smf.smf.model.CollectionconfirmErrorVO;

/**
 * 
 * @Description: TODO service
 * @author: yunhe
 * @date: 2017年12月8日 14:54:37
 */
@Service("collConfirmErrService")
public class CollConfirmErrService {

	@Autowired
	private CollConfirmErrDao collConfirmErrDao;

	@Autowired
	private CollectionConfirmDao collectionConfirmDao;
	
	//分页查询
	public Page<CollectionconfirmErrorVO> selectByPage(CollectionconfirmErrorDO bean,Page<CollectionconfirmErrorVO> page){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", bean.getCode());
		map.put("enterprise_payment_name", bean.getEnterprise_payment_name());
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
		List<CollectionconfirmErrorVO> list = collConfirmErrDao.selectByPage(map);
		int totalCount = collConfirmErrDao.countByPage(map).intValue();
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
	public CollectionconfirmErrorVO getByPrimaryKey(CollectionconfirmErrorDO bean) {
		return collConfirmErrDao.getByPrimaryKey(bean);
	}
	
	/**
	 * @Title: insertCollectionconfirmError 
	 * @author:致远
	 * @Description: 保存收款确认单 和 收款异常记录
	 * @param modelDO 收款确认单
	 * @param userInfo
	 * @return
	 * @throws MsgException
	 * @return: int
	 */
	public int insertCollectionconfirmError(CollectionConfirmDO modelDO) throws MsgException {
		/*modelDO.setBookindate(new Date());
		modelDO.setEnterpriseid(138l);
		int m = collectionConfirmDao.add(modelDO);
		if (m != 1) {
			throw new MsgException("0", "插入收款确认单失败");
		}*/
		CollectionconfirmErrorDO errorDO = new CollectionconfirmErrorDO();
		errorDO.setPk_collection_confirm(modelDO.getPk_collection_confirm());
		errorDO.setCode(modelDO.getCode());
//		errorDO.setError_msg(""); 分账异常时回写字段
		errorDO.setPk_enterprise_payment(modelDO.getPk_enterprise_payment());
		errorDO.setEnterprise_payment_name(modelDO.getEnterprise_payment_name());
		errorDO.setContact(modelDO.getContact());
		errorDO.setContact_number(modelDO.getContact_number());
		errorDO.setPayment_method(modelDO.getPayment_method());
		errorDO.setPayment_billno(modelDO.getPayment_billno());
		errorDO.setPayment_account(modelDO.getPayment_account());
		errorDO.setPayment_bank(modelDO.getPayment_bank());
		errorDO.setCollection_amount(modelDO.getCollection_amount());
		errorDO.setPk_currency(modelDO.getPk_currency());
		errorDO.setCurrency_name(modelDO.getCurrency_name());
		errorDO.setBusi_date(modelDO.getBusi_date());
		errorDO.setSourcebilltype(modelDO.getSourcebilltype());
		errorDO.setSourcebillid(modelDO.getSourcebillid());
		errorDO.setState(Constant.COLLECTIONCONFIRMERROR_STATE_DEAL);
		errorDO.setInputmanid(modelDO.getInputmanid());
		errorDO.setInputmanname(modelDO.getInputmanname());
		errorDO.setBookindate(new Date());
		errorDO.setEnterpriseid(modelDO.getEnterpriseid());
		int m1 = collConfirmErrDao.insert(errorDO);
		if (m1 != 1) {
			throw new MsgException("0", "插入收款确认异常失败");
		}
		return 1;
	}
	
	 //查出所有的收款确认异常数据
    public List<CollectionconfirmErrorVO> getAllPriamryKey(){
    	return collConfirmErrDao.getAllPriamryKey();
    };

	public void update(CollectionconfirmErrorVO bean) {
		collConfirmErrDao.update(bean);
	}

	public void deleteErrorDOList(CollectionConfirmVO collectionConfirmVO) {
		collConfirmErrDao.deleteErrorDOList(collectionConfirmVO);
	}
	
	public void updateErrorState(CollectionconfirmErrorVO errorVO){
		collConfirmErrDao.updateErrorState(errorVO);
	}
	
	public CollectionConfirmDO getAllCollByPk(CollectionconfirmErrorVO errorVO){
		return collConfirmErrDao.getAllCollByPk(errorVO.getPk_collection_confirm());
	}
	
	
	
	public List<String> selDisEnterAndApp(){
		return collConfirmErrDao.selDisEnterAndApp();
	}
	
	public CollectionconfirmErrorVO selCollErrorByEnter(long enterpriseid,long pk_enterprise_application){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("enterpriseid", "enterpriseid");
		map.put("pk_enterprise_application", "pk_enterprise_application");
		return collConfirmErrDao.selCollErrorByEnter();
	}
}