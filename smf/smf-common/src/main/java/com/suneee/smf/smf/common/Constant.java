package com.suneee.smf.smf.common;

public class Constant {


    //收款确认信息状态
    public static final String COLLECTION_STATE_NEW = "新建";
    public static final String COLLECTION_STATE_APPROVING = "审核中";
    public static final String COLLECTION_STATE_APPROVED = "已审核";
    public static final String COLLECTION_STATE_DELETE = "已作废";
    public static final String COLLECTION_STATE_END = "审核拒绝";
    public static final String COLLECTION_STATE_HAVE = "已结算";
	public static final String COLLECTION_CONFIRM_BOOK_FLAG_NO = "未记账";
    
    public static final String COLLECTION_SOURCE_BILL_TYPE_SALEAPPLICATION = "发货申请";
    public static final String COLLECTION_SOURCE_BILL_TYPE_CAPITALINJECTION = "资金注入";

    public static final String COLLECTION_APPROVE_NAME = "SMF收款确认审批流程";			//收款申请审批流程名称
    public static final String COLLECTION_APPROVE_CODE = "smfCollectionConfirm";	//收款审批流程编码
    public static final String COLLECTION_APPROVE_FRISTTASK = "fristTask";		//收款申请审批流程第一级审批节点id
    public static final String COLLECTION_APPROVE_LASTTASK = "finance";		//收款审批流程最后级审批节点id
    //收款执行编码生成规则编号
    public static final String COLLECTION_CODERULE_CODE = "CollectionConfirmCode";

	/**
	 * 发货申请状态值
	 */
	public static final String SALEAPPLICATION_STATE_NEW = "新建";
	public static final String SALEAPPLICATION_STATE_APPROVING = "审核中";
	public static final String SALEAPPLICATION_STATE_APPROVED = "已审核";
	public static final String SALEAPPLICATION_STATE_DELETE = "已作废";
	public static final String SALEAPPLICATION_STATE_END = "审核拒绝";
	public static final String SALEAPPLICATION_STATE_SEND = "已发货";

	/**
	 * 发货申请编码生成规则编号
	 */
	public static final String SALEAPPLICATION_CODERULE_CODE = "SaleApplicationCode";

	/**
	 * 销售申请审批流程
	 */
	public static final String SALEAPPLICATION_APPROVE_NAME = "SMF销售申请审批流程";			//企业申请审批流程名称
	public static final String SALEAPPLICATION_APPROVE_CODE = "smfSaleApplication";	//企业申请审批流程编码
	public static final String SALEAPPLICATION_APPROVE_FRISTTASK = "fristTask";		//企业申请审批流程第一级审批节点id
	public static final String SALEAPPLICATION_APPROVE_LASTTASK = "finance";		//企业申请审批流程最后级审批节点id

	//资金注入状态
	public static final String CAPITALINJECTION_STATE_NEW = "新建";

	public static final String CAPITALINJECTION_STATE_APPROVING = "审核中";
	public static final String CAPITALINJECTION_STATE_APPROVED = "已审核";
	public static final String CAPITALINJECTION_STATE_DELETE = "已作废";
	public static final String CAPITALINJECTION_STATE_END = "审核拒绝";
	//资金注入编码生成规则编号
	public static final String CAPITAL_INJECTION_CODERULE_CODE = "CapitalInjectionCode";
	//资金注入附件上传
	public static final String CAPITALINJECTION_FILE_CODE = "CapitalInjection";
	//资金注入审批常量
	public static final String CAPITALINJECTION_APPROVE_NAME = "SMF资金注入审批流程";	    //资金注入申请审批流程名称
	public static final String CAPITALINJECTION_APPROVE_CODE = "smfCapitalInjection";  //资金注入申请审批流程编码
	public static final String CAPITALINJECTION_APPROVE_FRISTTASK = "fristTask";    //资金注入申请审批流程第一级审批节点id
	public static final String CAPITALINJECTION_APPROVE_LASTTASK = "finance";		//资金注入申请审批流程最后级审批节点id

	//资金使用申请
	public static final String CAPITALAPPLICATION_STATE_NEW = "新建";
	public static final String CAPITALAPPLICATION_STATE_APPROVING = "审核中";
	public static final String CAPITALAPPLICATION_STATE_APPROVED = "已审核";
	public static final String CAPITALAPPLICATION_STATE_END = "审核拒绝";
	public static final String CAPITALAPPLICATION_STATE_DEL = "已作废";
	
