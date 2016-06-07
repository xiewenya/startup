package com.github.startup.sina.utils;

public class SinaConsts {
	/**
	 * 版本号
	 */
	public static final String VERSION_1 = "1.0";

    public static final String VERSION = "1.0";

    public static final String SIGN_VERSION = "1.0";

	public static final String ENCRYPT_VERSION = "1.0";
	/**
	 * 编码
	 */
	public static final String CHARSET = "UTF-8";
	/**
	 * 2.1创建激活会员
	 */
	public static final String Service_create_activate_member = "create_activate_member";
	/**
	 * 2.2设置实名认证
	 */
	public static final String Service_set_real_name = "set_real_name";
	/**
	 * 2.3绑定认证信息
	 */
	public static final String Service_binding_verify = "binding_verify";
	/**
	 * 2.4解绑认证信息
	 */
	public static final String Service_unbinding_verify = "unbinding_verify";
	/**
	 * 2.5查询认证信息
	 */
	public static final String Service_query_verify = "query_verify";
	/**
	 * 2.6绑定银行卡
	 */
	public static final String Service_binding_bank_card = "binding_bank_card";
	/**
	 * 2.7绑定银行卡推进
	 */
	public static final String Service_binding_bank_card_advance = "binding_bank_card_advance";
	/**
	 * 2.8解绑银行卡
	 */
	public static final String Service_unbinding_bank_card = "unbinding_bank_card";
	/**
	 * 2.9 查询银行卡
	 */
	public static final String Service_query_bank_card = "query_bank_card";
	/**
	 * 2.10查询余额/基金份额
	 */
	public static final String Service_query_balance = "query_balance";
	/**
	 * 2.11查询收支明细
	 */
	public static final String Service_query_account_details = "query_account_details";
	/**
	 * 2.12冻结金额
	 */
	public static final String Service_balance_freeze = "balance_freeze";
	/**
	 * 2.13解冻金额
	 */
	public static final String Service_balance_unfreeze = "balance_unfreeze";
	/**
	 * 2.14 请求审核企业会员资质
	 */
	public static final String Service_audit_member_infos = "audit_member_infos";
	/**
	 * 2.15 查询企业会员信息
	 */
	public static final String Service_query_member_infos = "query_member_infos";
	/**
	 * 2.16 查询企业会员审核结果
	 */
	public static final String Service_query_audit_result = "query_audit_result";
	/**
	 * 2.17	sina页面展示用户信息
	 */
	public static final String Service_show_member_infos_sina = "show_member_infos_sina";
	
	/**
	 * 2.18 查询冻结解冻结果
	 */
	public static final String Service_query_ctrl_result = "query_ctrl_result";
	/**
	 * 2.19经办人信息
	 */
	public static final String Service_smt_fund_agent_buy = "smt_fund_agent_buy";

	/*************************** 订单类接口 *******************************/
	/**
	 * 3.1创建托管代收交易
	 */
	public static final String Service_create_hosting_collect_trade = "create_hosting_collect_trade";
	/**
	 * 3.2 创建托管代付交易
	 */
	public static final String Service_create_single_hosting_pay_trade = "create_single_hosting_pay_trade";
	/**
	 * 3.3 创建批量托管代付交易
	 */
	public static final String Service_create_batch_hosting_pay_trade = "create_batch_hosting_pay_trade";
	/**
	 * 3.4 托管交易支付
	 */
	public static final String Service_pay_hosting_trade = "pay_hosting_trade";
	/**
	 * 3.5 支付结果查询
	 */
	public static final String Service_query_pay_result = "query_pay_result";
	/**
	 * 3.6 托管交易查询
	 */
	public static final String Service_query_hosting_trade = "query_hosting_trade";
	/**
	 * 3.7 托管交易批次查询
	 */
	public static final String Service_query_hosting_batch_trade = "query_hosting_batch_trade";
	/**
	 * 3.8 托管退款
	 */
	public static final String Service_create_hosting_refund = "create_hosting_refund";
	/**
	 * 3.9 托管退款查询
	 */
	public static final String Service_query_hosting_refund = "query_hosting_refund";
	/**
	 * 3.10 托管充值
	 */
	public static final String Service_create_hosting_deposit = "create_hosting_deposit";
	/**
	 * 3.11 托管充值查询
	 */
	public static final String Service_query_hosting_deposit = "query_hosting_deposit";
	/**
	 * 3.12 托管提现
	 */
	public static final String Service_create_hosting_withdraw = "create_hosting_withdraw";
	/**
	 * 3.13 托管提现查询
	 */
	public static final String Service_query_hosting_withdraw = "query_hosting_withdraw";
	/**
	 * 3.14 转账接口
	 */

