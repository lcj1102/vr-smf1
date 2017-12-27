package com.suneee.smf.smf.rest.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.scn.scf.model.EnterpriseVO;
import com.suneee.smf.smf.api.rest.LoanContractRestService;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.DateUnit;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.LoanContractConsumer;
import com.suneee.smf.smf.model.AttachmentsDO;
import com.suneee.smf.smf.model.AttachmentsInsertDO;
import com.suneee.smf.smf.model.LoanContractDO;
import com.suneee.smf.smf.model.LoanContractDOList;
import com.suneee.smf.smf.model.LoanContractVO;

/**
 * @Description 长期借款合同
 * @author WuXiaoYang
 * @Date 2017-12-8 16:57
 *
 */
@Service
@Path("/loanContract")
public class LoanContractRestServiceImpl implements LoanContractRestService
{
	@Autowired
	private LoanContractConsumer loanContractConsumer;
 
	/**
	 * 查询
	 */
	@GET
    @Path("/selectLoanContract")
    @Produces("application/json")
	@Override
	public ResultMsg selectLoanContract(@QueryParam("length")String length, 
										@QueryParam("pageNum")String pageNum, 
										@QueryParam("searchValue")String searchValue, 
										@QueryParam("state")String state, 
										@QueryParam("code")String code, 
										@QueryParam("name")String name, 
										@QueryParam("enterprise_name")String enterprise_name, 
										@QueryParam("starttime")String starttime, 
										@QueryParam("endtime")String endtime, 
										@HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        
        Page<LoanContractDO> page = new Page<LoanContractDO>();
        // 设置page属性（分页）
    	page.setPageSize(Integer.parseInt(length));// 每页条数
    	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        
    	LoanContractVO loanContractVO = new LoanContractVO();
        
        //关键字查询条件
        loanContractVO.setSearchValue(searchValue);
        
        //------------------高级搜索查询条件设置------------------------------
        loanContractVO.setCode(code);
        loanContractVO.setName(name);
        loanContractVO.setState(state);
        loanContractVO.setEnterprise_name(enterprise_name);
        if(starttime != null & !"".equals(starttime))
        {
        	loanContractVO.setStarttime(DateUnit.string2date(starttime, DateUnit.YYYY_MM_DD));
    	}
    	if(endtime != null & !"".equals(endtime))
    	{
    		Calendar calendar = Calendar.getInstance();
    		Date date = DateUnit.string2date(endtime, DateUnit.YYYY_MM_DD);
    		calendar.setTime(date);
    		calendar.add(Calendar.DAY_OF_MONTH, 1);
    		Date endDate = calendar.getTime();
    		loanContractVO.setEndtime(endDate);
    	}
    	//------------------高级搜索查询条件设置------------------------------
    	
    	resultMsg = loanContractConsumer.selectLoanContract(page, loanContractVO);
		
		return resultMsg;
	}

