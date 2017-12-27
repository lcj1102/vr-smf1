package com.suneee.smf.smf.service;

import java.math.BigDecimal;
import java.util.*;

import com.suneee.smf.smf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.suneee.scn.authentication.model.dbo.SystemUserInfoT;
import com.suneee.scn.basic.component.cache.CacheUtils;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.Page;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.dao.CollectionConfirmDao;
import com.suneee.smf.smf.dao.EnterpriseSettlementDao;
import com.suneee.smf.smf.dao.InterestCalculationDao;

/**
 * 
 * @Description: 分账结算service
 * @author: 择善
 * @date: 2017年12月8日 上午11:28:19
 */
@Service("enterpriseSettlementService")
public class EnterpriseSettlementService {

	private static final Logger log= LoggerFactory.getLogger(EnterpriseSettlementService.class);
	
	@Autowired
	private SaleApplicationService saleApplicationService;
	
	@Autowired
	private CollectionConfirmService collectionConfirmService;

	@Autowired
	private CollectionConfirmDao collectionConfirmDao;
	
	@Autowired
	private DeliveryAdviceService deliveryAdviceService;
	
	@Autowired
	private CapitalApplicationService capitalApplicationService;

	@Autowired
	private AdvanceConfirmService advanceConfirmService;

	@Autowired
	private InterestCalculationDao interestCalculationDao;
	
	@Autowired
	private EnterpriseSettlementDao enterpriseSettlementDao;
	
	