	public static final String Service_create_hosting_transfer = "create_hosting_transfer";
	/**
	 * 3.15 支付推进
	 */
	public static final String Service_advance_hosting_pay = "advance_hosting_pay";
	/**
	 * 3.16 标的录入
	 */
	public static final String Service_create_p2p_hosting_borrowing_target = "create_p2p_hosting_borrowing_target";

	/**
	 * 3.17 创建单笔代付到提现卡交易
	 */
	public static final String Service_create_single_hosting_pay_to_card_trade = "create_single_hosting_pay_to_card_trade";
	/**
	 * 3.18 创建批量代付到提现卡交易
	 */
	public static final String Service_create_batch_hosting_pay_to_card_trade = "create_batch_hosting_pay_to_card_trade";
	/**
	 * 5.1 存钱罐基金收益率查询
	 */
	public static final String Service_query_fund_yield = "query_fund_yield";

	/**************************** 6.1外部业务码 ***********************************/
	/**
	 * 外部业务码 代收投资金 投资业务：代收投资[1001]和代付借款[2001] 还款业务：代收还款[1002]和代付（本金/收益）[2002]
	 */
	public static final String Out_trade_no_1001 = "1001";
	/**
	 * 外部业务码 代付借款金 投资业务：代收投资[1001]和代付借款[2001] 还款业务：代收还款[1002]和代付（本金/收益）[2002]
	 */
	public static final String Out_trade_no_2001 = "2001";
	/**
	 * 外部业务码 代收还款金 投资业务：代收投资[1001]和代付借款[2001] 还款业务：代收还款[1002]和代付（本金/收益）[2002]
	 */
	public static final String Out_trade_no_1002 = "1002";
	/**
	 * 外部业务码 代付（本金/收益）金 投资业务：代收投资[1001]和代付借款[2001]
	 * 还款业务：代收还款[1002]和代付（本金/收益）[2002]
	 */
	public static final String Out_trade_no_2002 = "2002";

