package com.github.startup.sina;

import baojinsuo.bean.GenericResult;
import baojinsuo.generator.IDGenerator;
import baojinsuo.serviceImpl.*;
import baojinsuo.sina.utils.SinaConsts;
import baojinsuo.sms.SMSHelper;
import baojinsuo.systemconfig.Consts;
import baojinsuo.systemconfig.SystemProperties;
import baojinsuo.utils.SafeUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class SinaRspTreatHelper {

	private Logger log = Logger.getLogger(SinaRspTreatHelper.class);
	protected ScoreServiceImpl scoreService = new ScoreServiceImpl();
	protected UserServiceImpl userService = new UserServiceImpl();
	protected CurrentServiceImpl currentService = new CurrentServiceImpl();
	protected ProductServiceImpl productService = new ProductServiceImpl();

	public SinaRspTreatResult treatResponse(boolean isFromBG, String from, String charset, String response) {
		Map tmpmap = new Hashtable();
		SinaRspTreatResult tmpresult = null;
		try {
			JSONObject jobj = new JSONObject(response);
			Iterator tmpiterator = jobj.keys();
			String tmpkey;
			while (tmpiterator.hasNext()) {
				tmpkey = SafeUtils.getString(tmpiterator.next());
				tmpmap.put(tmpkey, jobj.get(tmpkey));
			}
			tmpresult = treatResponse(isFromBG, from, charset, tmpmap);
		} catch (Exception ex) {

		}
		return tmpresult;

	}

	public SinaRspTreatResult treatCurrentPayResponse(boolean isFromBG, String from, String charset, String response) {
		Map tmpmap = new Hashtable();
		SinaRspTreatResult tmpresult = null;
		try {
			JSONObject jobj = new JSONObject(response);
			Iterator tmpiterator = jobj.keys();
			String tmpkey;
			while (tmpiterator.hasNext()) {
				tmpkey = SafeUtils.getString(tmpiterator.next());
				tmpmap.put(tmpkey, jobj.get(tmpkey));
			}
			tmpresult = treatCurrentPayResponse(isFromBG, from, charset, tmpmap);
		} catch (Exception ex) {

		}
		return tmpresult;

	}

	public SinaRspTreatResult treatCurrentRansomResponse(boolean isFromBG, String from, String charset,
			String response) {
		Map tmpmap = new Hashtable();
		SinaRspTreatResult tmpresult = null;
		try {
			JSONObject jobj = new JSONObject(response);
			Iterator tmpiterator = jobj.keys();
			String tmpkey;
			while (tmpiterator.hasNext()) {
				tmpkey = SafeUtils.getString(tmpiterator.next());
				tmpmap.put(tmpkey, jobj.get(tmpkey));
			}
			tmpresult = treatCurrentPayResponse(isFromBG, from, charset, tmpmap);
		} catch (Exception ex) {

		}
		return tmpresult;

	}

	/**
	 * 同步账户信息，主要为余额，可用额度和冻结金额，保持跟支付平台一致性
	 * 
	 * @param custType
	 *            本平台账户类型
	 * @param vipCode
	 *            VIP号
	 * @return
	 */
	public void getSyncAccountInfo(String custType, String vipCode) {
		syncAccountInfo(custType, vipCode);
	}

	/**
	 * 同步账户信息，主要为余额，可用额度和冻结金额，保持跟支付平台一致性
	 * 
	 * @param custType
	 *            本平台账户类型
	 * @param vipCode
	 *            VIP号
	 * @return
	 */
	private GenericResult syncAccountInfo(String custType, String vipCode) {
		GenericResult tmpresult = new GenericResult();
		SinaHelper tmphelper = new SinaHelper();
		SinaRspResult tmprsp = tmphelper.query_balance(SinaConsts.Identity_type_uid, vipCode,
				SinaConsts.AccountType_SAVING_POT, "");
		if (tmprsp.getResultCode().equals(SinaResCodeEnum.APPLY_SUCCESS.getValue())) {// 查询余额请求成功
			try {
				BigDecimal tmpAcctBal = SafeUtils.getBigDecimal(tmprsp.getResultMap().get("balance"));
				BigDecimal tmpAvlBal = SafeUtils.getBigDecimal(tmprsp.getResultMap().get("available_balance"));
				BigDecimal tmpFrzBal = tmpAcctBal.subtract(tmpAvlBal);
				String tmpBonus = tmprsp.getResultMap().get("bonus");
				BigDecimal tmpSinaDay = new BigDecimal(0);
				BigDecimal tmpSinaMonth = new BigDecimal(0);
				BigDecimal tmpSinaTotal = new BigDecimal(0);
				String[] s = tmpBonus.split("\\^");
				if (s.length == 3) {
					tmpSinaDay = SafeUtils.getBigDecimal(s[0]);// 昨日存钱罐收益
					tmpSinaMonth = SafeUtils.getBigDecimal(s[1]);// 本月存钱罐收益
					tmpSinaTotal = SafeUtils.getBigDecimal(s[2]);// 存钱罐总收益
				}
				GenericResult tmpupdateResult = userService.updateBalanceByFundAcct(custType, vipCode, tmpAcctBal,
						tmpAvlBal, tmpFrzBal, tmpSinaDay, tmpSinaMonth, tmpSinaTotal);

			} catch (Exception ex) {
				log.error("同步新浪存钱罐账户余额-失败-异常:" + ex.getMessage());
			}

		} else {
			log.error("同步新浪存钱罐账户余额-失败-返回码:" + tmprsp.getResultCode() + "返回信息描述:" + tmprsp.getResultDesc());
		}
		return tmpresult;

	}

	// 用户投资 ------新浪异步返回处理
	public SinaRspTreatResult treatResponse(boolean isFromBG, String from, String charset, Map paramMap) {
		SinaRspTreatResult tmpresult = new SinaRspTreatResult();

		String tmpUsrCustId = "";
		String tmpFreezeTrxId = "";
		String finProductName = "";
		String finSellerCode = "";
		String tmpTrxId = SafeUtils.getRequestValue(paramMap.get("inner_trade_no"));
		String tmpOrdId = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));
		String tmpStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpNotifyId = SafeUtils.getRequestValue(paramMap.get("notify_id"));
		String tmpNotifyTime = SafeUtils.getRequestValue(paramMap.get("notify_time"));
		int tmpNotifyTimeInt = SafeUtils.getUnixTimeFromString(tmpNotifyTime, "yyyyMMddHHmmss");
		String tmpNotifyType = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String tmpMerPriv = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));// 交易订单号
		String tmpResMessage = SafeUtils.getRequestValue(paramMap.get("response_message"));
		String tmpService = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String transAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
		String tmpRespCharset = SafeUtils.getRequestValue(paramMap.get("_input_charset"));
		String tmpCollectionStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpPayMethod = "middle_account_transfer^" + transAmt;
		log.info("Result From Sinapay:" + tmpNotifyType);

		JSONObject tmpobj = new JSONObject();

		try {
			for (Object key : paramMap.keySet()) {
				String tmpvalue = SafeUtils.getRequestValue(paramMap.get(key));
				tmpobj.put(key.toString(), tmpvalue);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		if (tmpNotifyType.equals(SinaConsts.Notifybusstype_withdraw_status_sync)
				|| tmpNotifyType.equals(SinaConsts.Notifybusstype_deposit_status_sync)) {
			GlobalServiceImpl tmpUserService = new GlobalServiceImpl();
			tmpUserService.saveSinaResponse(from, tmpobj.toString(), charset, SafeUtils.getCurrentUnixTime());
			return null;
		}
		if (tmpNotifyType.equals(SinaConsts.Notifybusstype_trade_status_sync)) {// 投资

			int finCustomerID = 0;
			int tmpFinanticalStatus = 0;
			int tmpCustomerInvestStatus = 0;
			int tmpSinaTransStatus = 0;
			int tmpTenderStatus = 0;
			String tmpTradeStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
			String tmpTradeAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
			BigDecimal tmpTransAmt = SafeUtils.getBigDecimal(transAmt);
			String tmptransactioNo = IDGenerator.generateRequestNo();
			GenericResult productMsg = userService.getSellerCode(tmpOrdId, tmpTrxId, tmptransactioNo);
			String tmpRespCodes = SafeUtils.getRequestValue(productMsg.getData().get("RespCode"));
			if (tmpTradeStatus.equals(SinaConsts.DealStatus_TRADE_FINISHED)) {
				log.info("==========进入交易，投资异步响应成功==========:" + tmpTradeStatus);
				SinaHelper tmphelper = new SinaHelper();
				UserServiceImpl userService = new UserServiceImpl();
				String tmpProductCode = SafeUtils.getRequestValue(productMsg.getData().get("ProductCode"));
				String tmpSellerCode = SafeUtils.getRequestValue(productMsg.getData().get("SellerCode"));
				String tmpCustomerCode = SafeUtils.getRequestValue(productMsg.getData().get("CustomerCode"));
				String tmpCustomerId = SafeUtils.getRequestValue(productMsg.getData().get("CustomerId"));
				String tmpCmdVersion = SinaConsts.VERSION_1;
				tmpFinanticalStatus = 2;
				tmpCustomerInvestStatus = 0;
				tmpSinaTransStatus = 1;
				tmpTenderStatus = 2;
				if (tmpRespCodes.equals("1")) {// 用户投资第一步异步
					log.info("==1.用户投资，中间账户收款==");

					log.info("用户投资，新浪第一步异步成功返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCollectionStatus);
					log.info("用户投资，新浪第一步异步成功返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户投资，新浪第一步异步成功返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					userService.saveSinaCollectionAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpCustomerInvestStatus, tmpTrxId,
							tmpSinaTransStatus, tmpTenderStatus);
					log.info(
							"用户投资，新浪第一步异步成功返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");

					log.info("用户投资，中间账户转账至保理商同步操作前,requestHistory插入的存储过程----P_SaveSinaRequest----front");
					userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
							SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
							tmpCmdVersion, "", "", Consts.CUSTTYPE_SELLER, tmpCustomerId, 0, 0, 0, 0, tmpTransAmt,
							new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0), tmpSellerCode,
							SinaConsts.AccountType_BASIC, tmpPayMethod, "", 0, Consts.OPERATETYPE_INSERT);
					log.info("用户投资，中间账户转账至保理商同步操作前,requestHistory插入的存储过程----P_SaveSinaRequest----behind");

					log.info("用户投资，中间账户转账至保理商同步操作前，表t_financialrecord插入资金记录的存储过程----front");
					userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
							Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
							tmptransactioNo, "", Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
							SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
							"保理商收款| 产品[" + finProductName + "]投标", null, finSellerCode, "", "", "", "", "", tmpTransAmt,
							new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD_FINANCIALTYPE_COLLECTION, 0,
							Consts.OPERATETYPE_INSERT);
					log.info("用户投资，中间账户转账至保理商同步操作前，表t_financialrecord插入资金记录的存储过程----behind");

					log.info("用户投资，中间账户转账至保理商同步操作前，表 t_customer_invest更新数据的存储过程----front");
					userService.saveCustomerInvest(SafeUtils.getInt(tmpCustomerId), 0, 0,
							SafeUtils.getCurrentUnixTime(), tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0),
							new BigDecimal(0.0), Consts.INVESTTYPE_CUSTOMER, "", tmpOrdId, tmpOrdId,
							SafeUtils.getCurrentTimeStr("yyyyMMdd"), 2, "", "", "", "", tmpTrxId, 0, "", -1,
							new BigDecimal(0.0), new BigDecimal(0.0), 0, "", 0, -1, -1, -1, "", 0, "", "", "", "", 1,
							tmptransactioNo, Consts.OPERATETYPE_UPDATE);
					log.info("用户投资，中间账户转账至保理商同步操作前，表 t_customer_invest更新数据的存储过程----behind");

					SinaRspResult result = new SinaRspResult();
					tmphelper.setSinapay_callbackurl(SystemProperties.get("sinapay_callbackurl"));
					result = tmphelper.create_single_hosting_pay_trade(tmptransactioNo, SinaConsts.Out_trade_no_2001,
							tmpCustomerCode, SinaConsts.Identity_type_uid, SinaConsts.AccountType_BASIC,
							new BigDecimal(tmpTradeAmt),
							tmpCustomerCode + "^" + SinaConsts.Identity_type_uid + "^^" + tmpSellerCode + "^"
									+ SinaConsts.Identity_type_uid + "^" + SinaConsts.AccountType_BASIC + "^"
									+ tmpTradeAmt + "^支付",
							"投资入账", "", tmpProductCode);

					if (result.getResultCode().equals("APPLY_SUCCESS")) {
						log.info("用户投资，中间账户转账至保理商同步成功，表 t_customer_invest更新数据的存储过程----front");
						GenericResult productMsg4 = userService.saveCustomerInvest(SafeUtils.getInt(tmpCustomerId), 0,
								0, SafeUtils.getCurrentUnixTime(), tmpTransAmt, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), Consts.INVESTTYPE_CUSTOMER, "", tmpOrdId,
								tmpOrdId, SafeUtils.getCurrentTimeStr("yyyyMMdd"), 8, "", "", "", "", tmpTrxId, 0, "",
								-1, new BigDecimal(0.0), new BigDecimal(0.0), 0, "", 0, -1, -1, -1, "", 0, "", "", "",
								"", 1, tmptransactioNo, Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步成功，表 t_customer_invest更新数据的存储过程----behind");

						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("用户投资，中间账户转账至保理商同步成功,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_CUSTOMER, tmpCustomerId,
								0, 0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步成功,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("用户投资，中间账户转账至保理商同步成功，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
								Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
								result.getResultMap().get("out_trade_no"), result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"保理商收款| 产品[" + finProductName + "]投标", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0,
								Consts.FINANCIALRECORD_FINANCIALTYPE_COLLECTION, 1, Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步成功，表t_financialrecord更新资金记录的存储过程----behind");

					} else {

						log.info("用户投资，中间账户转账至保理商同步失败，表 t_customer_invest更新数据的存储过程----front");
						GenericResult productMsg4 = userService.saveCustomerInvest(SafeUtils.getInt(tmpCustomerId), 0,
								0, SafeUtils.getCurrentUnixTime(), tmpTransAmt, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), Consts.INVESTTYPE_CUSTOMER, "", tmpOrdId,
								tmpOrdId, SafeUtils.getCurrentTimeStr("yyyyMMdd"), 9, "", "", "", "", tmpTrxId, 0, "",
								-1, new BigDecimal(0.0), new BigDecimal(0.0), 0, "", 0, -1, -1, -1, "", 3, "", "", "",
								"", 4, tmptransactioNo, Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步失败，表 t_customer_invest更新数据的存储过程----behind");

						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("用户投资，中间账户转账至保理商同步失败,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_CUSTOMER, tmpCustomerId,
								0, 0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步失败,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("用户投资，中间账户转账至保理商同步失败，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
								Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
								result.getResultMap().get("out_trade_no"), result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"保理商收款| 产品[" + finProductName + "]投标", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0,
								Consts.FINANCIALRECORD_FINANCIALTYPE_COLLECTION, 3, Consts.OPERATETYPE_UPDATE);
						log.info("用户投资，中间账户转账至保理商同步失败，表t_financialrecord更新资金记录的存储过程----behind");
					}

				} else if (tmpRespCodes.equals("2")) {// 用户投资
					log.info("==2.用户投资，中间账户放款==");

					log.info("用户投资，新浪第二步异步成功返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCollectionStatus);
					log.info("用户投资，新浪第二步异步成功返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户投资，新浪第二步异步成功返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					GenericResult productMsg2 = userService.saveSinaSecCollectionAsynchronousUpdate(tmpOrdId,
							tmpNotifyId, tmpStatus, tmpTrxId, tmpNotifyTime, tmpNotifyTimeInt,
							tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(), tmpStatus, tmpResMessage, 2, 3, 3,
							tmpNotifyTimeInt, 0, "", 1, tmpTrxId, 2);
					log.info(
							"用户投资，新浪第二步异步成功返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");

					if (productMsg2.getResult() == 0) {
						Map tmpinvestinfo = this.userService.getInvestDetailByTenderTransOrder(tmpOrdId);

						StringBuilder tmpsb = new StringBuilder();
						tmpsb.append("您成功购买了")
								.append(SafeUtils
										.formatCurrency(SafeUtils.getBigDecimal(tmpinvestinfo.get("InvestMoney")), 2))
								.append("元").append(tmpinvestinfo.get("ProductName")).append("，年化利率")
								.append(SafeUtils
										.formatCurrency(SafeUtils.getBigDecimal(tmpinvestinfo.get("AnnualRate")), 2))
								.append("%，理财周期").append(tmpinvestinfo.get("Cycle")).append("天,到期日")
								.append(SafeUtils.getFormatDateTimeFromUnixTime(
										SafeUtils.getInt(tmpinvestinfo.get("DueDate")), "yyyy年MM月dd日"))
								.append("。");

						SMSHelper tmpsmshelper = new SMSHelper();
						// 下面的内容发送应该交由消息队列发送，这个暂时用此方法
						log.info("SendSMS:" + tmpsb.toString());
						tmpsmshelper.sendByHttp(SafeUtils.getString(tmpinvestinfo.get("Mobile")), tmpsb.toString(),
								"" + SafeUtils.getCurrentUnixTime());

					}
					log.info("SinaRespDesc==" + productMsg2.getResult() + productMsg2.getResultDesc());
				} else if (tmpRespCodes.equals("3")) {// 销售商还款收款至中间账户
					log.info("==3.销售商投资，中间账户收款");

					log.info("销售商投资成功付款后，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					userService.saveSinaCollectionAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, 2, 0, tmpTrxId, 1, 2);
					log.info("销售商投资成功付款后，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");

				} else if (tmpRespCodes.equals("4")) {// 中间账户还款放款至用户手中
					log.info("==4.销售商投资,中间账户放款");
					log.info("中间账户还款至用户，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					GenericResult productMsg2 = userService.saveSinaAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus,
							tmpTrxId, tmpNotifyTime, tmpNotifyTimeInt,
							tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(), tmpStatus, tmpResMessage, 2);
					log.info("中间账户还款至用户，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");
				}
				// 发起放款
			} else if (tmpTradeStatus.equals(SinaConsts.DealStatus_PAY_FINISHED)) {
				log.info("==========进入交易，投资异步响应忽略==========:" + tmpTradeStatus);
			} else {
				if (tmpRespCodes.equals("1")) {
					tmpFinanticalStatus = 3;
					tmpCustomerInvestStatus = 3;
					tmpSinaTransStatus = 3;
					tmpTenderStatus = 7;
					log.info("==========进入交易，投资第一步异步响应失败==========:" + tmpTradeStatus);
					log.info("用户投资，新浪第一步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCollectionStatus);
					log.info("用户投资，新浪第一步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户投资，新浪第一步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					userService.saveSinaCollectionAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpCustomerInvestStatus, tmpTrxId,
							tmpSinaTransStatus, tmpTenderStatus);
					log.info(
							"用户投资，新浪第一步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");
				} else if (tmpRespCodes.equals("2")) {
					tmpFinanticalStatus = 3;
					tmpCustomerInvestStatus = 3;
					tmpSinaTransStatus = 4;
					tmpTenderStatus = 4;
					log.info("==========进入交易，投资第二步异步响应失败==========:" + tmpTradeStatus);
					log.info("用户投资，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCollectionStatus);
					log.info("用户投资，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户投资，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					userService.saveSinaSecCollectionAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpTenderStatus, 3, tmpNotifyTimeInt, 0, "",
							tmpCustomerInvestStatus, tmpTrxId, tmpSinaTransStatus);
					log.info(
							"用户投资，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");
				}
			}
		}
		tmpresult.setTrxId(tmpMerPriv);
		return tmpresult;

	}

	// 仓单宝购买------新浪异步返回处理
	public SinaRspTreatResult treatCurrentPayResponse(boolean isFromBG, String from, String charset, Map paramMap) {
		SinaRspTreatResult tmpresult = new SinaRspTreatResult();
		int pid = SafeUtils.getInt(SystemProperties.get("huoqiid"));
		String tmpFreezeTrxId = "";
		int finCustomerID = 0;
		String finProductName = "";
		String tmpUsrCustId = "";
		String finSellerCode = "";
		String tmpTrxId = SafeUtils.getRequestValue(paramMap.get("inner_trade_no"));
		String tmpOrdId = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));
		String tmpStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpNotifyId = SafeUtils.getRequestValue(paramMap.get("notify_id"));
		String tmpNotifyTime = SafeUtils.getRequestValue(paramMap.get("notify_time"));
		int tmpNotifyTimeInt = SafeUtils.getUnixTimeFromString(tmpNotifyTime, "yyyyMMddHHmmss");
		String tmpNotifyType = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String tmpMerPriv = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));// 交易订单号
		String tmpRespCode = SafeUtils.getRequestValue(paramMap.get("trade_status_sync"));
		String tmpResMessage = SafeUtils.getRequestValue(paramMap.get("response_message"));
		String tmpService = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String transAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
		String tmpRespCharset = SafeUtils.getRequestValue(paramMap.get("_input_charset"));
		String tmpCurrentPayStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpPayMethod = "middle_account_transfer^" + transAmt;
		log.info("Result From Sinapay:" + tmpNotifyType);

		JSONObject tmpobj = new JSONObject();
		try {
			for (Object key : paramMap.keySet()) {
				String tmpvalue = SafeUtils.getRequestValue(paramMap.get(key));
				tmpobj.put(key.toString(), tmpvalue);
			}

			GlobalServiceImpl tmpUserService = new GlobalServiceImpl();
			log.info("----SinaRspTreatHelper----front");
			tmpUserService.saveSinaResponse(from, tmpobj.toString(), charset, SafeUtils.getCurrentUnixTime());
			log.info("----SinaRspTreatHelper----behind");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		if (tmpNotifyType.equals(SinaConsts.Notifybusstype_trade_status_sync)) {// 活期投资

			int tmpFinanticalStatus = 0;
			int tmpCurrentInvestStatus = 0;
			int tmpSinaTransStatus = 0;
			int tmpTenderStatus = 0;
			log.info("==========进入活期投资交易，用户扣款，中间商收款响应==========");
			String tmpTradeStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
			String tmpTradeAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
			BigDecimal tmpTransAmt = SafeUtils.getBigDecimal(transAmt);

			String tmptransactioNo = IDGenerator.generateRequestNo();
			GenericResult productMsg = userService.getCurrentSellerCode(tmpOrdId, tmpTrxId, tmptransactioNo);
			String tmpRespCodes = SafeUtils.getRequestValue(productMsg.getData().get("RespCode"));
			if (tmpTradeStatus.equals(SinaConsts.DealStatus_TRADE_FINISHED)) {
				SinaHelper tmphelper = new SinaHelper();
				UserServiceImpl userService = new UserServiceImpl();
				log.info("tmptransactioNo====================" + tmptransactioNo);
				log.info("tmpOrdId====================" + tmpOrdId);
				log.info("tmpTrxId====================" + tmpTrxId);

				String tmpCustomerId = SafeUtils.getRequestValue(productMsg.getData().get("CustomerId"));
				String tmpSellerCode = SafeUtils.getRequestValue(productMsg.getData().get("SellerCode"));
				String tmpCustomerCode = SafeUtils.getRequestValue(productMsg.getData().get("CustomerCode"));
				String tmpProductCode = SafeUtils.getRequestValue(productMsg.getData().get("ProductCode"));
				String tmpCmdVersion = SinaConsts.VERSION_1;
				log.info("tmpRespCodes====================" + tmpRespCodes);
				if (tmpRespCodes.equals("1")) {// 用户活期投资，中间商收款

					tmpFinanticalStatus = 2;
					tmpCurrentInvestStatus = 0;
					tmpSinaTransStatus = 1;
					tmpTenderStatus = 2;
					log.info("仓单宝用户投资付款，第一步返回异步成功的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentPayStatus);
					log.info("仓单宝用户投资付款，第一步返回异步成功的存储过程----P_SaveSinaAsynchronousResponse----behind");

					log.info(
							"仓单宝用户投资付款，第一步返回异步成功，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					userService.saveSinaCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpCurrentInvestStatus, tmpTrxId,
							tmpSinaTransStatus, tmpTenderStatus);
					log.info(
							"仓单宝用户投资付款，第一步返回异步成功，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");

					log.info("仓单宝投资，中间账户转账至保理商之前,requestHistory插入的存储过程----P_SaveSinaRequest----front");
					userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
							SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
							tmpCmdVersion, "", "", Consts.CUSTTYPE_SELLER, tmpCustomerId, 0, 0, 0, 0, tmpTransAmt,
							new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0), tmpSellerCode,
							SinaConsts.AccountType_BASIC, tmpPayMethod, "", 0, Consts.OPERATETYPE_INSERT);
					log.info("仓单宝投资，中间账户转账至保理商之前,requestHistory插入的存储过程----P_SaveSinaRequest----behind");

					log.info("仓单宝投资，中间账户转账至保理商之前，表t_financialrecord插入资金记录的存储过程----front");
					userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
							Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
							tmptransactioNo, "", Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
							SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
							"保理商收款| 活期产品[" + finProductName + "]购买", null, finSellerCode, "", "", "", "", "",
							tmpTransAmt, new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD__PAY, 0,
							Consts.OPERATETYPE_INSERT);
					log.info("仓单宝投资，中间账户转账至保理商之前，表t_financialrecord插入资金记录的存储过程----behind");

					log.info("仓单宝投资，中间账户转账至保理商之前，表 t_current_invest更新数据的存储过程----front");
					currentService.saveCurrentInvest(0, 0, 0, 0, new BigDecimal(0.0), new BigDecimal(0.0),
							new BigDecimal(0.0), 0, 0, "", 1, tmpOrdId, tmptransactioNo, "", 0, 2,
							Consts.OPERATETYPE_UPDATE);
					log.info("仓单宝投资，中间账户转账至保理商之前，表 t_current_invest更新数据的存储过程----behind");

					SinaRspResult result = new SinaRspResult();
					tmphelper.setSinapay_callbackurl(SystemProperties.get("sinapay_callbackurl_current_pay"));
					result = tmphelper.create_single_hosting_pay_trade(tmptransactioNo, SinaConsts.Out_trade_no_2001,
							tmpCustomerCode, SinaConsts.Identity_type_uid, SinaConsts.AccountType_BASIC,
							new BigDecimal(tmpTradeAmt),
							tmpCustomerCode + "^UID^^" + tmpSellerCode + "^UID^BASIC^" + tmpTradeAmt + "^支付", "投资入账",
							"", tmpProductCode);

					if (result.getResultCode().equals("APPLY_SUCCESS")) {
						log.info("仓单宝投资，中间账户转账至保理商同步成功，表 t_current_invest更新数据的存储过程----front");
						GenericResult productMsg4 = currentService.saveCurrentInvest(0, 0, 0, 0, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), 0, 0, "", 1, tmpOrdId, tmptransactioNo, "", 0,
								8, Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步成功，表 t_current_invest更新数据的存储过程----behind");
						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("仓单宝投资，中间账户转账至保理商同步成功,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_SELLER, tmpCustomerId, 0,
								0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步成功,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("仓单宝投资，中间账户转账至保理商同步成功，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
								Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
								result.getResultMap().get("out_trade_no"), result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"保理商收款| 活期产品[" + finProductName + "]购买", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD__PAY, 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步成功，表t_financialrecord更新资金记录的存储过程----behind");
					} else {
						log.info("仓单宝投资，中间账户转账至保理商同步失败，表 t_current_invest更新数据的存储过程----front");
						GenericResult productMsg4 = currentService.saveCurrentInvest(0, 0, 0, 0, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), 0, 0, "", 3, tmpOrdId, tmptransactioNo, "", 3,
								9, Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步失败，表 t_current_invest更新数据的存储过程----behind");
						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("仓单宝投资，中间账户转账至保理商同步失败,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_SELLER, tmpCustomerId, 0,
								0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步失败,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("仓单宝投资，中间账户转账至保理商同步失败，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_SELLER, finCustomerID,
								Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt, Consts.FUNDORGID_SINA,
								result.getResultMap().get("out_trade_no"), result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"保理商收款| 活期产品[" + finProductName + "]购买", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD__PAY, 3,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝投资，中间账户转账至保理商同步失败，表t_financialrecord更新资金记录的存储过程----behind");
					}

				} else if (tmpRespCodes.equals("2")) {// 用户活期投资，中间商放款
					log.info("2===============用户投资，中间商放款");
					log.info("仓单宝用户投资，中间商放款，第二步返回异步成功的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentPayStatus);
					log.info("仓单宝用户投资，中间商放款，第二步返回异步成功的存储过程----P_SaveSinaAsynchronousResponse----front");

					log.info(
							"仓单宝用户投资，中间商放款，第二步返回异步成功，异步更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					GenericResult productMsg2 = currentService.saveSinaSecCurrentAsynchronousUpdate(tmpOrdId,
							tmpNotifyId, tmpStatus, tmpTrxId, tmpNotifyTime, tmpNotifyTimeInt,
							tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(), tmpStatus, tmpResMessage, 2,
							Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")), 2, tmpTrxId, 1, 3);
					log.info(
							"仓单宝用户投资，中间商放款，第二步返回异步成功，异步更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");

					if (productMsg2.getResult() == 0) {
						Map tmpinvestinfo = this.currentService.getCurrentInvestDetailByTransOuterOrder(tmpOrdId);

						StringBuilder tmpsb = new StringBuilder();
						tmpsb.append("您成功购买了")
								.append(SafeUtils
										.formatCurrency(SafeUtils.getBigDecimal(tmpinvestinfo.get("TotalMoney")), 2))
								.append("元的仓单宝理财产品。");

						SMSHelper tmpsmshelper = new SMSHelper();
						// 下面的内容发送应该交由消息队列发送，这个暂时用此方法
						log.info("SendSMS:" + tmpsb.toString());
						tmpsmshelper.sendByHttp(SafeUtils.getString(tmpinvestinfo.get("Mobile")), tmpsb.toString(),
								"" + SafeUtils.getCurrentUnixTime());
					}
				}
			} else if (tmpTradeStatus.equals(SinaConsts.DealStatus_PAY_FINISHED)) {
				log.info("==========进入交易，活期投资异步响应忽略==========:" + tmpTradeStatus);
			} else {

				if (tmpRespCodes.equals("1")) {
					// 解冻活期父产品的可投资金额
					BigDecimal tmpAmount = SafeUtils.getBigDecimal(transAmt);
					productService.unFreezeAmount(pid, tmpAmount);

					tmpFinanticalStatus = 3;
					tmpCurrentInvestStatus = 3;
					tmpSinaTransStatus = 3;
					tmpTenderStatus = 7;
					// 改变t_requestHistory、t_current_invest、t_financialrecord的状态，由处理中改为处理失败
					log.info("仓单宝用户投资付款，失败后返回第一步异步的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentPayStatus);
					log.info("仓单宝用户投资付款，失败后返回第一步异步的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"仓单宝用户投资付款，失败后返回第一步异步，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					userService.saveSinaCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpCurrentInvestStatus, tmpTrxId,
							tmpSinaTransStatus, tmpTenderStatus);
					log.info(
							"仓单宝用户投资付款，失败后返回第一步异步，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");
				} else if (tmpRespCodes.equals("2")) {
					tmpFinanticalStatus = 3;
					tmpCurrentInvestStatus = 3;
					tmpSinaTransStatus = 4;
					tmpTenderStatus = 4;
					log.info("==========进入交易，活期投资第二步异步响应失败==========:" + tmpTradeStatus);
					log.info("用户活期投资，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentPayStatus);
					log.info("用户活期投资，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户活期投资，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					currentService.saveSinaSecCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus,
							Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")), tmpSinaTransStatus, tmpTrxId,
							tmpCurrentInvestStatus, tmpTenderStatus);
					log.info(
							"用户活期投资，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");
				}
			}

		}
		tmpresult.setTrxId(tmpMerPriv);
		return tmpresult;
	}

	// 仓单宝赎回------新浪异步返回处理
	public SinaRspTreatResult treatCurrentRansomResponse(boolean isFromBG, String from, String charset, Map paramMap) {
		SinaRspTreatResult tmpresult = new SinaRspTreatResult();
		int pid = SafeUtils.getInt(SystemProperties.get("huoqiid"));
		String tmpFreezeTrxId = "";
		int finCustomerID = 0;
		String finProductName = "";
		String tmpUsrCustId = "";
		String finSellerCode = "";
		String tmpTrxId = SafeUtils.getRequestValue(paramMap.get("inner_trade_no"));
		String tmpOrdId = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));
		String tmpStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpNotifyId = SafeUtils.getRequestValue(paramMap.get("notify_id"));
		String tmpNotifyTime = SafeUtils.getRequestValue(paramMap.get("notify_time"));
		int tmpNotifyTimeInt = SafeUtils.getUnixTimeFromString(tmpNotifyTime, "yyyyMMddHHmmss");
		String tmpNotifyType = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String tmpMerPriv = SafeUtils.getRequestValue(paramMap.get("outer_trade_no"));// 交易订单号
		String tmpRespCode = SafeUtils.getRequestValue(paramMap.get("trade_status_sync"));
		String tmpResMessage = SafeUtils.getRequestValue(paramMap.get("response_message"));
		String tmpService = SafeUtils.getRequestValue(paramMap.get("notify_type"));
		String transAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
		String tmpRespCharset = SafeUtils.getRequestValue(paramMap.get("_input_charset"));
		String tmpCurrentRansomStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
		String tmpPayMethod = "middle_account_transfer^" + transAmt;
		log.info("Result From Sinapay:" + tmpNotifyType);

		JSONObject tmpobj = new JSONObject();

		try {
			for (Object key : paramMap.keySet()) {
				String tmpvalue = SafeUtils.getRequestValue(paramMap.get(key));
				tmpobj.put(key.toString(), tmpvalue);
			}
			GlobalServiceImpl tmpUserService = new GlobalServiceImpl();
			log.info("----SinaRspTreatHelper----front");
			tmpUserService.saveSinaResponse(from, tmpobj.toString(), charset, SafeUtils.getCurrentUnixTime());
			log.info("----SinaRspTreatHelper----behind");
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		if (tmpNotifyType.equals(SinaConsts.Notifybusstype_trade_status_sync)) {// 交易扣款响应
			log.info("==========进入活期赎回交易，保理商扣款响应==========");
			int tmpFinanticalStatus = 0;
			int tmpCurrentInvestStatus = 0;
			int tmpSinaTransStatus = 0;
			int tmpTenderStatus = 0;
			String tmpTradeStatus = SafeUtils.getRequestValue(paramMap.get("trade_status"));
			String tmpTradeAmt = SafeUtils.getRequestValue(paramMap.get("trade_amount"));
			BigDecimal tmpTransAmt = SafeUtils.getBigDecimal(transAmt);

			String tmptransactioNo = IDGenerator.generateRequestNo();
			GenericResult productMsg = userService.getCurrentSellerCode(tmpOrdId, tmpTrxId, tmptransactioNo);
			String tmpRespCodes = SafeUtils.getRequestValue(productMsg.getData().get("RespCode"));
			if (tmpTradeStatus.equals(SinaConsts.DealStatus_TRADE_FINISHED)) {
				SinaHelper tmphelper = new SinaHelper();
				UserServiceImpl userService = new UserServiceImpl();
				log.info("tmptransactioNo====================" + tmptransactioNo);
				log.info("tmpOrdId====================" + tmpOrdId);
				log.info("tmpTrxId====================" + tmpTrxId);

				String tmpProductCode = SafeUtils.getRequestValue(productMsg.getData().get("ProductCode"));
				String tmpTenderOrdId = SafeUtils.getRequestValue(productMsg.getData().get("TenderOrdId"));
				String tmpSellerCode = SafeUtils.getRequestValue(productMsg.getData().get("SellerCode"));
				String tmpCustomerCode = SafeUtils.getRequestValue(productMsg.getData().get("CustomerCode"));
				String tmpCustomerId = SafeUtils.getRequestValue(productMsg.getData().get("CustomerId"));
				String tmpSellerId = SafeUtils.getRequestValue(productMsg.getData().get("SellerID"));
				String tmpCmdVersion = SinaConsts.VERSION_1;
				log.info("SinaRespCode====================" + tmpRespCodes);
				if (tmpRespCodes.equals("3")) {// 保理商打款至中间账户

					tmpFinanticalStatus = 2;
					tmpCurrentInvestStatus = 0;
					tmpSinaTransStatus = 1;
					tmpTenderStatus = 2;
					log.info("仓单宝用户赎回，第一步异步返回成功的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentRansomStatus);
					log.info("仓单宝用户赎回，第一步异步返回成功的存储过程----P_SaveSinaAsynchronousResponse----behind");

					log.info(
							"仓单宝用户赎回，第一步异步返回成功，异步更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					userService.saveSinaCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus, tmpCurrentInvestStatus, tmpTrxId,
							tmpSinaTransStatus, tmpTenderStatus);
					log.info(
							"仓单宝用户赎回，第一步异步返回成功，异步更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");

					log.info("仓单宝赎回，中间商打款至用户之前，requestHistory插入的存储过程----P_SaveSinaRequest----front");
					userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
							SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
							tmpCmdVersion, "", "", Consts.CUSTTYPE_SELLER, tmpSellerId, 0, 0, 0, 0, tmpTransAmt,
							new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0), tmpSellerCode,
							SinaConsts.AccountType_BASIC, tmpPayMethod, "", 0, Consts.OPERATETYPE_INSERT);
					log.info("仓单宝赎回，中间商打款至用户之前，requestHistory插入的存储过程----P_SaveSinaRequest----behind");

					log.info("仓单宝赎回，中间商打款至用户之前，表t_financialrecord插入资金记录的存储过程----front");
					userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_CUSTOMER,
							Integer.parseInt(tmpCustomerId), Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt,
							Consts.FUNDORGID_SINA, tmptransactioNo, "",
							Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")), SafeUtils.getCurrentUnixTime(),
							SafeUtils.getCurrentUnixTime(), "", "用户收款| 活期产品[" + finProductName + "]赎回", null,
							finSellerCode, "", "", "", "", "", tmpTransAmt, new BigDecimal(0.0), 0, "", 0,
							Consts.FINANCIALRECORD_CURRENT_PAY, 0, Consts.OPERATETYPE_INSERT);
					log.info("仓单宝赎回，中间商打款至用户之前，表t_financialrecord插入资金记录的存储过程----behind");

					log.info("仓单宝赎回，中间商打款至用户之前，表 t_current_invest更新数据的存储过程----front");
					currentService.saveCurrentInvest(0, 0, 0, 2, new BigDecimal(0.0), new BigDecimal(0.0),
							new BigDecimal(0.0), 0, 0, "", 1, tmpOrdId, tmptransactioNo, "", 0, 2,
							Consts.OPERATETYPE_UPDATE);
					log.info("仓单宝赎回，中间商打款至用户之前，表 t_current_invest更新数据的存储过程----behind");

					SinaRspResult result = new SinaRspResult();
					tmphelper.setSinapay_callbackurl(SystemProperties.get("sinapay_callbackurl_current_ransom"));
					result = tmphelper.create_single_hosting_pay_trade(tmptransactioNo, SinaConsts.Out_trade_no_2002,
							tmpSellerCode, SinaConsts.Identity_type_uid, SinaConsts.AccountType_BASIC,
							new BigDecimal(tmpTradeAmt),
							tmpSellerCode + "^" + SinaConsts.Identity_type_uid + "^" + SinaConsts.AccountType_BASIC
									+ "^" + tmpCustomerCode + "^" + SinaConsts.Identity_type_uid + "^"
									+ SinaConsts.AccountType_SAVING_POT + "^" + new BigDecimal(tmpTradeAmt) + "^" + "|",
							"活期赎回", "", tmpProductCode);

					if (result.getResultCode().equals("APPLY_SUCCESS")) {

						log.info("仓单宝赎回，中间商打款至用户成功，表 t_current_invest更新数据的存储过程----front");
						GenericResult productMsg4 = currentService.saveCurrentInvest(0, 0, 0, 2, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), 0, 0, "", 1, tmpOrdId, tmptransactioNo, "", 0,
								8, Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户成功，表 t_current_invest更新数据的存储过程----behind");
						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("仓单宝赎回，中间商打款至用户成功,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_SELLER, tmpCustomerId, 0,
								0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户成功,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("仓单宝赎回，中间商打款至用户成功，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_CUSTOMER,
								Integer.parseInt(tmpCustomerId), Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt,
								Consts.FUNDORGID_SINA, result.getResultMap().get("out_trade_no"),
								result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"用户收款| 活期产品[" + finProductName + "]赎回", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD_CURRENT_PAY, 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户成功，表t_financialrecord更新资金记录的存储过程----behind");
					}else{
						
						log.info("仓单宝赎回，中间商打款至用户失败，表 t_current_invest更新数据的存储过程----front");
						GenericResult productMsg4 = currentService.saveCurrentInvest(0, 0, 0, 2, new BigDecimal(0.0),
								new BigDecimal(0.0), new BigDecimal(0.0), 0, 0, "", 3, tmpOrdId, tmptransactioNo, "", 3,
								9, Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户失败，表 t_current_invest更新数据的存储过程----behind");
						finCustomerID = Integer.parseInt(productMsg4.getData().get("ResultSellerID").toString());
						finProductName = productMsg4.getData().get("ResultProductName").toString();
						finSellerCode = productMsg4.getData().get("ResultSellerCode").toString();

						log.info("仓单宝赎回，中间商打款至用户失败,requestHistory更新的存储过程----P_SaveSinaRequest----front");
						userService.saveSinaRequestHistory(tmptransactioNo, SafeUtils.getCurrentUnixTime(),
								SafeUtils.getCurrentUnixTime(), SinaConsts.CMDID_LOANS, Consts.FUNDORGID_SINA,
								tmpCmdVersion, result.getResultMap().get("response_code"),
								result.getResultMap().get("response_message"), Consts.CUSTTYPE_SELLER, tmpCustomerId, 0,
								0, 0, 0, tmpTransAmt, new BigDecimal(0.0), new BigDecimal(0.0), new BigDecimal(0.0),
								tmpSellerCode, SinaConsts.AccountType_BASIC, tmpPayMethod, "", 1,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户失败,requestHistory更新的存储过程----P_SaveSinaRequest----behind");

						log.info("仓单宝赎回，中间商打款至用户失败，表t_financialrecord更新资金记录的存储过程----front");
						userService.saveSinaFinancialrecord(Consts.FINANCIALRECORD_CUSTOMER,
								Integer.parseInt(tmpCustomerId), Consts.FINANCIALRECORD_FINANCIALTYPE_PAY, tmpTransAmt,
								Consts.FUNDORGID_SINA, result.getResultMap().get("out_trade_no"),
								result.getResultMap().get("out_trade_no"),
								Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")),
								SafeUtils.getCurrentUnixTime(), SafeUtils.getCurrentUnixTime(), "",
								"用户收款| 活期产品[" + finProductName + "]赎回", null, finSellerCode, "", "", "", "", "",
								tmpTransAmt, new BigDecimal(0.0), 0, "", 0, Consts.FINANCIALRECORD_CURRENT_PAY, 3,
								Consts.OPERATETYPE_UPDATE);
						log.info("仓单宝赎回，中间商打款至用户失败，表t_financialrecord更新资金记录的存储过程----behind");
						
					}
				} else if (tmpRespCodes.equals("2")) {// 中间账户还款至用户
					log.info("2===============用户赎回，中间商放款，用户收款");
					log.info("仓单宝用户赎回，第二步异步返回成功的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentRansomStatus);
					log.info("仓单宝用户赎回，第二步异步返回成功的存储过程----P_SaveSinaAsynchronousResponse----front");

					log.info("仓单宝用户赎回，第二步异步返回成功，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					GenericResult productMsg2 = currentService.saveSinaSecCurrentAsynchronousUpdate(tmpOrdId,
							tmpNotifyId, tmpStatus, tmpTrxId, tmpNotifyTime, tmpNotifyTimeInt,
							tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(), tmpStatus, tmpResMessage, 2,
							Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")), 2, tmpTrxId, 1, 3);
					log.info(
							"仓单宝用户赎回，第二步异步返回成功，更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");

					if (productMsg2.getResult() == 0) {
						Map tmpinvestinfo = this.currentService.getCurrentInvestDetailByTransOuterOrder(tmpOrdId);

						StringBuilder tmpsb = new StringBuilder();
						tmpsb.append("您成功购赎回了")
								.append(SafeUtils
										.formatCurrency(SafeUtils.getBigDecimal(tmpinvestinfo.get("TotalMoney")), 2))
								.append("元的活期理财产品。");

						SMSHelper tmpsmshelper = new SMSHelper();
						// 下面的内容发送应该交由消息队列发送，这个暂时用此方法
						log.info("SendSMS:" + tmpsb.toString());
						tmpsmshelper.sendByHttp(SafeUtils.getString(productMsg2.getData().get("Mobile")),
								tmpsb.toString(), "" + SafeUtils.getCurrentUnixTime());
					}
				}

			} else if (tmpTradeStatus.equals(SinaConsts.DealStatus_PAY_FINISHED)) {
				// 新浪此状态不再返回，但测试平台仍具有，故列出，勿删！
			} else {

				if (tmpRespCodes.equals("1")) {

					tmpFinanticalStatus = 3;
					tmpCurrentInvestStatus = 3;
					tmpSinaTransStatus = 3;
					tmpTenderStatus = 7;
					// 改变t_requestHistory、t_current_invest、t_financialrecord的状态，由处理中改为处理失败
					log.info("仓单宝用户赎回，失败后返回第一步异步的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentRansomStatus);
					log.info("仓单宝用户赎回，失败后返回第一步异步的存储过程----P_SaveSinaAsynchronousResponse----behind");

					log.info("仓单宝用户赎回，失败后返回第一步异步,更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----front");
					userService.saveSinaCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus,tmpCurrentInvestStatus,
							tmpTrxId,tmpSinaTransStatus, tmpTenderStatus);
					log.info("仓单宝用户赎回，失败后返回第一步异步,更新表t_financialrecord、t_requesthistory以及t_current_invest的存储过程----behind");
				}else if(tmpRespCodes.equals("2")){
					
					tmpFinanticalStatus = 3;
					tmpCurrentInvestStatus = 3;
					tmpSinaTransStatus = 4;
					tmpTenderStatus = 4;
					log.info("==========进入交易，活期赎回第二步异步响应失败==========:" + tmpTradeStatus);
					log.info("用户活期赎回，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----front");
					userService.saveSinaAsynchronousResponse(from, SafeUtils.getCurrentUnixTime(), tmpService,
							tmpNotifyId, tmpFreezeTrxId, tmpOrdId, tmpNotifyTime, tmpUsrCustId, transAmt, tmpTrxId,
							tmpRespCharset, Consts.RESPFORMAT, tmpobj.toString(), "", "", tmpCurrentRansomStatus);
					log.info("用户活期赎回，新浪第二步异步失败返回的存储过程----P_SaveSinaAsynchronousResponse----behind");
					log.info(
							"用户活期赎回，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----front");
					currentService.saveSinaSecCurrentAsynchronousUpdate(tmpOrdId, tmpNotifyId, tmpStatus, tmpTrxId,
							tmpNotifyTime, tmpNotifyTimeInt, tmpNotifyTimeInt - SafeUtils.getCurrentUnixTime(),
							tmpStatus, tmpResMessage, tmpFinanticalStatus,
							Integer.parseInt(SafeUtils.getCurrentTimeStr("yyyyMMdd")), tmpSinaTransStatus, tmpTrxId,
							tmpCurrentInvestStatus, tmpTenderStatus);
					log.info(
							"用户活期赎回，新浪第二步异步失败返回，异步更新表t_financialrecord、t_requesthistory以及t_coustomer_invest的存储过程----behind");
				}
			}
		}
		return tmpresult;
	}

}
