/**
 * 
 */
package com.suneee.smf.smf.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.suneee.smf.smf.model.EnterpriseSettlementDO;
import com.suneee.smf.smf.model.EnterpriseSettlementVO;

/**
 * 企业分账结算
 * 
 * @author suneee
 *
 */
public interface EnterpriseSettlementDao {
	public int insert(EnterpriseSettlementDO enterpriseSettlementDO);

	// 更新三大费用
	public int updateMoney(EnterpriseSettlementVO enterpriseSettlementVO);

	public List<String> isExistsGivenEnterprise(
			@Param("pk_enterprise") Long pk_enterprise,
			@Param("enterpriseid") Long enterpriseid);

	List<String> isCodeExists(@Param("code") String code,
			@Param("enterpriseid") Integer enterpriseid);

	// 根据企业查询结算记录
	public EnterpriseSettlementVO getByEnterprise(
			EnterpriseSettlementDO enterpriseSettlementDO);

	// 更新借贷金额
	int updateAmount(EnterpriseSettlementVO enterpriseSettlementVO);

	/** 查询指定条件数据 */
	List<EnterpriseSettlementVO> selectByPage(Map<String, Object> param);

	/** 查数据条件下数据总条数 */
	long queryCount(Map<String, Object> param);
	
	/** 根据ID查询企业结算数据*/
	EnterpriseSettlementVO getById(@Param("id") Long id, @Param("enterpriseid") Long enterpriseid);

	public List<EnterpriseSettlementVO> selectByName(Map<String, Object> param);

	public List<EnterpriseSettlementVO> selectByCode(Map<String, Object> param);

}