	/**************************** 6.2交易状态 ***********************************/
	/**
	 * 交易状态 WAIT_PAY 等待付款(系统不会异步通知)
	 */
	public static final String DealStatus_WAIT_PAY = "WAIT_PAY";
	/**
	 * 交易状态 PAY_FINISHED 已付款(系统会异步通知)
	 */
	public static final String DealStatus_PAY_FINISHED = "PAY_FINISHED";
	/**
	 * 交易状态 TRADE_FAILED 交易失败(系统会异步通知)
	 */
	public static final String DealStatus_TRADE_FAILED = "TRADE_FAILED";
	/**
	 * 交易状态 TRADE_FINISHED 交易结束(系统会异步通知)
	 */
	public static final String DealStatus_TRADE_FINISHED = "TRADE_FINISHED";
	/**
	 * 交易状态 TRADE_CLOSED 交易关闭（合作方通过调用交易取消接口来关闭）(系统会异步通知)
	 */
	public static final String DealStatus_TRADE_CLOSED = "TRADE_CLOSED";
	/**************************** 6.3退款状态 ***********************************/
	/**
	 * 退款状态 WAIT_REFUND 等待退款（处理中）(系统不会异步通知)
	 */
	public static final String RefundStatus_WAIT_REFUND = "WAIT_REFUND";
	/**
	 * 退款状态 PAY_FINISHED 已扣款（处理中）(系统不会异步通知)
	 */
	public static final String RefundStatus_PAY_FINISHED = "PAY_FINISHED";
	/**
	 * 退款状态 SUCCESS 退款成功(系统会异步通知)
	 */
	public static final String RefundStatus_SUCCESS = "SUCCESS";
	/**
	 * 退款状态 FAILED 退款失败(系统会异步通知)
	 */
	public static final String RefundStatus_FAILED = "FAILED";
	/**************************** 6.4支付状态 ***********************************/
	/**
	 * 支付状态 SUCCESS 成功(系统会异步通知)
	 */
	public static final String PayStatus_SUCCESS = "SUCCESS";
	/**
	 * 支付状态 FAILED 失败(系统会异步通知)
	 */
	public static final String PayStatus_FAILED = "FAILED";
	/**
	 * 支付状态 PROCESSING 处理中(系统不会异步通知)
	 */
	public static final String PayStatus_PROCESSING = "PROCESSING";

	/**************************** 6.5充值状态 ***********************************/
	/**
	 * 充值状态 SUCCESS 成功(系统会异步通知)
	 */
	public static final String RechargeStatus_SUCCESS = "SUCCESS";
	/**
	 * 充值状态 FAILED 失败(系统会异步通知)
	 */
	public static final String RechargeStatus_FAILED = "FAILED";
	/**
	 * 充值状态 PROCESSING 处理中(系统不会异步通知)
	 */
	public static final String RechargeStatus_PROCESSING = "PROCESSING";
	/**************************** 6.6提现状态 ***********************************/
	/**
	 * 提现状态 SUCCESS 成功(系统会异步通知)
	 */
	public static final String WithdrawStatus_SUCCESS = "SUCCESS";
	/**
	 * 提现状态 FAILED 失败(系统会异步通知)
	 */
	public static final String WithdrawStatus_FAILED = "FAILED";
	/**
	 * 提现状态 PROCESSING 处理中(系统不会异步通知)
	 */
	public static final String WithdrawStatus_PROCESSING = "PROCESSING";
	/**
	 * 提现状态 RETURNT_TICKET 退票(系统会异步通知)
	 */
	public static final String WithdrawStatus_RETURNT_TICKET = "RETURNT_TICKET";
	/**************************** 6.7批次状态 ***********************************/
	/**
	 * 批次状态 WAIT_PROCESS 待处理(系统不会异步通知)
	 */
	public static final String BatchStatus_WAIT_PROCESS = "WAIT_PROCESS";
	/**
	 * 批次状态 PROCESSING 处理中(系统不会异步通知)
	 */
	public static final String BatchStatus_PROCESSING = "PROCESSING";
	/**
	 * 批次状态 FINISHED 处理结束(系统会异步通知)
	 */
	public static final String BatchStatus_FINISHED = "FINISHED";

