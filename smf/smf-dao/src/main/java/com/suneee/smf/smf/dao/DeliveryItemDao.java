package com.suneee.smf.smf.dao;

import java.util.List;

import com.suneee.smf.smf.model.DeliveryItemDO;
import com.suneee.smf.smf.model.DeliveryItemVO;

public interface DeliveryItemDao {
	//根据主表id查询所有的子表
	List<DeliveryItemVO> queryListByDeliveryAdviceId(DeliveryItemDO deliveryItemDO);
	//插入数据
	int insert(DeliveryItemVO item);
	//根据主键id查询数据
	DeliveryItemVO getRestByPrimaryKey(DeliveryItemVO deliveryItemVO);
	//更新数据
    int updateById(DeliveryItemVO deliveryItemVO);
    //删除数据
    int deleteById(DeliveryItemVO deliveryItemVO);
}
