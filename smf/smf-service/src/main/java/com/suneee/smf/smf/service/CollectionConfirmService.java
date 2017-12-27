package com.suneee.smf.smf.service;


import com.alibaba.dubbo.config.annotation.Reference;
import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.smf.smf.common.*;
import com.suneee.smf.smf.dao.CollectionConfirmDao;
import com.suneee.smf.smf.model.CollectionConfirmDO;
import com.suneee.smf.smf.model.CollectionConfirmVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: ${收款确认}
 * @author: 超杰
 * @date: ${date} ${time}
 * ${tags}
 */

@Service("collectionConfirmService")
public class CollectionConfirmService {

    @Autowired
    private CollectionConfirmDao collectionConfirmDao;

    @Autowired
    private ActivitiUtilService activitiUtilService;

    @Autowired
    private EnterpriseSettlementService enterpriseSettlementService;
    @Autowired
    private  CollConfirmErrService collConfirmErrService;
    
    //@Autowired
	//private CodeRuleUtilService codeRuleUtilService;

    @Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
    private CodeRuleRestProvider codeRuleRestProvider;

    //分页列表查询
    public Page<CollectionConfirmVO> queryByPage(Page<CollectionConfirmVO> page, CollectionConfirmVO bean) {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("code",bean.getCode());
        map.put("enterprise_payment_name",bean.getEnterprise_payment_name());

        map.put("beginAmount",bean.getBeginAmount());
        map.put("endAmount",bean.getEndAmount());
        map.put("beginDate",bean.getBeginDate());
        if (bean.getEndDate() != null && !"".equals(bean.getEndDate())) {
            map.put("endDate", DateUnit.icDateByDay(bean.getEndDate(),1));
        }
        map.put("searchValue", bean.getSearchValue());
        map.put("enterpriseid",bean.getEnterpriseid());
        map.put("state",bean.getState());
        int startNum = (page.getPageNo()-1)*(page.getPageSize());
        map.put("startNum",startNum);
        map.put("pageSize",page.getPageSize());
        List<CollectionConfirmVO> list = collectionConfirmDao.selectByExample1(map);
        page.setResults(list);    //待分页的数据
        int totalCount = (int) collectionConfirmDao.countByExample(map);
        page.setTotalCount(totalCount); // 总条数
        page.setPageCount(totalCount / page.getPageSize() + 1); //总页数
        return page;
    }

    /**
     * @Description: ${插入收款确认及收款异常数据}
     * @author: 超杰
     * @date: ${date} ${time}
     * ${tags}
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public ResultMsg add(CollectionConfirmDO confirmDO,Long enterprised) throws MsgException  {
        if(confirmDO.getBooked_flag()==null){
            confirmDO.setBooked_flag(Constant.COLLECTION_CONFIRM_BOOK_FLAG_NO);
        }
        confirmDO.setBookindate(new Date());
        confirmDO.setState(Constant.COLLECTION_STATE_APPROVED);
     /*   Map<String,Object> map = codeRuleRestProvider.submitSingleton(enterprised, Constant.COLLECTION_CODERULE_CODE);
        if ("1".equals((String)map.get("code"))) {
            if ("".equals((String) map.get("msg"))) {
                throw new MsgException("0", "生成收款确认单号失败！！！");
            }
            confirmDO.setCode((String) map.get("msg"));
        }else{
            throw new MsgException("0", "生成收款确认单号失败！！！");
        }*/
        confirmDO.setEnterpriseid(enterprised);
        int m=collectionConfirmDao.add(confirmDO);
        if (m != 1) {
            throw new MsgException("0", "插入收款确认单失败");
        }
        //插入收款异常表
        collConfirmErrService.insertCollectionconfirmError(confirmDO);

