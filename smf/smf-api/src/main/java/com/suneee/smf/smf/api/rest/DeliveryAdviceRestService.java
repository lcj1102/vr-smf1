package com.suneee.smf.smf.api.rest;

import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.DeliveryAdviceList;
import com.suneee.smf.smf.model.DeliveryAdviceVO;

public interface DeliveryAdviceRestService {
	ResultMsg deliveryAdviceList(String length,String pageNum,String searchValue,String state,String code,
			String name,String enterprise_name,String starttime,String endtime,String sessionId);
	ResultMsg insert(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg insertAndSubmit(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg selectByPrimaryKey(Long id,String sessionId);
	ResultMsg saveAndSubmit(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg save(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg deleteSelect(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg approveSelect(DeliveryAdviceList deliveryAdviceList,String sessionId);
	ResultMsg checkApproveById(DeliveryAdviceVO deliveryAdviceVO, String sessionId);
	ResultMsg submitModel(DeliveryAdviceVO deliveryAdviceVO,String sessionId);
	ResultMsg approveLog(DeliveryAdviceVO deliveryAdviceVO,String sessionId);
}
