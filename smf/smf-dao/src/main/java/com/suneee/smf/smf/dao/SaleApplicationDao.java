package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.SaleApplicationDO;
import com.suneee.smf.smf.model.SaleApplicationVO;

/**
 * 
 * @Description: TODO 发货申请dao
 * @author: 崔亚强
 * @date: 2017年12月7日 下午3:55:10
 */
public interface SaleApplicationDao {

	/**
	 * 
	 * @Title: selectByPage 
	 * @Description: TODO 分页查询方法
	 * @param map
	 * @return
	 * @return: SaleApplicationVO
	 */
    List<SaleApplicationVO> selectByPage(Map<String, Object> map);

    /**
     * 
     * @Title: countByPage 
     * @Description: TODO 统计符合条件的总数
     * @param map
     * @return
     * @return: Long
     */
    Long countByPage(Map<String, Object> map);

    /**
     * 
     * @Title: insertDO 
     * @Description: TODO 新增
     * @param bean
     * @return
     * @return: int
     */
    int insertDO(SaleApplicationDO bean);
    
    /**
     * 
     * @Title: delete 
     * @Description: TODO 删除
     * @param bean
     * @return
     * @return: int
     */
    int delete(SaleApplicationDO bean);

    /**
     * 
     * @Title: approve 
     * @Description: TODO 审核
     * @param bean
     * @return
     * @return: int
     */
	int approve(SaleApplicationDO bean);
	
	/**
	 * 
	 * @Title: altCost 
	 * @Description: TODO 修改物流费和监管费
	 * @param map
	 * @return
	 * @return: int
	 */
	int altCost(Map<String,Object> map);
	
	/**
	 * 
	 * @Title: getDOByPrimaryKey 
	 * @Description: TODO 根据id查询
	 * @param bean
	 * @return
	 * @return: SaleApplicationVO
	 */
	SaleApplicationVO getDOByPrimaryKey(SaleApplicationDO bean);
	
	/**
	 * 
	 * @Title: update 
	 * @Description: TODO 编辑修改
	 * @param bean
	 * @return
	 * @return: int
	 */
	int update(SaleApplicationDO bean);

    /**
     * 
     * @Title: countByCode 
     * @Description: TODO 判断code是否已经存在
     * @param map
     * @return
     * @return: long
     */
    long countByCode(SaleApplicationDO bean);
    
    /**
     * 
     * @Title: checkDeliveryAdviceTimeStamp 
     * @Description: TODO 验证时间戳
     * @param deliveryAdviceDO
     * @return
     * @return: long
     */
    long checkSaleApplicationTimeStamp(SaleApplicationDO bean);
    
    /**
     * 
     * @Title: altState 
     * @Description: TODO 修改申请单状态为已发货
     * @param bean
     * @return
     * @return: int
     */
    int altState(SaleApplicationDO bean);
    
    /**
     * 
     * @Title: countSendNumber 
     * @Description: TODO 根据资金使用申请主键查询发货申请表中已发货状态的数量 
     * @param bean
     * @return
     * @return: double
     */
    double countSendNumber(SaleApplicationDO bean);

}