        return new ResultMsg("1", "保存成功！");
    }

    //根据主键查询
    public CollectionConfirmVO getRestByPrimaryKey(CollectionConfirmVO vo,SystemUserInfoT userInfoT) {
        vo.setEnterpriseid(userInfoT.getEnterpriseid().longValue());
        CollectionConfirmVO restByPrimaryKey = collectionConfirmDao.getRestByPrimaryKey(vo);
        return restByPrimaryKey;
    }
    
    //根据主键查询
    public CollectionConfirmVO getCollectionConfirmVOByPrimaryKey(CollectionConfirmVO vo) {
        CollectionConfirmVO restByPrimaryKey = collectionConfirmDao.getRestByPrimaryKey(vo);
        return restByPrimaryKey;
    }

  /*  public ResultMsg startTaskForCollection(CollectionConfirmDO confirmDO, SystemUserInfoT userInfo) {
        String confirmId = String.valueOf(confirmDO.getPk_collection_confirm());
        ResultMsg msg = activitiUtilService.startApprove(Constant.COLLECTION_APPROVE_NAME,confirmId, Constant.COLLECTION_APPROVE_CODE, userInfo);
        if ("1".equals(msg.getCode())) {
            // 设置提交时，补充协议的状态:2-待审核
            confirmDO.setState(Constant.COLLECTION_STATE_APPROVING);
            collectionConfirmDao.approveCollectionConfirmDO(confirmDO);
        } else {
            return msg;
        }
        return new ResultMsg("1", "提交成功");
    }*/

    //编辑
   /* public ResultMsg modify(CollectionConfirmDO confirmDO, SystemUserInfoT userInfo) {
    	//对执行单号唯一校验
        confirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        if(collectionConfirmDao.countByCode(confirmDO) > 0){
            return new ResultMsg("2","单号重复");
        }
        confirmDO.setModefydate(new Date());
        confirmDO.setModifername(userInfo.getName());
        confirmDO.setModiferid(userInfo.getUserId().longValue());
        confirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        int m = collectionConfirmDao.modify(confirmDO);
        if (m != 1) {
            return new ResultMsg("0", "该数据已失效，请刷新！");
        }
        if (Constant.COLLECTION_STATE_APPROVING.equals(confirmDO.getState())) {
            return startTaskForCollection(confirmDO,userInfo);
        }
        return new ResultMsg("1","保存成功");
    }*/

 /*   public int delete(CollectionConfirmDO bean, SystemUserInfoT userInfo) {
        bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        bean.setCanceldate(new Date());
        bean.setCancelname(userInfo.getName());
        bean.setCancelid(userInfo.getUserId().longValue());
        bean.setState(Constant.COLLECTION_STATE_DELETE);
       
        return collectionConfirmDao.delete(bean);
    }*/

    /**
     * 审批
     * @param confirmVO
     * @param userInfo
     * @return
     */
  /*  public ResultMsg approveCollectionConfirmDO(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) throws MsgException {

        CollectionConfirmDO collectionConfirmDO=new CollectionConfirmDO();
        collectionConfirmDO.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        collectionConfirmDO.setPk_collection_confirm(confirmVO.getPk_collection_confirm());
        collectionConfirmDO.setTime_stamp(confirmVO.getTime_stamp());
        ResultMsg msg = new ResultMsg();
        msg.setCode("1");

        int taskstatus = 0;
        //获取任务
        if ("agree".equals(confirmVO.getType())) { //通过
            taskstatus = 1;
            boolean isLastNode = activitiUtilService.isNode(confirmVO.getPk_collection_confirm(),Constant.COLLECTION_APPROVE_CODE,Constant.COLLECTION_APPROVE_LASTTASK);
            if (isLastNode) {// 判断审核流程是否结束
                collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVED);
            } else {
                collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVING);
            }
        } else if ("disagree".equals(confirmVO.getType())){ //退回上一层
            taskstatus = 0;
            boolean isFristNode = activitiUtilService.isNode(confirmVO.getPk_collection_confirm(),Constant.COLLECTION_APPROVE_CODE,Constant.COLLECTION_APPROVE_FRISTTASK);
            if (isFristNode) {
                collectionConfirmDO.setState(Constant.COLLECTION_STATE_NEW);
            } else {
                collectionConfirmDO.setState(Constant.COLLECTION_STATE_APPROVING);
            }
        } else if ("end".equals(confirmVO.getType())){//终止
            taskstatus = -1;
            collectionConfirmDO.setState(Constant.COLLECTION_STATE_END);
        } else if ("reject".equals(confirmVO.getType())){//驳回
            taskstatus = 2;
            collectionConfirmDO.setState(Constant.COLLECTION_STATE_NEW);
           
        } else {
            throw new MsgException("0", "审核参数不正确！");
        }
        int m = collectionConfirmDao.approveCollectionConfirmDO(collectionConfirmDO);
        if (m != 1) {
            throw new MsgException("0", "该数据已失效，请刷新");
        }
        activitiUtilService.approve(Constant.COLLECTION_APPROVE_CODE, String.valueOf(confirmVO.getPk_collection_confirm()), confirmVO.getOption(), String.valueOf(userInfo.getUserId()), userInfo, taskstatus);
        return msg;
    }*/


    /*public String getApproveRoleCodes(Long id, SystemUserInfoT userInfo) {
        return activitiUtilService.getApproveRoleCodes(id, Constant.COLLECTION_APPROVE_CODE, userInfo);
    }*/

  /*  public ResultMsg approveLog(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) {
        return activitiUtilService.getFlowPlan(Constant.COLLECTION_APPROVE_CODE,String.valueOf(confirmVO.getPk_collection_confirm()));
    }*/

    //线程安全的时间戳校验
    public boolean checkCreditQuotaTimeStamp(CollectionConfirmDO confirmDO){
		return collectionConfirmDao.checkCollectionConfirmTimeStamp(confirmDO)<1;
	}

/*    public ResultMsg submitModel(CollectionConfirmVO confirmVO, SystemUserInfoT userInfo) {

        CollectionConfirmDO bean=new CollectionConfirmDO();
        bean.setEnterpriseid(userInfo.getEnterpriseid().longValue());
        bean.setState(Constant.COLLECTION_STATE_APPROVING);
        bean.setPk_collection_confirm(confirmVO.getPk_collection_confirm());
        bean.setTime_stamp(confirmVO.getTime_stamp());
        int m = collectionConfirmDao.submitModel(bean);
        if (m != 1) {
            return new ResultMsg("0","该数据已失效，请刷新");
        }
        return startTaskForCollection(bean,userInfo);
    }*/
    //接口 通过来源id获取数据
    public List<CollectionConfirmVO> queryCollectionBySource(Long sourcebillid,String sourcebilltype,Long enterpriseid  ){
        return collectionConfirmDao.queryCollectionBySource(sourcebillid,sourcebilltype,enterpriseid);
    }

}