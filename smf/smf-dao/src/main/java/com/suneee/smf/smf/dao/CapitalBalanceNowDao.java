package com.suneee.smf.smf.dao;

import java.util.List;

import com.suneee.smf.smf.model.CapitalBalanceNowDO;

public interface CapitalBalanceNowDao {

	List<CapitalBalanceNowDO> selectNow();

	int updateBalanceNow(CapitalBalanceNowDO balanceNowDO);

}
