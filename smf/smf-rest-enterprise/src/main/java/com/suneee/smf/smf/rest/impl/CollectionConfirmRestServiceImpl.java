package com.suneee.smf.smf.rest.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.api.rest.CollectionConfirmRestService;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.consumer.impl.CollectionConfirmConsumer;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: ${收款确认}
 * @author: 超杰
 * @date: ${date} ${time}
 * ${tags}
 */

@Service//指明dubbo服务
@Path("/collection")//指定访问TestService的URL相对路径是/test，即http://localhost:8080/test
public class CollectionConfirmRestServiceImpl implements CollectionConfirmRestService {
	
	@Autowired
	private CollectionConfirmConsumer collectionConfirmConsumer;
	
	@GET
    @Path("/list")
    @Produces("application/json")
	@Override
	public ResultMsg list(
						  @QueryParam("code") String code,
						  @QueryParam("length") String length,
						  @QueryParam("state") String state,
						  @QueryParam("pageNum") String pageNum,
						  @QueryParam("searchValue") String searchValue,
						  @HeaderParam("sessionId") String sessionId,
						  @QueryParam("enterprise_payment_name") String enterprise_payment_name,
						  @QueryParam("beginDate") String beginDate1,
						  @QueryParam("endDate") String endDate1,
						  @QueryParam("beginAmount") String beginAmount1,
						  @QueryParam("endAmount") String endAmount1

	                       ) {
		
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        ResultMsg result = new ResultMsg();
        if (userInfo != null) {
        	// 初始化
        	List<Object> list = new ArrayList<Object>();
        	Page<CollectionConfirmVO> page = new Page<CollectionConfirmVO>();
			CollectionConfirmVO bean = new CollectionConfirmVO();
        	// 设置page属性
        	page.setPageSize(Integer.parseInt(length));// 每页条数
        	page.setPageNo(Integer.parseInt(pageNum));// 当前页
        	// 设置bean属性（查询条件）

			bean.setCode(code);
			bean.setSearchValue(searchValue);
			bean.setState(state);
			bean.setEnterprise_payment_name(enterprise_payment_name);
			if(!"".equals(beginAmount1) && beginAmount1!=null){
				Double beginAmount = Double.valueOf(beginAmount1.toString());
				bean.setBeginAmount(beginAmount);
			}
			if(!"".equals(endAmount1) && endAmount1!=null){
				Double endAmount = Double.valueOf(endAmount1.toString());
				bean.setEndAmount(endAmount);
			}
			if(!"".equals(beginDate1) && beginDate1!=null){
				try {
					DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
					Date beginDate = fmt.parse(beginDate1);
					bean.setBeginDate(beginDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(!"".equals(endDate1) && endDate1!=null){
				try {
					DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
					Date endDate = fmt.parse(endDate1);
					bean.setEndDate(endDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        	list.add(collectionConfirmConsumer.queryByPage(page, bean));
        	result.setData(list);
        } else {
        	result.setMsg("用户登陆过期，请重新登陆！");
        }
        return result;
	}

/*	@POST
    @Path("/insert")
    @Produces("application/json")
	@Override
	public ResultMsg insert(CollectionConfirmInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null) {
	        try {
				CollectionConfirmDO collectionConfirmDO = insertDO.getInsertDO().get(0);
				if ("submit".equals(insertDO.getSaveType())){
					collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVING);
				}else {
					collectionConfirmDO.setState(Constant.COLLECTION_STATE_NEW);
				}
	        	msg = collectionConfirmConsumer.add(collectionConfirmDO,userInfo);
	        } catch (Exception e) {
	            msg.setMsg("新增失败！！");
	            e.printStackTrace();
	        }
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        } 
        return msg;
	}*/
	
	@GET
    @Path("/byid")//为空则其完整路径就是类路径
    @Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg selectByPrimaryKey(@QueryParam("id") Long id,@HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class,sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			List<Object> pb = new ArrayList<Object>();// 返回集合
			if (id != null && !"".equals(id)) {
				CollectionConfirmVO vo = new CollectionConfirmVO();
				vo.setEnterpriseid(userInfo.getEnterpriseid().longValue());
				vo.setPk_collection_confirm(id);
				pb.add(collectionConfirmConsumer.getRestByPrimaryKey(vo,userInfo));
				msg.setData(pb);
			} else {
				msg.setData(pb);
				msg.setMsg("查询失败");
			}
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}

/*	@POST
    @Path("/update")
    @Produces("application/json")
    @Consumes("application/json")
	@Override
	public ResultMsg update(CollectionConfirmInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null) {
        	 try {
				 CollectionConfirmDO collectionConfirmDO = insertDO.getInsertDO().get(0);
				 if ("submit".equals(insertDO.getSaveType())){
					 collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVING);
				 }else {
					 collectionConfirmDO.setState(Constant.COLLECTION_STATE_NEW);
				 }
				 msg = collectionConfirmConsumer.modify(collectionConfirmDO,userInfo);
 	        } catch (Exception e) {
 	            msg.setMsg("保存失败！！");
 	            e.printStackTrace();
 	        }
        	
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}*/

/*	@POST
    @Path("/deleteSelect")
    @Produces("application/json")
	@Override
	public ResultMsg deleteSelect(CollectionConfirmInsertDO insertDO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
        SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
        if (userInfo != null) {
        	
        	if (insertDO == null || insertDO.getInsertDO() == null) {
        		msg.setMsg("删除失败！！");
        	} else {
        		msg = collectionConfirmConsumer.delete(insertDO,userInfo);
        		
        	}
        } else {
        	msg.setMsg("用户登陆过期，请重新登陆！");
        }
        return msg;
	}*/

	/**
	 * 查看当前用户是否有对应数据的审批权限
	 */
/*	@POST
	@Path("/checkApproveById")//为空则其完整路径就是类路径
	@Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg checkApproveById(CollectionConfirmVO collectionConfirmVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg = collectionConfirmConsumer.checkApproveById(collectionConfirmVO,userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}*/

/*	@POST
	@Path("/approveLog")//流程进度
	@Produces("application/json")//编辑初始显示
	@Override
	public ResultMsg approveLog(CollectionConfirmVO collectionConfirmVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg = collectionConfirmConsumer.approveLog(collectionConfirmVO,userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}*/
	/**
	 * 审核数据
	 */
/*	@POST
	@Path("/approveSelect")
	@Produces("application/json")
	@Override
	public ResultMsg approveSelect(CollectionConfirmVO collectionConfirmVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null) {
			try {
				msg = collectionConfirmConsumer.approveCollectionConfirmDO(collectionConfirmVO,userInfo);
			} catch (MsgException e) {
				msg.setCode(e.getCode());
				msg.setMsg(e.getMessage());
			}

		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}*/


	/**
	 * 提交数据
	 */
/*	@POST
	@Path("/submitModel")
	@Produces("application/json")
	@Override
	public ResultMsg submitModel(CollectionConfirmVO collectionConfirmVO, @HeaderParam("sessionId") String sessionId) {
		ResultMsg msg = new ResultMsg();
		SystemUserInfoT userInfo = CacheUtils.get(SystemUserInfoT.class, sessionId);
		if (userInfo != null && userInfo.getEnterpriseid() != null) {
			msg = collectionConfirmConsumer.submitModel(collectionConfirmVO,userInfo);
		} else {
			msg.setMsg("用户登陆过期，请重新登陆！");
		}
		return msg;
	}*/
}
