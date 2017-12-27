package com.suneee.smf.smf.dao;



import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
/**
 * @Description: ${收款确认}
 * @author: 超杰
 * @date: ${date} ${time}
 * ${tags}
 */
public interface CollectionConfirmDao {

    List<CollectionConfirmVO> selectByExample1(Map<String, Object> map);

	long countByExample(Map<String, Object> map);

	int add(CollectionConfirmDO confirmDO);

	CollectionConfirmVO getRestByPrimaryKey(CollectionConfirmVO vo);

	int modify(CollectionConfirmDO confirmDO);

	int delete(CollectionConfirmDO bean);

	int approveCollectionConfirmDO(CollectionConfirmDO bean);



	long countByCode(CollectionConfirmDO bean);
	//加时间戳保证线程安全
	long checkCollectionConfirmTimeStamp(CollectionConfirmDO confirmDO);

	int submitModel(CollectionConfirmDO confirmDO);

	List<CollectionConfirmVO> queryCollectionBySource(@Param("sourcebillid") Long sourcebillid,@Param("sourcebilltype") String sourcebilltype,@Param("enterpriseid") Long enterpriseid);
	
	//获取为结算的收款确认
	List<CollectionConfirmVO> queryUnsettleCollectionBySaleApplication(Map<String, Object> params);

	List<CollectionConfirmVO> selectForBalance(Map<String, Object> map);

	int updateForBalance(CollectionConfirmDO confirmDO);

	//更新状态及金额使用情况
	int updateCollectionUseFor(CollectionConfirmVO bean);

	long checkCode(Map<String, Object> map);

}
