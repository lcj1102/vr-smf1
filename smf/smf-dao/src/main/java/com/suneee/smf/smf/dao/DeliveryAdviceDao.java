package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.DeliveryAdviceDO;
import com.suneee.smf.smf.model.DeliveryAdviceVO;

public interface DeliveryAdviceDao {
	 //分页查询数据
    List<DeliveryAdviceVO> queryByPage(Map<String,Object> map);
	 //计算总数据数
    long countByExample(Map<String,Object> map);
    //插入数据
    int insert(DeliveryAdviceVO deliveryAdviceVO);
    //根据主键查询数据
    DeliveryAdviceVO getRestByPrimaryKey(DeliveryAdviceDO deliveryAdviceDO);
    //更新数据
    int update(DeliveryAdviceVO deliveryAdviceVO);
    //删除
    int delete(DeliveryAdviceVO bean);
    //审核
    int approve(DeliveryAdviceVO bean);
    //时间戳验证
    long checkDeliveryAdviceTimeStamp(DeliveryAdviceDO deliveryAdviceDO);
    //验证单号是否重复
    long countByCode(Map<String, Object> map);
}
