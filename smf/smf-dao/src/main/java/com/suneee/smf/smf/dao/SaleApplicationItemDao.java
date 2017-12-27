package com.suneee.smf.smf.dao;

import java.util.List;

import com.suneee.smf.smf.model.SaleApplicationItemDO;
import com.suneee.smf.smf.model.SaleApplicationItemVO;


public interface SaleApplicationItemDao {

    /**
     * 
     * @Title: insertItemDO 
     * @Description: TODO 新增
     * @param bean
     * @return
     * @return: int
     */
    int insertItemDO(SaleApplicationItemDO bean);


    /**
     * 
     * @Title: queryListByDeliveryAdviceId 
     * @Description: TODO 根据主单id查询子单
     * @param bean
     * @return
     * @return: List<SaleApplicationItemVO>
     */
    List<SaleApplicationItemVO> queryListBySaleApplicationId(SaleApplicationItemDO bean);

    /**
     * 
     * @Title: deleteItemByAdviceId 
     * @Description: TODO 根据主单id删除子单
     * @param bean
     * @return
     * @return: int
     */
    int deleteItemBySaleApplicationId(SaleApplicationItemDO bean);
    
}