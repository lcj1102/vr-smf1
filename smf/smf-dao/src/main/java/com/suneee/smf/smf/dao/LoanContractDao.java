package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.suneee.smf.smf.model.LoanContractDO;
import com.suneee.smf.smf.model.LoanContractVO;

/**
 * @Description 长期借款合同
 * @author WuXiaoYang
 * @Date 2017-12-8 10:15
 *
 */
public interface LoanContractDao
{
	int insert(LoanContractVO loanContractVO);
	int delete(List<LoanContractDO> deleteList);
	int modify(LoanContractDO loanContractDO);
	List<LoanContractDO> selectLoanContract(Map<String,Object> map);
	Integer countLoanContract(Map<String,Object> map);
	int approve(LoanContractVO loanContractVO);
	int cancelByEnterpriseID(LoanContractVO loanContractVO);
	LoanContractDO selectLoanContractByID(LoanContractVO loanContractVO);
	int checkLoanContractTimeStamp(LoanContractDO loanContractDO);
	List<String> canInsert(LoanContractVO loanContractVO);
	List<String> isCodeExists(@Param("code")String code,@Param("enterpriseid") Integer enterpriseid);
}
