package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.CapitalApplicationDO;
import com.suneee.smf.smf.model.CapitalSettlementDO;
import com.suneee.smf.smf.model.CapitalSettlementVO;

public interface CapitalSettlementDao {

	List<CapitalSettlementVO> selectByExample(Map<String, Object> map);

	long countByExample(Map<String, Object> map);

	long selectCountByName(CapitalSettlementDO capitalSettlementDO);

	void insert(CapitalSettlementDO capitalSettlementDO);

	CapitalSettlementVO selectOneByPrimaryKey(CapitalSettlementVO vo);

	int deleteCapitalSettlementDO(CapitalSettlementDO bean);

	int modifyCapitalSettlement(CapitalSettlementDO capitalSettlementDO);

	int submitModel(CapitalSettlementDO capitalSettlementDO);

	int approve(CapitalSettlementDO modelDO);

	void updateCapitalSettlementState(CapitalSettlementDO capitalSettlementDO);

	CapitalSettlementDO selectCapitalSettlementByStatus(
			CapitalSettlementDO capitalSettlementDO);


}
