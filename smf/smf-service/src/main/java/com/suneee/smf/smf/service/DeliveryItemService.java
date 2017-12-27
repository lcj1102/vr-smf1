package com.suneee.smf.smf.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.smf.smf.dao.DeliveryItemDao;
import com.suneee.smf.smf.model.DeliveryItemDO;
import com.suneee.smf.smf.model.DeliveryItemVO;

@Service("deliveryItemService")
public class DeliveryItemService {
	@Autowired
	private DeliveryItemDao deliveryItemDao;
	
	public List<DeliveryItemVO> queryListByDeliveryAdviceId(DeliveryItemDO deliveryItemDO) {
		return deliveryItemDao.queryListByDeliveryAdviceId(deliveryItemDO);
	}
}