	/**************************** 6.9支付方式扩展(异步通知) ***********************************/
	/**
	 * 网银（online_bank）
	 * 余额（balance）
	 * 绑定支付（binding_pay）
	 * 快捷（quick_pay）
	 */
	public static final String Paymethod_balance = "balance";
	public static final String Paymethod_online_bank = "online_bank";
	public static final String Paymethod_binding_pay = "binding_pay";
	public static final String Paymethod_quick_pay = "quick_pay";
	/**************************** 6.10会员类型 ***********************************/
	/**
	 * 会员类型 1 个人会员 2 企业会员
	 */
	public static final String MemberType_Person = "1";
	/**
	 * 会员类型 1 个人会员 2 企业会员
	 */
	public static final int MemberType_Corp = 2;
	/**************************** 6.11证件类型 ***********************************/
	/**证件类型 
	 * IC 身份证
	 */
	public static final String CertType_IC = "IC";
	/**************************** 6.12账户类型 ***********************************/
	/**
	 * 账户类型 
	 * BASIC 基本户 ENSURE 保证金户 RESERVE 准备金 SAVING_POT 存钱罐
	 */
	public static final String AccountType_BASIC = "BASIC";
	/**
	 * 账户类型
	 *  BASIC 基本户 ENSURE 保证金户 RESERVE 准备金 SAVING_POT 存钱罐
	 */
	public static final String AccountType_ENSURE = "ENSURE";
	/**
	 * 账户类型
	 *  BASIC 基本户 ENSURE 保证金户 RESERVE 准备金 SAVING_POT 存钱罐
	 */
	public static final String AccountType_RESERVE = "RESERVE";
	/**
	 * 账户类型
	 *  BASIC 基本户 ENSURE 保证金户 RESERVE 准备金 SAVING_POT 存钱罐
	 */
	public static final String AccountType_SAVING_POT = "SAVING_POT";
	/**************************** 6.13认证类型 ***********************************/
	/**
	 * 认证类型
 	 * MOBILE 手机 EMAIL 邮箱
	 */
	public static final String CertType_MOBILE = "MOBILE";
	/**
	 * 认证类型
 	 * MOBILE 手机 EMAIL 邮箱
	 */
	public static final String CertType_EMAIL = "EMAIL";
	/**************************** 6.14卡类型 ***********************************/

	/**
	 * 卡类型
	 * DEBIT 借记 CREDIT 贷记（信用卡）
	 */
	public static final String CardType_DEBIT = "DEBIT";
	/**
	 * 卡类型
	 * DEBIT 借记 CREDIT 贷记（信用卡）
	 */
	public static final String CardType_CREDIT = "CREDIT";
	/**************************** 6.15卡属性 ***********************************/

