package com.suneee.smf.smf.api.rest;


import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.SaleApplicationDO;
import com.suneee.smf.smf.model.SaleApplicationInsertDO;
import com.suneee.smf.smf.model.SaleApplicationVO;

/**
 * 
 * @Description: TODO 发货申请后台接口方法
 * @author: 崔亚强
 * @date: 2017年12月7日 下午4:08:27
 */
public interface SaleApplicationRestService {

    public ResultMsg selectByPage(String code,String capital_application_code,String enterprise_application_name,
    		String enterprise_payment_name,String minAmount,String maxAmount,String beginDate,String endDate,
    		String searchValue,String state,String length,String pageNum,String sessionId);

    public ResultMsg insertDO(SaleApplicationInsertDO insertDO,String sessionId);

    public ResultMsg delete(SaleApplicationInsertDO insertDO,String sessionId);
    
    public ResultMsg checkApproveById(SaleApplicationVO saleApplicationVO,String sessionId);
	
	public ResultMsg approve(SaleApplicationInsertDO insertDO,String sessionId);

	public ResultMsg reCollection(SaleApplicationInsertDO insertDO,String sessionId);
	
    public ResultMsg getDOByPrimaryKey(Long pk_sale_application,String sessionId);
    
    public ResultMsg update(SaleApplicationInsertDO insertDO,String sessionId);
    
    public ResultMsg submitModel(SaleApplicationDO saleApplicationDO,String sessionId);
    
    public ResultMsg approveLog(SaleApplicationVO saleApplicationVO,String sessionId);
    
    public ResultMsg getEnterpriseByName(String keyword,String sessionId);
    
    public ResultMsg selectEnterpriseByName(String keyword,String sessionId);
    
    public ResultMsg getEnterpriseByCode(String code,String sessionId);
}