	/**
	 * 根据企业ID判断是否已经存在某一企业分账结算记录
	 * @param pk_enterprise 企业ID
	 * @param enterpriseid
	 * @return
	 */
	public boolean isExistsGivenEnterprise(Long pk_enterprise, Long enterpriseid)
	{
		List<String> xList = enterpriseSettlementDao.isExistsGivenEnterprise(pk_enterprise, enterpriseid);
		if(xList != null && xList.size() > 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 *  校验企业结算单号是否重复 
	 * @param code 企业结算单号
	 * @param userInfo
	 * @return
	 */
	public boolean isCodeExists(String code, SystemUserInfoT userInfo)
	{
		if(code == null || "".equals(code))
		{
			return false;
		}
		
		List<String> x = enterpriseSettlementDao.isCodeExists(code, userInfo.getEnterpriseid());
		if(x != null && x.size() > 0)
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * 新增一条企业的分账结算空记录
	 * @param enterpriseSettlementDO
	 * @param userInfo
	 * @return
	 */
	public int insert(EnterpriseSettlementDO enterpriseSettlementDO, SystemUserInfoT userInfo)
	{
		int line = enterpriseSettlementDao.insert(enterpriseSettlementDO);
		return line;
	}
	
	
	/**
	 * 
	 * @Title: separateAccountingSettlement 
	 * @Description: 分账结算
	 * @param enterpriseid 业务组织主键
	 * @param deliveryAdviceCode 发货通知单号
	 * @return
	 * @return: ResultMsg
	 * @throws InterruptedException 
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public ResultMsg separateAccountingSettlement(Long pk_collection_confirm,Long enterpriseid,String deliveryAdviceCode,String advanceConfirmCode) throws MsgException, InterruptedException{

		//获取完整的收款确认申请单
		CollectionConfirmVO vo = new CollectionConfirmVO();
		vo.setPk_collection_confirm(pk_collection_confirm);
		vo.setEnterpriseid(enterpriseid);
		CollectionConfirmVO collectionConfirmVO = collectionConfirmService.getCollectionConfirmVOByPrimaryKey(vo);
		
		if(!Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION.equals(collectionConfirmVO.getSourcebilltype())){
			throw new MsgException(Constant.ENTERPRISE_SETTLEMENT_ERROR_CODE_TWO, "该收款确认单数据来源不是发货申请，不需分账结算！");
		}
		
		SaleApplicationDO saleApplicationDO = new SaleApplicationDO();
		saleApplicationDO.setPk_sale_application(collectionConfirmVO.getSourcebillid());
		saleApplicationDO.setEnterpriseid(enterpriseid);
		//寻找对应的发货申请
		SaleApplicationVO saleApplicationVO=  saleApplicationService.getDOByPrimaryKey(saleApplicationDO);
		if(null == saleApplicationVO){
			throw new MsgException("0", "未找到对应的发货申请！");
		}
		//查找对应的累计收款单
		List<CollectionConfirmVO> collectionConfirmVOs = collectionConfirmService.queryCollectionBySource(saleApplicationDO.getPk_sale_application(),collectionConfirmVO.getSourcebilltype(),enterpriseid);
		
		//利润拆分部分
		List<CollectionConfirmVO> resultList = null;
		//对于已结算的收款确认单直接跳过利润拆分
		if (!Constant.COLLECTION_STATE_HAVE.equals(collectionConfirmVO.getState())) {
			resultList = splitProfit(saleApplicationVO,pk_collection_confirm, enterpriseid, advanceConfirmCode);
		}else{
			resultList = new ArrayList<>();
		}

		//发货通知部分
		double totalCollectionAmount = 0d;
		//计算累计收款金额
		for(CollectionConfirmVO bean : collectionConfirmVOs){
			totalCollectionAmount += bean.getCollection_amount();
		}
		
		//发货部分
        if(!Constant.SALEAPPLICATION_STATE_SEND.equals(saleApplicationVO.getState())){
        	//累计收款金额小于支付货款金额
    		if(totalCollectionAmount >= saleApplicationVO.getPayment_amount()){
    			//发货通知
    			DeliveryAdviceVO deliveryAdviceVO = new DeliveryAdviceVO();
    			List<DeliveryItemVO> deliveryItemList = new ArrayList<>();
    			if(null != saleApplicationVO.getItemList()){
    				for(SaleApplicationItemVO item : saleApplicationVO.getItemList()){
    					DeliveryItemVO deliveryItemVO = new DeliveryItemVO();
    					deliveryItemVO.setRownumber(item.getRownumber());
    					deliveryItemVO.setGoods_name(item.getGoods_name());
    					deliveryItemVO.setSpec(item.getSpec());
    					deliveryItemVO.setProducing_area(item.getProducing_area());
    					deliveryItemVO.setQuality(item.getQuality());
    					deliveryItemVO.setPacking(item.getPacking());
    					deliveryItemVO.setPk_measurement(item.getPk_measurement());
    					deliveryItemVO.setMeasurement_name(item.getMeasurement_name());
    					deliveryItemVO.setNumber(item.getNumber());
    					deliveryItemVO.setPrice(item.getPrice());
    					deliveryItemVO.setMoney(item.getMoney());
    					deliveryItemVO.setBatch_number(item.getBatch_number());
    					deliveryItemVO.setStorage_location(item.getStorage_location());
    					deliveryItemVO.setSourcebilltype(item.getSourcebilltype());
    					deliveryItemVO.setSourcebillid(item.getSourcebillid());
    					deliveryItemVO.setSourcebillitemid(item.getSourcebillitemid());
    					deliveryItemList.add(deliveryItemVO);
    				}
    			}
    			
    			deliveryAdviceVO.setDeliveryItemList(deliveryItemList);
    			
    			deliveryAdviceVO.setCode(deliveryAdviceCode);
    			deliveryAdviceVO.setPk_enterprise(saleApplicationVO.getPk_enterprise_application());
    			deliveryAdviceVO.setEnterprise_name(saleApplicationVO.getEnterprise_application_name());
    			deliveryAdviceVO.setApplication_amount(saleApplicationVO.getAmount());
    			deliveryAdviceVO.setAmount(saleApplicationVO.getPayment_amount());
    			deliveryAdviceVO.setPk_currency(saleApplicationVO.getPk_currency());
    			deliveryAdviceVO.setCurrency_name(saleApplicationVO.getCurrency_name());
    			deliveryAdviceVO.setContact(saleApplicationVO.getContact());
    			deliveryAdviceVO.setContact_number(saleApplicationVO.getContact_number());
    			deliveryAdviceVO.setBusi_date(new Date());
    			deliveryAdviceVO.setInputmanid(saleApplicationVO.getInputmanid());
    			deliveryAdviceVO.setInputmanname(saleApplicationVO.getInputmanname());
    			deliveryAdviceVO.setEnterpriseid(enterpriseid);
    			deliveryAdviceVO.setState(Constant.DELIVERYADVICE_STATE_APPROVED);
    			
    			ResultMsg deliveryAdviceRM= deliveryAdviceService.insert(deliveryAdviceVO);
    			
    			if("0".equals(deliveryAdviceRM.getCode())){
    				throw new MsgException("0", "生成发货通知失败！");
    			}
    			
    			//回写发货申请为已发货状态
				ResultMsg saleApplicationRM = saleApplicationService.altState(saleApplicationDO);
				if("0".equals(saleApplicationRM.getCode())){
    				throw new MsgException("0", "回写发货申请为已发货状态失败！");
    			}
				
				//获取资金使用申请的发货数量
				double totalCapitalApplicationItemsNumber = capitalApplicationService.sumApplicationItemsNumber(saleApplicationVO.getPk_capital_application(), saleApplicationVO.getEnterpriseid());
				//获取发货申请以发货的数量
				double totalSaleApplicationItemsNumber =saleApplicationService.countSendNumber(saleApplicationVO.getPk_capital_application(), saleApplicationVO.getEnterpriseid());
				//如果全部货物都已销售,将资金使用申请的销售标识也设置为“已销售”状态。
				if(totalCapitalApplicationItemsNumber == totalSaleApplicationItemsNumber){
					CapitalApplicationDO laDO = new CapitalApplicationDO();
					laDO.setPk_capital_application(saleApplicationVO.getPk_capital_application());
					laDO.setEnterpriseid(saleApplicationDO.getEnterpriseid());
					laDO.setSale_flag(Constant.CAPITALAPPLICATION_SALE_FINISH);
					capitalApplicationService.updateCapitalApplicationSaleFlag(laDO);
				}
    			
//    			
//    			Map<String,Object> param = new HashMap<String,Object>();
//    			//暂时先不管仓库监管编号、仓库、仓库地址
////    			param.put("warehouse_supervise_code", deliveryAdviceVO.getWarehouse_supervise_code());
////    			param.put("warehouse", deliveryAdviceVO.getWarehouse());
////    			param.put("warehouse_address", deliveryAdviceVO.getWarehouse_address());
//    			param.put("contact", deliveryAdviceVO.getContact());
//    			param.put("contact_number", deliveryAdviceVO.getContact_number());
//    			param.put("item_list", deliveryAdviceVO.getDeliveryItemList());
//
//    			//发送WMS
//    			JSONObject sendJson = JSONObject.fromObject(param);
//    			JSONObject json = HttpClientUtil.httpPost(Constant.DELIVERYADVICE_TO_WMS_URL+"?sessionId="+sessionId,sendJson);
//    			System.out.println("DELIVERYADVICE_TO_WMS JSON :" + json);
//    			if ("000000".equals(json.get("code"))) {
//    					
//    			}else{
//    				throw new MsgException("0", "发送WMS失败！！");
//    			}
    			
    		}
        }
        //封装返回数据
        ResultMsg msg = new ResultMsg("1", "分账结算成功");
        ArrayList<Object> arrayList = new ArrayList<Object>();
        arrayList.addAll(resultList);
        msg.setData(arrayList);
		return msg;
	}

	/* 
	 * 利润拆分
	 * 参数: 对应的发货申请单saleApplicationVO和对应的已审核（未结算）状态的收款确认单列表collectionConfirmVOs
	 */
	public List<CollectionConfirmVO> splitProfit(SaleApplicationVO saleApplicationVO,Long pk_collection_confirm, Long enterpriseid, String advanceConfirmCode) throws MsgException, InterruptedException{
		
		//查找所有未结算的针对该发货申请的收款确认单
		Map<String, Object> params = new HashMap<>();
		params.put("enterpriseid", enterpriseid);
		params.put("pk_enterprise_application", saleApplicationVO.getPk_enterprise_application());
		params.put("sourcebilltype", Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION);
		List<CollectionConfirmVO> collectionConfirmVOs = collectionConfirmDao.queryUnsettleCollectionBySaleApplication(params);
		
		//定义时间
		Date date = new Date();

		//企业结算表数据加锁
		lockEnterpriseSettlement(saleApplicationVO.getPk_enterprise_application(), enterpriseid);

		try{
			//查询所有未结息记录
			InterestCalculationVO interestCalculationVO = new InterestCalculationVO();
			interestCalculationVO.setEnterpriseid(saleApplicationVO.getEnterpriseid());
			interestCalculationVO.setPk_enterprise_application(saleApplicationVO.getPk_enterprise_application());
			interestCalculationVO.setState(Constant.INTEREST_CALCULATION_STATE_NO);
			List<InterestCalculationVO> notSettlementList = interestCalculationDao.selectNotSettlement(interestCalculationVO);

			//获取对应的结算单
			EnterpriseSettlementDO enterpriseSettlementDO = new EnterpriseSettlementDO();
			enterpriseSettlementDO.setPk_enterprise(saleApplicationVO.getPk_enterprise_application());
			enterpriseSettlementDO.setEnterpriseid(saleApplicationVO.getEnterpriseid());
			EnterpriseSettlementVO enterpriseSettlementVO = enterpriseSettlementDao.getByEnterprise(enterpriseSettlementDO);
			if (enterpriseSettlementVO == null) {
				throw new MsgException("0", "企业结算表未注册该运营企业");
			}
			//获三大费用的原始数据，并排除null
			if (enterpriseSettlementVO.getAmount() == null) {
				enterpriseSettlementVO.setAmount(0d);
			}
			if (enterpriseSettlementVO.getLogistics_cost() == null) {
				enterpriseSettlementVO.setLogistics_cost(0d);
			}
			if (enterpriseSettlementVO.getSupervision_cost() == null) {
				enterpriseSettlementVO.setSupervision_cost(0d);
			}
			double loan = enterpriseSettlementVO.getAmount();
			double logisticsCost = enterpriseSettlementVO.getLogistics_cost();
			double SupervisionCost = enterpriseSettlementVO.getSupervision_cost();

			//判断是否付息后结余，不够利息则不做处理
			//未结息总金额
			Double totalInterest = 0d;
			//所有未结息的利息记录id的list，用于更新的sql
			if (notSettlementList != null && notSettlementList.size() > 0) {
				for (InterestCalculationVO bean : notSettlementList) {
					//计算所有未结利息
					totalInterest += bean.getDays_interest();
					bean.setState(Constant.INTEREST_CALCULATION_HAVE);
				}
			}
			//将总利息设置到结算表中对象中
			//将总利息变成小数点后两位
			totalInterest = new BigDecimal(totalInterest).setScale(2, BigDecimal.ROUND_HALF_EVEN).doubleValue();
			enterpriseSettlementVO.setTotalInterest(totalInterest);
			//实现利润拆分
			double totalOverFlow = distributionOneCollection(enterpriseSettlementVO, collectionConfirmVOs);

			//-----------------------------还款不够利息，不做操作-------------------------------
			if (enterpriseSettlementVO.getTotalInterest() > 0) {
				//解锁
				unlockEnterpriseSettlement(saleApplicationVO.getPk_enterprise_application(), enterpriseid);

				//查询当前推送的收款确认单
				CollectionConfirmVO vo = new CollectionConfirmVO();
				vo.setPk_collection_confirm(pk_collection_confirm);
				vo.setEnterpriseid(enterpriseid);
				CollectionConfirmVO collectionConfirmVO = collectionConfirmService.getCollectionConfirmVOByPrimaryKey(vo);
				List<CollectionConfirmVO> returnList = new ArrayList<>();
				returnList.add(collectionConfirmVO);

				return returnList;
			}
			//--------------------------------收款金额够还利息-------------------------------

			//-----------更新数据库所有利息为已结息状态----------------
			if (notSettlementList != null && notSettlementList.size() > 0) {
				//创建临时表
				interestCalculationDao.createTempTable();
				//向临时表插入数据
				int m = interestCalculationDao.insertTempTable(notSettlementList);
				if (m != notSettlementList.size()) {
					throw new MsgException("0", "利息数据插入临时表失败");
				}
				//更新利息表数据
				m = interestCalculationDao.updateStateWithTempTable();
				if (m != notSettlementList.size()) {
					throw new MsgException("0", "更新利息状态为已结息出错");
				}
			}

			//更新所有的收款确认单
			updateAllCollectionUseFor(collectionConfirmVOs);
			//获取还款分配情况
			double diffLoan = loan - enterpriseSettlementVO.getAmount();
			double diffLogistics = logisticsCost - enterpriseSettlementVO.getLogistics_cost();
			double diffSupervision = SupervisionCost - enterpriseSettlementVO.getSupervision_cost();

			//企业结算单需要更新的情况
			if (diffLoan > 0 || diffLogistics > 0 || diffSupervision > 0) {
				//更新企业结算单
				updateEnterpriseSettlement(enterpriseid, date, enterpriseSettlementVO);
			}

			//需要调用支付接口的情况
			if (diffLogistics > 0 || diffSupervision > 0 || totalOverFlow > 0) {
				//---------------------------------封装调用支付接口传递的参数-------------------------------------
				//封装数据准备发送
				Map<String, Double> resultMap = new HashMap<>();
				resultMap.put("diffLogistics", diffLogistics);
				resultMap.put("diffSupervision", diffSupervision);
				resultMap.put("totalOverFlow", totalOverFlow);
				//TODO 调用支付接口
				log.info("//=======================调用支付接口=====================");
				log.info("支付物流费用： " + diffLogistics);
				log.info("支付监管费用： " + diffSupervision);
				log.info("支付溢出费用： " + totalOverFlow);

				//实现放款
				packagingAndInsertAdvanceConfirm(saleApplicationVO, pk_collection_confirm,totalOverFlow + diffLogistics + diffSupervision, advanceConfirmCode);
			}
			unlockEnterpriseSettlement(saleApplicationVO.getPk_enterprise_application(), enterpriseid);
			return collectionConfirmVOs;

		}catch(MsgException e){
			unlockEnterpriseSettlement(saleApplicationVO.getPk_enterprise_application(), enterpriseid);
			e.printStackTrace();
			throw e;
		}catch(Exception e){
			unlockEnterpriseSettlement(saleApplicationVO.getPk_enterprise_application(), enterpriseid);
			e.printStackTrace();
			throw new MsgException("0", "分账结算出现未知异常！");
		}
	}

	private void packagingAndInsertAdvanceConfirm (SaleApplicationVO saleApplicationVO, Long pk_collection_confirm, double amount, String advanceConfirmCode) throws MsgException{
		Date date = new Date();
		AdvanceConfirmDO confirmDO = new AdvanceConfirmDO();
		confirmDO.setCode(advanceConfirmCode);
		confirmDO.setPk_enterprise_application(saleApplicationVO.getPk_enterprise_application());
		confirmDO.setEnterprise_application_name(saleApplicationVO.getEnterprise_application_name());
		confirmDO.setContact(saleApplicationVO.getContact());
		confirmDO.setContact_number(saleApplicationVO.getContact_number());
		confirmDO.setApplication_amount(amount);
		confirmDO.setAdvances_amount(amount);
		confirmDO.setPk_currency(saleApplicationVO.getPk_currency());
		confirmDO.setCurrency_name(saleApplicationVO.getCurrency_name());
		confirmDO.setApplication_date(date);
		confirmDO.setBusi_date(date);
		confirmDO.setBank_account("63225885522552212112");
		confirmDO.setBank_deposit("上海银行嘉定支行");
		confirmDO.setSourcebilltype(Constant.ADVANCE_CONFIRM_COLLECTION_CONFIRM);
		confirmDO.setSourcebillid(pk_collection_confirm);
		confirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVED);
		confirmDO.setInputmanid(saleApplicationVO.getInputmanid());
		confirmDO.setInputmanname(saleApplicationVO.getInputmanname());
		confirmDO.setBookindate(date);
		confirmDO.setEnterpriseid(saleApplicationVO.getEnterpriseid());
		confirmDO.setBooked_flag(Constant.ADVANCE_CONFIRM_BOOK_FLAG_NO);
		confirmDO.setPayment_method("银行转账");
		confirmDO.setPayment_billno(""+new Random().nextInt(1000));
		int m = advanceConfirmService.insert(confirmDO);
		if (m != 1) {
			throw new MsgException("0", "插入放款确认单失败");
		}
	}


	private void updateEnterpriseSettlement(Long enterpriseid,
			Date date, EnterpriseSettlementVO enterpriseSettlementVO) throws MsgException {
		enterpriseSettlementVO.setEnterpriseid(enterpriseid);
		enterpriseSettlementVO.setModefydate(date);
		
		int m = enterpriseSettlementDao.updateMoney(enterpriseSettlementVO);
		if (m != 1) {
			throw new MsgException("0", "更新id为" + enterpriseSettlementVO.getPk_enterprise_settlement() + "企业结算单失败");
		}
	}

	private void updateAllCollectionUseFor(List<CollectionConfirmVO> collectionConfirmVOs)
			throws MsgException {
		int m = 0;
		for (int i = 0; i < collectionConfirmVOs.size(); i++) {
			CollectionConfirmVO bean = collectionConfirmVOs.get(i);
			//更新收款确认单
			//相应的更新收款确认单置为已结算，并更新相关四个金额字段
			m = collectionConfirmDao.updateCollectionUseFor(bean);
			if (m != 1) {
				throw new MsgException("0", "更新id为" + bean.getPk_collection_confirm() + "收款确认单失败");
			}
		}
	}
	
	/*
	 * 根据收款金额实现利润之外的资金分配
	 * 返回溢出金额
	 */
	public double distributionOneCollection(EnterpriseSettlementVO enterpriseSettlementVO, List<CollectionConfirmVO> collectionConfirmVOs){
		
		double totalOverFlow = 0d;
		double totalInterest = enterpriseSettlementVO.getTotalInterest();
		double loan = enterpriseSettlementVO.getAmount();
		double logisticsCost = enterpriseSettlementVO.getLogistics_cost();
		double supervisionCost = enterpriseSettlementVO.getSupervision_cost();
		
		for (int i = 0; i < collectionConfirmVOs.size(); i++) {
			
			CollectionConfirmVO collectionConfirmVO = collectionConfirmVOs.get(i);
			double repayment = collectionConfirmVO.getCollection_amount();

			//还利息
			if(repayment <= totalInterest){
				//还款不多于利息
				collectionConfirmVO.setRepayment_interest(repayment);
				totalInterest -= repayment;
				repayment = 0d;
			}else{
				collectionConfirmVO.setRepayment_interest(totalInterest);
				repayment -= totalInterest;
				totalInterest= 0d;
			}
			//还本金
			if(repayment <= loan){
				//还款不多于利息+本金
				collectionConfirmVO.setRepayment_principal(repayment);
				loan -= repayment;
				repayment = 0d;
			}else{
				collectionConfirmVO.setRepayment_principal(loan);
				repayment -= loan;
				loan= 0d;
			}
			//还物流费用
			if(repayment <= logisticsCost){
				//还款不多于利息+本金+物流费用
				collectionConfirmVO.setLogistics_cost(repayment);
				logisticsCost -= repayment;
				repayment = 0d;
			}else{
				collectionConfirmVO.setLogistics_cost(logisticsCost);
				repayment -= logisticsCost;
				logisticsCost= 0d;
			}
			//还管理费用
			if(repayment <= supervisionCost){
				//还款不多于利息+本金+物流费用+管理费用
				collectionConfirmVO.setSupervision_cost(repayment);
				supervisionCost -= repayment;
				repayment = 0d;
			}else{
				collectionConfirmVO.setSupervision_cost(supervisionCost);
				repayment -= supervisionCost;
				supervisionCost= 0d;
			}
			
			//还款多于全部金额有溢出
			collectionConfirmVO.setOverflow_amount(repayment);
			totalOverFlow += repayment;
		}
		
		enterpriseSettlementVO.setTotalInterest(totalInterest);
		enterpriseSettlementVO.setAmount(loan);
		enterpriseSettlementVO.setLogistics_cost(logisticsCost);
		enterpriseSettlementVO.setSupervision_cost(supervisionCost);
		
		return totalOverFlow;
	}

	public ResultMsg whetherDelivery(Long pk_collection_confirm,Long enterpriseid){
		//获取完整的收款确认申请单
		CollectionConfirmVO vo = new CollectionConfirmVO();
		vo.setPk_collection_confirm(pk_collection_confirm);
		vo.setEnterpriseid(enterpriseid);
		CollectionConfirmVO collectionConfirmVO = collectionConfirmService.getCollectionConfirmVOByPrimaryKey(vo);
				
		if(!Constant.COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION.equals(collectionConfirmVO.getSourcebilltype())){
			return new ResultMsg("0", "该收款确认单数据来源不是发货申请，不需分账结算！");
		}
				
		SaleApplicationDO saleApplicationDO = new SaleApplicationDO();
		saleApplicationDO.setPk_sale_application(collectionConfirmVO.getSourcebillid());
		saleApplicationDO.setEnterpriseid(enterpriseid);
		//寻找对应的发货申请
		SaleApplicationVO saleApplicationVO=  saleApplicationService.getDOByPrimaryKey(saleApplicationDO);
		if(null == saleApplicationVO){
			return new ResultMsg("0", "未找到对应的发货申请！");
		}
		//查找对应的累计收款单
		List<CollectionConfirmVO> collectionConfirmVOs = collectionConfirmService.queryCollectionBySource(saleApplicationDO.getPk_sale_application(),collectionConfirmVO.getSourcebilltype(),enterpriseid);
		
		//发货通知部分
		double totalCollectionAmount = 0d;
		//计算累计收款金额
		for(CollectionConfirmVO bean : collectionConfirmVOs){
			totalCollectionAmount += bean.getCollection_amount();
		}
				
		//发货部分
		if(!Constant.SALEAPPLICATION_STATE_SEND.equals(saleApplicationVO.getState())){
		    //累计收款金额小于支付货款金额
		    if(totalCollectionAmount >= saleApplicationVO.getPayment_amount()){
		    	return new ResultMsg("1", "需要生成发货通知单号！");
		    }
		}
		return new ResultMsg("0", "不需要生成发货通知单号！");
	}

	/**
	 * 
	 * @Title: listByPage 
	 * @Description: 分页查询分账结算
	 * @param page
	 * @param bean
	 * @return
	 * @return: Page<EnterpriseSettlementVO>
	 */
	public Page<EnterpriseSettlementVO> listByPage(Page<EnterpriseSettlementVO> page, EnterpriseSettlementVO bean){
		
		/** 设置数据查询参数 */
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("name", bean.getName());
		param.put("enterprise_name", bean.getEnterprise_name());
		param.put("beginDate", bean.getStartDate());
		param.put("endDate", bean.getEndDate());
		param.put("searchValue", bean.getSearchValue());
		param.put("enterprise_name", bean.getEnterprise_name());
		param.put("enterpriseid", bean.getEnterpriseid());
		param.put("minAmount",bean.getMinAmount());
		param.put("maxAmount", bean.getMaxAmount());
		
		/** 分页参数 */
		int startNum = (page.getPageNo() - 1) * (page.getPageSize());
		param.put("startNum", startNum);
		param.put("pageSize", page.getPageSize());
		
		List<EnterpriseSettlementVO> settlementVOs = enterpriseSettlementDao.selectByPage(param);
		page.setResults(settlementVOs);

		int count = (int) enterpriseSettlementDao.queryCount(param);
		page.setTotalCount(count);
		page.setPageCount(count / page.getPageSize()
				+ (count % page.getPageSize() == 0 ? 0 : 1));
		return page;
	}

	/**
	 * @Title: selectByName 
	 * @author:致远
	 * @Description: 按照企业名称模糊搜索
	 * @param keyword
	 * @param enterpriseId
	 * @return
	 * @return: List<EnterpriseSettlementVO>
	 */
	public List<EnterpriseSettlementVO> selectByName(String keyword, long enterpriseId) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("enterpriseId", enterpriseId);
		param.put("enterprise_name", keyword);
//		param.put("state", keyword);
		return enterpriseSettlementDao.selectByName(param);
	}

	/**
	 * @Title: selectByCode 
	 * @author:致远
	 * @Description: 按照企业编码搜索
	 * @param code
	 * @param enterpriseId
	 * @return
	 * @return: EnterpriseSettlementVO
	 */
	public EnterpriseSettlementVO selectByCode(String code, long enterpriseId) {
		Map<String, Object> param = new HashMap<String,Object>();
		param.put("enterpriseId", enterpriseId);
		param.put("code", code);
//		param.put("state", code);
		List<EnterpriseSettlementVO> list = enterpriseSettlementDao.selectByCode(param);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @Title: queryById 
	 * @Description: 根据企业结息id查询企业结息详情
	 * @param id
	 * @return
	 * @return: EnterpriseSettlementVO
	 */
	public EnterpriseSettlementVO queryById(String id, Long enterpriseid){
		if(id == null || "".equals(id)){ return null; }
		return enterpriseSettlementDao.getById(Long.valueOf(id), enterpriseid);
	}
	
	
	/**
	 * 
	 * @Title: lockEnterpriseSettlement 
	 * @Description: 企业结算表加锁
	 * @param pk_enterprise_application 申请企业ID
	 * @param enterpriseid 企业组织ID
	 * @throws MsgException
	 * @throws InterruptedException
	 * @return: void
	 */
	public void lockEnterpriseSettlement(Long pk_enterprise_application ,Long enterpriseid) throws MsgException, InterruptedException{

		//判断企业结算表数据是否有人在使用
		String key = "SMF_EnterpriseSettlement_" + pk_enterprise_application + "_" + enterpriseid+"_flag";
		String flag = CacheUtils.getValue(key);
		int i = 0;
		while (flag != null && false == "".equals(flag)) {
			if (i > 10) {
				throw new MsgException(Constant.ENTERPRISE_SETTLEMENT_ERROR_CODE_THREE, "该结算企业，企业结算表被锁！");
			}
			//任务延迟2ms在判断
			Thread.sleep(2);
			flag = CacheUtils.getValue(key);
			i++;
		}
		CacheUtils.put(key, "1");
	}
	
	/**
	 * 
	 * @Title: unlockEnterpriseSettlement 
	 * @Description: 企业结算表解锁
	 * @param pk_enterprise_application 申请企业ID
	 * @param enterpriseid 企业组织ID
	 * @return: void
	 */
	public void unlockEnterpriseSettlement(Long pk_enterprise_application ,Long enterpriseid){
		CacheUtils.remove("SMF_EnterpriseSettlement_" + pk_enterprise_application + "_" + enterpriseid+"_flag");
	}
	
}
