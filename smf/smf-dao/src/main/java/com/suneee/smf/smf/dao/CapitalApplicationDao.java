package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.suneee.smf.smf.model.CapitalApplicationDO;
import com.suneee.smf.smf.model.CapitalApplicationItemDO;
import com.suneee.smf.smf.model.CapitalApplicationItemVO;
import com.suneee.smf.smf.model.CapitalApplicationVO;

/**
 * @Description: 资金使用申请
 * @author: 致远
 * @date: 2017年12月1日 下午4:05:14
 */
public interface CapitalApplicationDao {

	List<CapitalApplicationVO> selectByExample1(Map<String, Object> map);

	long countByExample(Map<String, Object> map);

	int addCapitalApplication(CapitalApplicationDO laDO);

	int addCapitalApplicationItem(CapitalApplicationItemDO itemDO);

	CapitalApplicationVO getRestByPrimaryKey(CapitalApplicationVO vo);

	List<CapitalApplicationItemVO> selectItemListById(CapitalApplicationVO vo);
	
	//获取细单中的数量为可发货最大数量
	List<CapitalApplicationItemVO> getItemListById(CapitalApplicationVO vo);
	
	List<CapitalApplicationItemVO> getItemMaxById(Map<String,Object> map);

	int updateCapitalApplication(CapitalApplicationDO laDO);

	int updateCapitalApplicationItem(CapitalApplicationItemDO itemDO);

	int deleteCapitalApplicationItem(CapitalApplicationItemDO itemDO);

	int deleteCapitalApplication(CapitalApplicationDO bean);

	int deleteCapitalApplicationItemById(CapitalApplicationDO bean);

	int approve(CapitalApplicationDO modelDO);

	long checkCode(Map<String, Object> map);

	int submitModel(CapitalApplicationDO bean);

	List<CapitalApplicationVO> getAllList(CapitalApplicationDO vo);

	CapitalApplicationVO getCapitalApplicationByCode(CapitalApplicationDO vo);

	double checkCapital(Map<String, Object> map);
	
	double sumApplicationItemsNumber(@Param("pk_capital_application") Long pk_capital_application,@Param("enterpriseid") Long enterpriseid);

	int updateCapitalApplicationSaleFlag(CapitalApplicationDO laDO);
}
