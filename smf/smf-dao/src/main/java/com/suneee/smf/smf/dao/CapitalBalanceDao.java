package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import com.suneee.smf.smf.model.CapitalBalanceDO;
import com.suneee.smf.smf.model.CapitalBalanceVO;

public interface CapitalBalanceDao {

	int insertCapitalBalance(CapitalBalanceDO balanceDO);

    List<CapitalBalanceVO> selectByPage(Map<String, Object> map);
    
    List<CapitalBalanceVO> balanceList(Map<String, Object> map);

    Long countByPage(Map<String, Object> map);
    
}