	/**
	 * 卡属性
	 * C 对私 B 对公
	 */
	public static final String CardProp_C = "C";
	/**
	 * 卡属性
	 * C 对私 B 对公
	 */
	public static final String CardProp_B = "B";
	/**************************** 6.16卡认证方式 ***********************************/
	public static final String CardSingType_SIGN = "SIGN";
	/**************************** 6.17通知业务类型 ***********************************/
	/**
	 * 通知业务类型 
	 * trade_status_sync 交易结果通知 
	 * refund_status_sync 交易退款结果通知
	 * deposit_status_sync 充值结果通知 
	 * withdraw_status_sync 提现结果通知
	 * batch_trade_status_sync 批量交易结果通知 
	 * audit_status_sync 审核结果通知
	 */
	/**
	 * 通知业务类型 trade_status_sync 交易结果通知
	 */
	public static final String Notifybusstype_trade_status_sync = "trade_status_sync";
	/**
	 * 通知业务类型 refund_status_sync 交易退款结果通知
	 */
	public static final String Notifybusstype_refund_status_sync = "refund_status_sync";
	/**
	 * 通知业务类型 deposit_status_sync 充值结果通知
	 */
	public static final String Notifybusstype_deposit_status_sync = "deposit_status_sync";
	/**
	 * 通知业务类型 withdraw_status_sync 提现结果通知
	 */
	public static final String Notifybusstype_withdraw_status_sync = "withdraw_status_sync";
	/**
	 * 通知业务类型 batch_trade_status_sync 批量交易结果通知
	 */
	public static final String Notifybusstype_batch_trade_status_sync = "batch_trade_status_sync";
	/**
	 * 通知业务类型 audit_status_sync 审核结果通知
	 */
	public static final String Notifybusstype_audit_status_sync = "audit_status_sync";
	/**************************** 6.18通知方式 ***********************************/
	/**
	 * 通知方式 single_notify 逐笔通知 batch_notify 批量通知
	 */
	public static final String Notifymode_single_notify = "single_notify";
	/**
	 * 通知方式 single_notify 逐笔通知 batch_notify 批量通知
	 */
	public static final String Notifymode_batch_notify = "batch_notify";
	/**************************** 6.19标识类型 ***********************************/
	/**
	 * UID 商户用户ID
	 * MOBILE 钱包绑定手机号
	 * EMAIL 钱包绑定邮箱
	 * MEMBER_ID 用户在SINA支付的会员编号
	 */
	public static final String Identity_type_uid = "UID";
	/**
	 * UID 商户用户ID
	 * MOBILE 钱包绑定手机号
	 * EMAIL 钱包绑定邮箱
	 * MEMBER_ID 用户在SINA支付的会员编号
	 */
	public static final String Identity_type_mobile = "MOBILE";
	/**
	 * UID 商户用户ID
	 * MOBILE 钱包绑定手机号
	 * EMAIL 钱包绑定邮箱
	 * MEMBER_ID 用户在SINA支付的会员编号
	 */
	public static final String Identity_type_email = "EMAIL";
	/**
	 * UID 商户用户ID
	 * MOBILE 钱包绑定手机号
	 * EMAIL 钱包绑定邮箱
	 * MEMBER_ID 用户在SINA支付的会员编号
	 */
	public static final String Identity_type_memberid = "MEMBER_ID";
	/**************************** 6.20存钱罐交易类型 ***********************************/
	/**
	 * 存钱罐交易类型
	 * IN 申购
	 */
	public static final String Savingport_type_in="IN";
	/**
	 * 存钱罐交易类型
	 * OUT 赎回
	 */
	public static final String Savingport_type_out="OUT";
	/**
	 * 存钱罐交易类型
	 * BONUS 收益
	 */
	public static final String Savingport_type_bonus="BONUS";
	/**************************** 6.23审核状态 ***********************************/
	/**
	 * 审核状态
	 * 
	 * SUCCESS 审核成功(系统会异步通知) FAILED 审核失败(系统会异步通知) PROCESSING 审核中(系统不会异步通知)
	 */
	public static final String VerifyStatus_SUCCESS = "SUCCESS";
	/**
	 * 审核状态
	 * 
	 * SUCCESS 审核成功(系统会异步通知) FAILED 审核失败(系统会异步通知) PROCESSING 审核中(系统不会异步通知)
	 */
	public static final String VerifyStatus_FAILED = "FAILED";
	/**
	 * 审核状态
	 * 
	 * SUCCESS 审核成功(系统会异步通知) FAILED 审核失败(系统会异步通知) PROCESSING 审核中(系统不会异步通知)
	 */
	public static final String VerifyStatus_PROCESSING = "PROCESSING";
	/**************************** 6.25响应码 ***********************************/ 

	/**
	 * 
	 */
	public static final String CMDID_NET_SAVE = "NetSave"; // 网银充值
	public static final String CMDID_DEPOSIT = "Deposit"; // 快捷充值
	public static final String CMDID_CASH = "Cash"; // 取现
	public static final String CMDID_INITITATIETENDER = "InititatieTender"; // 网银充值
	public static final String CMDID_LOANS= "Loans"; //自动扣款（放款）,后台数据流方式
	
	
	/**
	 * 投资请求的支付方式
	 * 
	 */
	public static final String  SINA_PAY_METHOD = "DEBT_MATURITY";
	
	/**
	 * t_requesthistory 判断插入时是收款还是还款
	 * 
	 */
	public static final int SERVICETYPE_INITITATIETENDER = 0; // InititatieTender
	public static final int SERVICETYPE_LOANS = 1; // Loans
	public static final String BALANCE = "Balance"; //支付方式
	
	
	/**
	 * 仓单宝
	 * 
	 */
	public static final int CURRENT_SERVICE_INVEST = 1;  //投资
	public static final int CURRENT_SERVICE_RANSOM = 2;  //赎回
	
}