	public static final String CAPITALAPPLICATION_SALE_NEW = "未销售";
	public static final String CAPITALAPPLICATION_SALE_FINISH = "已销售";
	
	public static final String CAPITALAPPLICATION_FILE_CODE = "CapitalApplication";
	
	public static final String CAPITALAPPLICATION_CODERULE_CODE = "CapitalApplicationCode";
	
	public static final String CAPITALAPPLICATION_APPROVE_NAME = "SMF资金使用申请审批流程";			//企业申请审批流程名称
	public static final String CAPITALAPPLICATION_APPROVE_CODE = "smfCapitalApplication";
	public static final String CAPITALAPPLICATION_APPROVE_FRISTTASK = "fristTask";		//企业申请审批流程第一级审批节点id
	public static final String CAPITALAPPLICATION_APPROVE_LASTTASK = "finance";		//企业申请审批流程最后级审批节点id

	//发货通知状态
	public static final String DELIVERYADVICE_STATE_NEW = "新建";
	public static final String DELIVERYADVICE_STATE_APPROVING = "审核中";
	public static final String DELIVERYADVICE_STATE_APPROVED = "已审核";
	public static final String DELIVERYADVICE_STATE_DELETE = "已作废";
	public static final String DELIVERYADVICE_STATE_END = "审核拒绝";
	
	public static final String DELIVERYADVICE_APPROVE_NAME = "SMF发货通知审批流程";			//发货通知审批流程名称
	public static final String DELIVERYADVICE_APPROVE_CODE = "smfDeliveryAdvice";		    //发货通知审批流程编码
	public static final String DELIVERYADVICE_APPROVE_FRISTTASK = "fristTask";		    //发货通知审批流程第一级审批节点id
	public static final String DELIVERYADVICE_APPROVE_LASTTASK = "finance";		        //发货通知审批流程最后级审批节点id
	
	public static final String DELIVERYADVICE_CODERULE_CODE = "DATZ";
	//发货通知对接wms的接口地址
	public static final String DELIVERYADVICE_TO_WMS_URL = "http://test.wms.yn.weilian.cn:81/wms/finance/sendDeliveryAdvice";
	
	//附件
	public static final String ATTACHMENTS_STATE_NEW = "新建";
	public static final String ATTACHMENTS_STATE_DEL = "已作废";
	
	//资金退出结算状态
	public static final String CAPITALSETTLEMENT_STATE_NEW = "新建";
	public static final String CAPITALSETTLEMENT_STATE_APPROVING = "审核中";
	public static final String CAPITALSETTLEMENT_STATE_APPROVED = "已审核";
	public static final String CAPITALSETTLEMENT_STATE_DELETE = "已作废";
	public static final String CAPITALSETTLEMENT_STATE_END = "审核拒绝";
	public static final String CAPITALSETTLEMENT_STATE_FINSIH = "已完结";
	
	public static final String CAPITALSETTLEMENT_FILE_CODE = "smfCapitalSettlement";
	public static final String CAPITALSETTLEMENT_CODERULE_CODE = "ZJTC";
	
	public static final String CAPITALSETTLEMENT_APPROVE_NAME = "SMF资金退出结算审批流程";	//资金退出结算审批流程名称
	public static final String CAPITALSETTLEMENT_APPROVE_CODE = "smfCapitalSettlement";	//资金退出结算审批流程编码
	public static final String CAPITALSETTLEMENT_APPROVE_FRISTTASK = "fristTask";	//资金退出结算审批流程第一级审批节点id
	public static final String CAPITALSETTLEMENT_APPROVE_LASTTASK = "finance";		//资金退出结算审批流程最后级审批节点id
	

