package com.suneee.smf.smf.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.LoanContractDao;
import com.suneee.smf.smf.model.LoanContractDO;
import com.suneee.smf.smf.model.LoanContractVO;

/**
 * @Description 长期借款合同
 * @author WuXiaoYang
 * @Date 2017-12-8 15:58
 *
 */
@Service("loanContractService")
public class LoanContractService
{
	private static final Logger log = LoggerFactory.getLogger(LoanContractService.class);
	
	@Autowired
	private LoanContractDao loanContractDao;
	
	@Autowired
	private ActivitiUtilService activitiUtilService;
	
	
	/**
	 * 判断当前企业是否已经存在状态为新建或者审核中的记录,如果有则不能新建
	 * @param loanContractDO
	 * @return
	 */
	public boolean canInsert(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		loanContractVO.setEnterpriseid(new Long(userInfo.getEnterpriseid()));
		List<String> x = loanContractDao.canInsert(loanContractVO);
		if(x != null && x.size() > 0)
		{
			return false;
		}
		return true;
	}
	
	/**
	 *  校验合同号是否重复 
	 * @param code 贷款合同编码
	 * @param userInfo
	 * @return
	 */
	public boolean isCodeExists(String code, SystemUserInfoT userInfo)
	{
		if(code == null || "".equals(code))
		{
			return false;
		}
		
		List<String> x = loanContractDao.isCodeExists(code, userInfo.getEnterpriseid());
		if(x != null && x.size() > 0)
		{
			return true;
		}
		return false;
	}
	
	public int insert(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		loanContractVO.setEnterpriseid(new Long(userInfo.getEnterpriseid()));
		
		loanContractVO.setBookindate(new Date());
		loanContractVO.setInputmanid(new Long(userInfo.getUserId()));
		loanContractVO.setInputmanname(userInfo.getUserName());
		
		loanContractVO.setCanceldate(null);
		loanContractVO.setCancelid(null);
		loanContractVO.setCancelname(null);
		
		loanContractVO.setModefydate(null);
		loanContractVO.setModiferid(null);
		loanContractVO.setModifername(null);
		
		int line = loanContractDao.insert(loanContractVO);
		return line;
	}
	
	public int delete(List<LoanContractDO> deleteList, SystemUserInfoT userInfo)
	{
		for (LoanContractDO loanContractDO : deleteList)
		{
			loanContractDO.setState(Constant.LOAN_CONTRACT_STATE_DELETE);
			loanContractDO.setEnterpriseid(new Long(userInfo.getEnterpriseid()));
			loanContractDO.setCanceldate(new Date());
			loanContractDO.setCancelid(new Long(userInfo.getUserId()));
			loanContractDO.setCancelname(userInfo.getUserName());
		}
		int line = loanContractDao.delete(deleteList);
		return line;
	}
	
	public int modify(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		LoanContractDO loanContractDO = new LoanContractDO();
		BeanUtils.copyProperties(loanContractVO, loanContractDO);
		loanContractDO.setModefydate(new Date());
		loanContractDO.setModiferid(new Long(userInfo.getUserId()));
		loanContractDO.setModifername(userInfo.getUserName());
		int line = loanContractDao.modify(loanContractDO);
		return line;
	}
	
	public Page<LoanContractDO> selectLoanContract(Page<LoanContractDO> page, LoanContractVO bean)
	{
		Map<String,Object> map = new HashMap<>();
		
		int startNum = (page.getPageNo() - 1) * (page.getPageSize());
		map.put("startNum", startNum);
		map.put("pageSize", page.getPageSize());
		
		//高级查询条件
		map.put("code", bean.getCode());
		map.put("name", bean.getName());
		map.put("enterprise_name", bean.getEnterprise_name());
		map.put("state", bean.getState());
		map.put("starttime", bean.getStarttime());
		map.put("endtime", bean.getEndtime());
		
		//关键字查询条件
		map.put("searchValue",bean.getSearchValue());
		
		List<LoanContractDO> loanContractList = loanContractDao.selectLoanContract(map);
		int resultsNum = loanContractDao.countLoanContract(map);
		
		page.setResults(loanContractList);
		page.setTotalCount(resultsNum);
		page.setPageCount(resultsNum / page.getPageSize() + 1);
		return page;
	}
	
	/**
	 * 
	 * @param bean
	 * @param enterpriseSettlementCode 企业结算单据号
	 * @param userInfo
	 * @return
	 */
	public ResultMsg approve(LoanContractVO bean, SystemUserInfoT userInfo)
	{
		String pk_loan_contract = bean.getPk_loan_contract().toString();
		String workFlowCode = Constant.LOAN_CONTRACT_APPROVE_CODE;
		String lastNodeName = Constant.LOAN_CONTRACT_APPROVE_LASTTASK;
		String firstNodeName = Constant.LOAN_CONTRACT_APPROVE_FRISTTASK;
		int taskstatus = 0;
		if ("agree".equals(bean.getType())) //通过
		{ 
			taskstatus = 1;
			boolean isLastNode = activitiUtilService.isNode(pk_loan_contract, workFlowCode, lastNodeName);
			if (isLastNode)// 判断审核流程是否结束
			{
				bean.setState(Constant.LOAN_CONTRACT_STATE_APPROVED);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			} else 
			{
				bean.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
				bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
			}
			int m = loanContractDao.approve(bean);
			if (m != 1) 
			{
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("disagree".equals(bean.getType()))//退回上一层
		{ 
			taskstatus = 0;
			boolean isFristNode = activitiUtilService.isNode(pk_loan_contract, workFlowCode, firstNodeName);
			if (isFristNode) 
			{
				bean.setState(Constant.LOAN_CONTRACT_STATE_NEW);
			} else 
			{
				bean.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
			}
			int m = loanContractDao.approve(bean);
			if (m != 1) 
			{
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("end".equals(bean.getType()))//终止
		{
			taskstatus = -1;
			bean.setState(Constant.LOAN_CONTRACT_STATE_END);
			int m = loanContractDao.approve(bean);
			if (m != 1) 
			{
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else if ("reject".equals(bean.getType()))//驳回
		{
			taskstatus = 2;
			bean.setState(Constant.LOAN_CONTRACT_STATE_NEW);
			int m = loanContractDao.approve(bean);
			if (m != 1) 
			{
				return new ResultMsg("0", "该数据已失效，请刷新！");
			}
		} else 
		{
			return new ResultMsg("0", "审核参数不正确！");
		}
		String userID = String.valueOf(userInfo.getUserId());
		//推进审核流程
		activitiUtilService.approve(workFlowCode, pk_loan_contract, bean.getOption(), userID, userInfo, taskstatus);
		log.info("------------长期借款合同审核完成！-------------");
		return new ResultMsg("1", "审核成功！");
	}
	
	public int submitModel(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		int m = loanContractDao.approve(loanContractVO);
		return m;
	}
	
	public int cancelByEnterpriseID(LoanContractVO loanContractVO,SystemUserInfoT userInfo)
	{
		int line = loanContractDao.cancelByEnterpriseID(loanContractVO);
		return line;
	}
	
	public LoanContractDO selectLoanContractByID(LoanContractVO loanContractVO, SystemUserInfoT userInfo)
	{
		LoanContractDO loanContractDO = loanContractDao.selectLoanContractByID(loanContractVO);
		return loanContractDO;
	}

	
	public boolean checkLoanContractTimeStamp(LoanContractDO loanContractDO, SystemUserInfoT userInfo)
	{
		return loanContractDao.checkLoanContractTimeStamp(loanContractDO) < 1;
	}

}
