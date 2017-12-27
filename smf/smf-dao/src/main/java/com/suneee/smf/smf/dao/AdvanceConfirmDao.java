package com.suneee.smf.smf.dao;

import com.suneee.smf.smf.model.AdvanceConfirmDO;
import com.suneee.smf.smf.model.AdvanceConfirmVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 
 * @Description: 放款确认dao
 * @author: 择善
 * @date: 2017年12月5日 上午8:48:28
 */
public interface AdvanceConfirmDao {

	List<AdvanceConfirmVO> selectByExample1(Map<String,Object> map);

	long countByExample(Map<String,Object> map);

	int insert(AdvanceConfirmDO advanceConfirmDO);

	AdvanceConfirmVO selectOneByPrimaryKey(AdvanceConfirmVO vo);

	int modifyAdvanceConfirm(AdvanceConfirmDO advanceConfirmDO);

	int deleteAdvanceConfirmDO(AdvanceConfirmDO bean);

	List<AdvanceConfirmVO> getAllList(AdvanceConfirmVO vo);

	long selectCountByCode(AdvanceConfirmDO advanceConfirmDO);

	int updateAdvanceConfirmState(AdvanceConfirmDO bean);

	AdvanceConfirmVO selectByCode(AdvanceConfirmVO vo);

	long checkAdvanceConfirmTimeStamp(AdvanceConfirmDO advanceConfirmDO);

	List<AdvanceConfirmVO> selectForBalance(Map<String, Object> map);

	int updateForBalance(AdvanceConfirmDO confirmDO);


	AdvanceConfirmVO queryAdvanceBySource(@Param("sourcebillid") Long sourcebillid, @Param("sourcebilltype") String sourcebilltype, @Param("enterpriseid") Long enterpriseid);
}