	//放款确认状态
	public static final String ADVANCE_CONFIRM_STATE_NEW = "新建";
	public static final String ADVANCE_CONFIRM_STATE_APPROVING = "审核中";
	public static final String ADVANCE_CONFIRM_STATE_APPROVED = "已审核";
	public static final String ADVANCE_CONFIRM_STATE_DELETE = "已作废";
	public static final String ADVANCE_CONFIRM_STATE_END = "审核拒绝";
	public static final String ADVANCE_CONFIRM_BOOK_FLAG_NO = "未记账";
	//放款确认来源单据类型
	public static final String ADVANCE_CONFIRM_CAPITAL_APPLICATION = "资金使用申请";
	public static final String ADVANCE_CONFIRM_CAPITAL_SETTLEMENT = "资金退出申请";
	public static final String ADVANCE_CONFIRM_COLLECTION_CONFIRM = "收款确认单";

	public static final String ADVANCE_CONFIRM_CODERULE_CODE = "FKQR";
	
	public static final String ADVANCE_CONFIRM_APPROVE_NAME = "SMF放款确认审批流程";			//企业申请审批流程名称
	public static final String ADVANCE_CONFIRM_APPROVE_CODE = "smfAdvanceConfirm";	//企业申请审批流程编码
	public static final String ADVANCE_CONFIRM_APPROVE_FRISTTASK = "fristTask";		//企业申请审批流程第一级审批节点id
	public static final String ADVANCE_CONFIRM_APPROVE_LASTTASK = "finance";		//企业申请审批流程最后级审批节点id
	
	//收款确认异常
	public static final String COLLECTIONCONFIRMERROR_STATE_DEAL = "未处理";
	public static final String COLLECTIONCONFIRMERROR_STATE_FINISH = "已处理";
	
	//长期贷款合同状态
	public static final String LOAN_CONTRACT_CODERULE_CODE = "LoanContractCode";    //贷款合同单据编码
	public static final String ENTERPRISE_SETTLEMENT_CODERULE_CODE = "EnterpriseSettlementCode";    //企业结算单据编码
	
	public static final String LOAN_CONTRACT_STATE_NEW = "新建";
	public static final String LOAN_CONTRACT_STATE_APPROVING = "审核中";
	public static final String LOAN_CONTRACT_STATE_APPROVED = "已审核";
	public static final String LOAN_CONTRACT_STATE_DELETE = "已作废";
	public static final String LOAN_CONTRACT_STATE_END = "审核拒绝";
	
	public static final String LOAN_CONTRACT_APPROVE_NAME = "SMF长期贷款合同审批流程";			//流程名称
	public static final String LOAN_CONTRACT_APPROVE_CODE = "smfLoanContract";			//流程编码
	public static final String LOAN_CONTRACT_APPROVE_FRISTTASK = "firstTask";		//程第一级审批节点id
	public static final String LOAN_CONTRACT_APPROVE_LASTTASK = "finance";			//最后一级审批节点id
	
	//长期借款合同附件编码
	public static final String LOAN_CONTRACT_FILE_CODE = "LoanContract";
	
	//企业计息
	public static final String INTEREST_CALCULATION_STATE_NO = "未结息";
	public static final String INTEREST_CALCULATION_HAVE = "已结息";
	//资金结余
	public static final String CAPITALBALANCE_STATE_NEW = "新建";
	public static final String CAPITALBALANCE_FOR_COLLECTIONCONFIRM_DEAL = "未记账";
	public static final String CAPITALBALANCE_FOR_COLLECTIONCONFIRM_FINISH = "已记账";
	
	//企业地位
	public static final String ENTERPRISE_INVESTMENT = "投资方";
	public static final String ENTERPRISE_OPERATION = "产业运营企业";
	
	//分账结算
	public static final String ENTERPRISE_SETTLEMENT_ERROR_CODE_TWO = "2"; //收款确认单数据来源不是发货申请，不需分账结算
	public static final String ENTERPRISE_SETTLEMENT_ERROR_CODE_THREE = "3"; //数据被锁，加锁失败
	
	//发货申请二次审批成功标志
	public static final String APPROVE_SUCCESS = "FINANCE_SUCESS";

	//放款确认异常
	public static final String ADVANCECONFIRMERROR_STATE_DEAL = "未处理";
	public static final String ADVANCECONFIRMERROR_STATE_FINISH = "已处理";

	public static final String COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_TIMING = "定时任务";
	public static final String COLLECTION_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL = "正常调用";

	public static final String ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_TIMING = "放款定时任务";
	public static final String ADVANCE_CONFIRM_PROVIDER_PUSH_TYPE_NORMAL = "放款正常调用";
}