	/**
	 * 新增
	 */
	@POST
    @Path("/insert")
    @Produces("application/json")
	@Override
	public ResultMsg insert(LoanContractVO loanContractVO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        
        String saveType = loanContractVO.getSaveType();
        if("save".equals(saveType))
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_NEW);
        }else
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
        }
        
		resultMsg = loanContractConsumer.insert(loanContractVO, userInfo);
		return resultMsg;
	}
	
	/**
	 * 删除（作废）
	 */
	@POST
    @Path("/delete")
    @Produces("application/json")
	@Override
	public ResultMsg delete(LoanContractDOList deleteList, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
		resultMsg = loanContractConsumer.delete(deleteList, userInfo);
		return resultMsg;
	}

	/**
	 * 编辑（修改）
	 */
	@POST
    @Path("/modify")
    @Produces("application/json")
	@Override
	public ResultMsg modify(LoanContractVO loanContractVO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        
        String saveType = loanContractVO.getSaveType();
        if("save".equals(saveType))
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_NEW);
        }else
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
        }
        
		resultMsg = loanContractConsumer.modify(loanContractVO, userInfo);
		return resultMsg;
	}

	/**
	 * 审核
	 */
	@POST
    @Path("/approve")
    @Produces("application/json")
	@Override
	public ResultMsg approve(LoanContractVO loanContractVO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
		resultMsg = loanContractConsumer.approve(loanContractVO, userInfo);
		return resultMsg;
	}

	/**
	 * 提交（进入审核流程）
	 */
	@POST
    @Path("/submitModel")
    @Produces("application/json")
	@Override
	public ResultMsg submitModel(LoanContractDO loanContractDO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
		resultMsg = loanContractConsumer.submitModel(loanContractDO, userInfo);
		return resultMsg;
	}

	/**
	 * 查看流程进度
	 */
	@POST
    @Path("/viewProcess")
    @Produces("application/json")
	@Override
	public ResultMsg viewProcess(LoanContractDO loanContractDO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        resultMsg = loanContractConsumer.viewProcess(loanContractDO, userInfo);
        
		return resultMsg;
	}

	/**
	 * 变更
	 */
	@POST
    @Path("/change")
    @Produces("application/json")
	@Override
	public ResultMsg change(LoanContractVO loanContractVO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        
        String saveType = loanContractVO.getSaveType();
        if("save".equals(saveType))
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_NEW);
        }else
        {
        	loanContractVO.setState(Constant.LOAN_CONTRACT_STATE_APPROVING);
        }
        
        resultMsg = loanContractConsumer.insert(loanContractVO, userInfo);
        
		return resultMsg;
	}
	
	@GET
    @Path("/selectLoanContractByID")
    @Produces("application/json")
	@Override
	public ResultMsg selectLoanContractByID(@QueryParam("pk_loan_contract")Long pk_loan_contract, @HeaderParam("sessionId")String sessionId)
	{
		LoanContractVO loanContractVO = new LoanContractVO();
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
        loanContractVO.setPk_loan_contract(pk_loan_contract);
        loanContractVO.setEnterpriseid(new Long(userInfo.getEnterpriseid()));
		LoanContractDO loanContractDO = loanContractConsumer.selectLoanContractByID(loanContractVO, userInfo);
		List<Object> list = new ArrayList<Object>();
		if (loanContractDO != null)
		{
			resultMsg.setCode("1");
			resultMsg.setMsg("根据ID查询成功！");
			list.add(loanContractDO);
		} else
		{
			resultMsg.setCode("0");
			resultMsg.setMsg("根据ID查询结果为空！");
		}
		resultMsg.setData(list);
		return resultMsg;
	}

	
	
	/**
	 * 审核角色校验
	 */
	@POST
    @Path("/checkApproveById")
    @Produces("application/json")
	@Override
	public ResultMsg checkApproveById(LoanContractDO loanContractDO, @HeaderParam("sessionId")String sessionId)
	{
		ResultMsg resultMsg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo == null || userInfo.getEnterpriseid() == null)
		{
        	resultMsg.setCode("0");
			resultMsg.setMsg("用户登陆过期，请重新登陆！");
		}
		resultMsg = loanContractConsumer.checkApproveById(loanContractDO, userInfo);
		return resultMsg;
	}
	
	/**
	 * 根据主键查询附件
	 */
	@GET
	@Path("/fileListById")
	@Produces("application/json")
	@Override
	public ResultMsg fileListById(@QueryParam("pk_loan_contract") Long pk_loan_contract, @HeaderParam("sessionId") String sessionId)
	{
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null)
		{
			msg = loanContractConsumer.getFileListById(pk_loan_contract, userInfo);
		} else
		{
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	
	/**
	 * 新增附件
	 */
	@POST
    @Path("/insertFile")
    @Produces("application/json")
	@Override
	public ResultMsg insertFile(AttachmentsInsertDO file, @HeaderParam("sessionId") String sessionId)
	{
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null)
		{
			try
			{
				msg = loanContractConsumer.addFile(file.getFile().get(0), userInfo);
				msg.setMsg("新增成功！！");
			} catch (MsgException e)
			{
				msg.setMsg(e.getMessage());
				msg.setCode(e.getCode());
			}
		} else
		{
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	
	/**
	 * 删除附件
	 */
	@POST
	@Path("/deleteFile")
	@Produces("application/json")
	@Override
	public ResultMsg deleteFile(AttachmentsInsertDO file, @HeaderParam("sessionId") String sessionId)
	{
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null)
		{
			int count = 0;
			if (file == null || file.getFile() == null)
			{
				msg.setMsg("删除失败！！");
			} else
			{
				try
				{
					for (AttachmentsDO bean : file.getFile())
					{
						count += loanContractConsumer.deleteFile(bean, userInfo);
					}
				} catch (MsgException e)
				{
					msg.setMsg(e.getMessage());
					msg.setCode(e.getCode());
					return msg;
				}
				if (count > 0)
				{
					msg.setMsg("删除成功！！");
				} else
				{
					msg.setMsg("删除失败！！");
				}
			}
		} else
		{
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}
	
	/**
	 * 根据企业名称模糊查询
	 */
	@GET
    @Path("/getEnterpriseByName")
    @Produces("application/json")
    @Override
    public ResultMsg getEnterpriseByName(@QueryParam("keyword") String keyword,@HeaderParam("sessionId") String sessionId) {
    	ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	try {
				keyword = URLDecoder.decode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        	List<Object> list = new ArrayList<Object>();
            List<EnterpriseVO> relist = loanContractConsumer.getEnterpriseByName(keyword, userInfo.getEnterpriseid().longValue());
            list.addAll(relist);
            msg.setData(list);
            msg.setCode("1");
            msg.setMsg("查询成功");
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
    }
	
	/**
	 *  根据企业编码查询企业信息
	 */
    @GET
    @Path("/getEnterpriseByCode")
    @Produces("application/json")
    @Override
	public ResultMsg getEnterpriseByCode(@QueryParam("code")String code,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null && userInfo.getEnterpriseid() != null) {
        	List<Object> list = new ArrayList<Object>();
            EnterpriseVO enterpriseVO = loanContractConsumer.getEnterpriseByCode(code, userInfo.getEnterpriseid().longValue());
            list.add(enterpriseVO);
            msg.setData(list);
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}
	    
}
