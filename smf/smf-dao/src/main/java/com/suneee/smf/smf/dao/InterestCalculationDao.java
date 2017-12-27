package com.suneee.smf.smf.dao;

import com.suneee.smf.smf.model.InterestCalculationVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @Description: 企业计息dao
 * @author: 张礼佳
 * @date: 2017年12月8日 上午11:25:06
 */
public interface InterestCalculationDao {

	/** 分页查企业计息 */
	List<InterestCalculationVO> selectByPage(Map<String, Object> param);

	/** 查询企业计息数据条数 */
	long queryCount(Map<String, Object> param);
	
	//查询所有未结息的利息记录
	List<InterestCalculationVO> selectNotSettlement(InterestCalculationVO interestCalculationVO);
	

	int add(Date date);

	//创建临时表
	void createTempTable();

	//临时表插入数据
	int insertTempTable(List<InterestCalculationVO> notSettlementList);

	//通过临时表更新利息状态
	int updateStateWithTempTable();

	//更新已结息的记录的状态
	int updateState(Map<String, Object> map);
}
