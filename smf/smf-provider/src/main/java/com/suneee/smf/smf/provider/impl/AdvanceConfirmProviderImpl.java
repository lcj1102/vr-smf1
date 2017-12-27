package com.suneee.smf.smf.provider.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.suneee.scn.scf.api.provider.CodeRuleRestProvider;
import com.suneee.smf.smf.api.provider.AdvanceConfirmProvider;
import com.suneee.smf.smf.common.Constant;
import com.suneee.smf.smf.common.MsgException;
import com.suneee.smf.smf.common.ResultMsg;
import com.suneee.smf.smf.model.*;
import com.suneee.smf.smf.service.AdvanceConfirmErrorService;
import com.suneee.smf.smf.service.AdvanceConfirmService;
import com.suneee.smf.smf.service.CapitalApplicationService;
import com.suneee.smf.smf.service.CapitalSettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

@Service//指明dubbo服务
public class AdvanceConfirmProviderImpl implements AdvanceConfirmProvider {
	@Autowired
	private CapitalApplicationService capitalApplicationService;

	@Autowired
	private CapitalSettlementService capitalSettlementService;

	@Autowired
	private AdvanceConfirmService advanceConfirmService;

	@Autowired
	private AdvanceConfirmErrorService advanceConfirmErrorService;

	private static final Logger log= LoggerFactory.getLogger(AdvanceConfirmProviderImpl.class);

	@Reference(url="dubbo://172.16.36.68:20913/com.suneee.scn.scf.api.provider.CodeRuleRestProvider")
	private CodeRuleRestProvider codeRuleRestProvider;


	@Override
	public ResultMsg add(AdvanceConfirmDO advanceConfirmDO,String pushType){
		if(Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)) {
			try {
				//通过来源类型查询对应数据
				String sourcebilltype = advanceConfirmDO.getSourcebilltype();
				Long sourcebillid = advanceConfirmDO.getSourcebillid();
				Long enterpriseid = null;
				//List<CollectionConfirmVO> collectionConfirmVOs = new ArrayList<>();
				if (Constant.ADVANCE_CONFIRM_CAPITAL_APPLICATION.equals(sourcebilltype)) {
					CapitalApplicationVO applicationVO = new CapitalApplicationVO();
					applicationVO.setPk_capital_application(advanceConfirmDO.getSourcebillid());
					CapitalApplicationVO restByPrimaryKey = capitalApplicationService.getRestByPrimaryKey(applicationVO);
					enterpriseid = restByPrimaryKey.getEnterpriseid();
				}
				if (Constant.ADVANCE_CONFIRM_CAPITAL_SETTLEMENT.equals(sourcebilltype)) {

					CapitalSettlementVO settlementVO = new CapitalSettlementVO();
					settlementVO.setPk_capital_settlement(advanceConfirmDO.getSourcebillid());
					CapitalSettlementVO restByPrimaryKey = capitalSettlementService.getRestByPrimaryKey(settlementVO);
					enterpriseid = restByPrimaryKey.getEnterpriseid();
				}
				AdvanceConfirmVO adConfirmVO = advanceConfirmService.queryAdvanceBySource(sourcebillid, sourcebilltype, enterpriseid);
				if(adConfirmVO==null) {
					Map<String, Object> map = codeRuleRestProvider.submitSingleton(enterpriseid, Constant.ADVANCE_CONFIRM_CODERULE_CODE);
					if ("1".equals(map.get("code"))) {
						if ("".equals((String) map.get("msg"))) {
							return new ResultMsg("0", "生成放款单号失败！！！");
						}
					}
					advanceConfirmDO.setEnterpriseid(enterpriseid);
					advanceConfirmDO.setCode((String) map.get("msg"));
					if (advanceConfirmDO.getBooked_flag() == null) {
						advanceConfirmDO.setBooked_flag(Constant.ADVANCE_CONFIRM_BOOK_FLAG_NO);
					}
					advanceConfirmDO.setBookindate(new Date());
					advanceConfirmDO.setState(Constant.ADVANCE_CONFIRM_STATE_APPROVED);

					advanceConfirmService.add(advanceConfirmDO, enterpriseid);
				}else{
					return new ResultMsg("1", "该单已经推送过放款确接口！");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ResultMsg("0", "生成放款确认单失败！");
			}
		}

		if(Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)) {
			//调用回写
			new Thread(new Runnable() {
				@Override
				public void run() {
					split(advanceConfirmDO, pushType);
				}
			}).start();
		}else{
			split(advanceConfirmDO,pushType);
		}

		log.info("放款确认Peovider调用成功============================");
		return new ResultMsg("1", "保存成功");

	}

	private void split(AdvanceConfirmDO advanceConfirmDO,String pushType){
		try {
			//AdvanceConfirmDO advanceConfirmDO = new AdvanceConfirmDO();
			//advanceConfirmDO.setSourcebilltype(bean.getSourcebilltype());
			//advanceConfirmDO.setPk_enterprise_application(bean.getPk_enterprise_application());
			//advanceConfirmDO.setEnterpriseid(bean.getEnterpriseid());
			//advanceConfirmDO.setAdvances_amount(bean.getAdvances_amount());
			//advanceConfirmDO.setSourcebillid(bean.getSourcebillid());

			if(Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
				Thread.sleep(20000);
			}
			ResultMsg msg = advanceConfirmService.updateSourceState(advanceConfirmDO);
			if ("1".equals(msg.getCode())) {
				AdvanceConfirmVO advanceConfirmVO = new AdvanceConfirmVO();
				advanceConfirmVO.setPk_advance_confirm(advanceConfirmDO.getPk_advance_confirm());
				advanceConfirmVO.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
				advanceConfirmVO.setState(Constant.ADVANCECONFIRMERROR_STATE_FINISH);

				if(Constant.ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL.equals(pushType)){
					//删除放款异常记录
					advanceConfirmErrorService.deleteErrorDO(advanceConfirmVO);
				}else{
					//修改对应的放款异常状态为已处理
					advanceConfirmErrorService.updateErrorDOState(advanceConfirmVO);
				}

			}

		} catch (MsgException e) {
			try {
				//修改状态失败，更新对应的放款异常
				AdvanceConfirmErrorVO bean = new AdvanceConfirmErrorVO();
				bean.setError_msg(e.getMessage());
				bean.setPk_advance_confirm(advanceConfirmDO.getPk_advance_confirm());
				bean.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
				advanceConfirmErrorService.update(bean);
			}catch (Exception ee){
				ee.printStackTrace();
				log.info("MsgException更新放款异常失败============================");
			}
		}catch (Exception e){
			try {
				log.error(e.getMessage());
				AdvanceConfirmErrorVO bean = new AdvanceConfirmErrorVO();
				bean.setPk_advance_confirm(advanceConfirmDO.getPk_advance_confirm());
				bean.setEnterpriseid(advanceConfirmDO.getEnterpriseid());
				bean.setError_msg("修改状态未知错误异常！");
				advanceConfirmErrorService.update(bean);
			}catch (Exception ee){
				ee.printStackTrace();
				log.info("Exception更新放款异常失败============================");
			}
		}

	}
}
