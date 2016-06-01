package com.github.startup.sina;

import baojinsuo.sina.utils.*;
import baojinsuo.systemconfig.SystemProperties;
import baojinsuo.utils.SafeUtils;
import com.meidusa.fastjson.JSON;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SinaHelper {
	private Logger log = Logger.getLogger(SinaHelper.class);
	private static String Sinapay_mas_gateway = SystemProperties.get("sinapay_mas_gateway");
	private static String Sina_encryptkey = SystemProperties.get("sina_encryptkey");
	private static String Sinapay_mgs_gateway = SystemProperties.get("sinapay_mgs_gateway");
	private static String Sinapay_partner_id = SystemProperties.get("sinapay_partner_id");
	private static String Sinapay_sign_type = SystemProperties.get("sinapay_sign_type");
	private String Sinapay_callbackurl = SystemProperties.get("sinapay_callbackurl");
	private static String Sinapay_returnurl = SystemProperties.get("sinapay_returnurl");
	private static String Sinapay_default_version = SinaConsts.VERSION_1;
	
	public SinaHelper() {
		// TODO Auto-generated constructor stub
	}
	public String getSinapay_callbackurl() {
		return Sinapay_callbackurl;
	}

	public void setSinapay_callbackurl(String sinapay_callbackurl) {
		Sinapay_callbackurl = sinapay_callbackurl;
	}

	/**
	 * 2.1 创建激活会员
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param member_type
	 *            会员类型（1：个人；2：企业）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_activate_member(String identity_id, String identity_type, String member_type,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_activate_member;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("version", version);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!member_type.equals("")) {
			tmprequestmap.put("member_type", member_type);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.2 设置实名信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param real_name
	 *            真实姓名
	 * @param cert_type
	 *            证件类型（目前只支持身份证：IC）
	 * @param cert_no
	 *            证件号码
	 * @param need_confirm
	 *            是否认证（是否需要钱包做实名认证，值为Y/N，默认Y。暂不开放外部自助实名认证。）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult set_real_name(String identity_id, String identity_type, String real_name, String cert_type,
			String cert_no, String need_confirm, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_set_real_name;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String encrypt = Sina_encryptkey;

		byte[] real_name_byte = null;
		byte[] cert_no_byte = null;
		try {
			// 认证内容加密
			real_name_byte = RSA.encryptByPublicKey(real_name.getBytes("utf-8"), encrypt);
			cert_no_byte = RSA.encryptByPublicKey(cert_no.getBytes(), encrypt);
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		String base64_real_name_encrypt = Base64.encode(real_name_byte);
		String base64_cert_no_encrypt = Base64.encode(cert_no_byte);

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		tmprequestmap.put("real_name", base64_real_name_encrypt);
		tmprequestmap.put("cert_type", cert_type);
		tmprequestmap.put("cert_no", base64_cert_no_encrypt);
		if (!need_confirm.equals("")) {
			tmprequestmap.put("need_confirm", need_confirm);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.3 绑定认证信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param verify_type
	 *            认证类型（MOBILE）
	 * @param verify_entity
	 *            认证内容
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult binding_verify(String identity_id, String identity_type, String verify_type,
			String verify_entity, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_binding_verify;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String encrypt = Sina_encryptkey;

		byte[] verify_entity_encrypt = null;
		try {
			// 认证内容加密
			verify_entity_encrypt = RSA.encryptByPublicKey(verify_entity.getBytes("utf-8"), encrypt);
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		String base64_verify_entity_encrypt = Base64.encode(verify_entity_encrypt);

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		tmprequestmap.put("verify_type", verify_type);
		tmprequestmap.put("verify_entity", base64_verify_entity_encrypt.toString());
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.4 解绑认证信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param verify_type
	 *            认证类型（MOBILE）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult unbinding_verify(String identity_id, String identity_type, String verify_type,
			String extend_param) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_unbinding_verify;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		tmprequestmap.put("verify_type", verify_type);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/***
	 * 2.5 查询认证信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param verify_type
	 *            认证类型（MOBILE）
	 * @param is_mask
	 *            是否掩码（返回认证内容是否掩码显示， 如手机号掩码为13****8。Y-是；N-否。为空默认为Y）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_verify(String identity_id, String identity_type, String verify_type, String is_mask,
			String extend_param) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_verify;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		tmprequestmap.put("verify_type", verify_type);
		if (!is_mask.equals("")) {
			tmprequestmap.put("is_mask", is_mask);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.6 绑定银行卡
	 * 
	 * @param request_no
	 *            绑卡请求号（商户网站交易订单号，商户内部保证唯一）
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param bank_code
	 *            绑卡请求号（银行字母编号）
	 * @param bank_account_no
	 *            绑卡请求号（银行卡号）
	 * @param account_name
	 *            户名
	 * @param card_type
	 *            卡类型（见附录DEBIT）
	 * @param card_attribute
	 *            卡属性（见附录C）
	 * @param cert_type
	 *            证件类型（目前只支持身份证：IC）
	 * @param cert_no
	 *            证件号码
	 * @param phone_no
	 *            银行预留手机号
	 * @param validity_period
	 *            有效期
	 * @param verification_value
	 *            CVV2
	 * @param province
	 *            省份
	 * @param city
	 *            城市
	 * @param bank_branch
	 *            支行名称
	 * @param verify_mode
	 *            认证方式（银行卡真实性认证方式，见附录“卡认证方式”，空则表示不认证SIGN）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult binding_bank_card(String request_no, String identity_id, String identity_type,
			String bank_code, String bank_account_no, String account_name, String card_type, String card_attribute,
			String cert_type, String cert_no, String phone_no, String validity_period, String verification_value,
			String province, String city, String bank_branch, String verify_mode, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_binding_bank_card;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		String encrypt = Sina_encryptkey;

		byte[] bank_account_no_byte = null;
		byte[] phone_no_byte = null;

		try {
			// 认证内容加密
			bank_account_no_byte = RSA.encryptByPublicKey(bank_account_no.getBytes(), encrypt);
			phone_no_byte = RSA.encryptByPublicKey(phone_no.getBytes(), encrypt);
		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		String bank_account_no_byte_encrypt = Base64.encode(bank_account_no_byte);
		String phone_no_byte_encrypt = Base64.encode(phone_no_byte);
		if (!version.equals("")) {
			tmprequestmap.put("version", version);
		}
		if (!tmpCmdId.equals("")) {
			tmprequestmap.put("service", tmpCmdId);
		}
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));

		if (!tmpPartnerId.equals("")) {
			tmprequestmap.put("partner_id", tmpPartnerId);
		}
		if (!tmpCharset.equals("")) {
			tmprequestmap.put("_input_charset", tmpCharset);
		}
		tmprequestmap.put("request_no", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		if (!identity_id.equals("")) {
			tmprequestmap.put("identity_id", identity_id);
		}
		tmprequestmap.put("identity_type", "UID");
		tmprequestmap.put("bank_code", bank_code);
		tmprequestmap.put("bank_account_no", bank_account_no_byte_encrypt);
		if (!account_name.equals("")) {
			tmprequestmap.put("account_name", account_name);
		}
		tmprequestmap.put("card_type", card_type);
		tmprequestmap.put("card_attribute", card_attribute);
		if (!cert_type.equals("")) {
			tmprequestmap.put("cert_type", cert_type);
		}
		if (!cert_no.equals("")) {
			tmprequestmap.put("cert_no", cert_no);
		}
		if (!phone_no.equals("")) {
			tmprequestmap.put("phone_no", phone_no_byte_encrypt);
		}
		if (!bank_code.equals("")) {
			tmprequestmap.put("bank_code", bank_code);
		}
		if (!validity_period.equals("")) {
			tmprequestmap.put("validity_period", validity_period);
		}
		if (!verification_value.equals("")) {
			tmprequestmap.put("verification_value", verification_value);
		}
		tmprequestmap.put("province", province);
		tmprequestmap.put("city", city);
		if (!bank_branch.equals("")) {
			tmprequestmap.put("bank_branch", bank_branch);
		}
		if (!verify_mode.equals("")) {
			tmprequestmap.put("verify_mode", verify_mode);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.7 绑定银行卡推进
	 * 
	 * @param ticket
	 *            绑卡时返回的ticket
	 * @param valid_code
	 *            短信验证码
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult binding_bank_card_advance(String ticket, String valid_code, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_binding_bank_card_advance;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("ticket", ticket);
		tmprequestmap.put("valid_code", valid_code);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.8 解绑银行卡
	 * 
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param card_id
	 *            钱包系统卡ID 钱包系统卡ID，绑卡返回的ID
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult unbinding_bank_card(String identity_type, String identity_id, String card_id,
			String extend_param) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_unbinding_bank_card;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		tmprequestmap.put("card_id", card_id);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.9 查询银行卡
	 * 
	 * 
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param card_id
	 *            钱包系统卡ID
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_bank_card(String identity_type, String identity_id, String card_id,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_query_bank_card;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!card_id.equals("")) {
			tmprequestmap.put("card_id", card_id);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.10 查询余额/基金份额
	 * 
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_balance(String identity_type, String identity_id, String account_type,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_query_balance;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.11 查询收支明细
	 * 
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param start_time
	 *            开始时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param end_time
	 *            结束时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_account_details(String identity_type, String identity_id, String account_type,
			String start_time, String end_time, String extend_param, String page_no, String page_size) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_query_account_details;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		tmprequestmap.put("start_time", start_time);
		tmprequestmap.put("end_time", end_time);
		if (!page_no.equals("")) {
			tmprequestmap.put("page_no", page_no);
		}
		if (!page_size.equals("")) {
			tmprequestmap.put("page_size", page_size);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.12 冻结余额
	 * 
	 * @param out_freeze_no
	 *            商户网站冻结订单号，商户内部保证唯一
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param amount
	 *            金额
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult balance_freeze(String out_freeze_no, String identity_id, String identity_type,
			String account_type, BigDecimal amount, String summary, String extend_param) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_balance_freeze;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		tmprequestmap.put("out_freeze_no", out_freeze_no);

		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		tmprequestmap.put("summary", summary);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.13 解冻余额
	 * 
	 * @param out_unfreeze_no
	 *            商户网站解冻订单号，商户内部保证唯一
	 * @param out_freeze_no
	 *            商户网站冻结订单号，商户内部保证唯一
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param amount
	 *            金额
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult balance_unfreeze(String out_unfreeze_no, String out_freeze_no, String identity_id,
			String identity_type, BigDecimal amount, String summary, String extend_param) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_balance_unfreeze;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		tmprequestmap.put("out_unfreeze_no", out_unfreeze_no);
		tmprequestmap.put("out_freeze_no", out_freeze_no);
		if (!amount.equals("")) {
			tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		}
		tmprequestmap.put("summary", summary);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.14 请求审核企业会员资质
	 * 
	 * @param audit_order_no
	 *            商户网站交易订单号，商户内部保证唯一
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param member_type
	 *            会员类型（1：个人；2：企业）
	 * @param company_name
	 *            公司名称全称，以便审核通过
	 * @param website
	 *            企业网址
	 * @param address
	 *            企业地址
	 * @param license_no
	 *            执照号
	 * @param license_address
	 *            营业执照所在地
	 * @param license_expire_date
	 *            执照过期日（营业期限）
	 * @param business_scope
	 *            营业范围
	 * @param telephone
	 *            联系电话
	 * @param email
	 *            联系Email
	 * @param organization_no
	 *            组织机构代码
	 * @param summary
	 *            摘要
	 * @param legal_person
	 * @param cert_no
	 *            证件号码
	 * @param cert_type
	 *            证件类型（目前只支持身份证：IC）
	 * @param legal_person_phone
	 *            法人手机号码
	 * @param bank_code
	 *            绑卡请求号（银行字母编号）
	 * @param bank_account_no
	 *            绑卡请求号（银行卡号）
	 * @param card_type
	 *            卡类型（见附录DEBIT）
	 * @param card_attribute
	 *            卡属性（见附录C）
	 * @param province
	 *            省份
	 * @param city
	 *            城市
	 * @param bank_branch
	 *            支行名称
	 * @param fileName
	 *            文件名称
	 * @param digest
	 *            文件摘要
	 * @param digestType
	 *            文件摘要算法
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult audit_member_infos(String audit_order_no, String identity_id, String identity_type,
			String member_type, String company_name, String website, String address, String license_no,
			String license_address, String license_expire_date, String business_scope, String telephone, String email,
			String organization_no, String summary, String legal_person, String cert_no, String cert_type,
			String legal_person_phone, String bank_code, String bank_account_no, String card_type,
			String card_attribute, String province, String city, String bank_branch, String fileName, String digest,
			String digestType, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_audit_member_infos;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String tmpNotifyurl = Sinapay_callbackurl;
		tmprequestmap.put("notify_url", tmpNotifyurl);
		String encrypt = Sina_encryptkey;
		byte[] license_no_byte = null;// 执照号
		byte[] telephone_byte = null;// 联系电话
		byte[] email_byte = null;// 联系Email
		byte[] organization_no_byte = null;// 组织机构代码
		byte[] legal_person_byte = null;// 企业法人
		byte[] cert_no_byte = null;// 法人证件号码
		byte[] legal_person_phone_byte = null;// 法人手机号码
		byte[] bank_account_no_byte = null;// 银行卡号

		try {
			// 认证内容加密
			if (!license_no.equals("")) {
				license_no_byte = RSA.encryptByPublicKey(license_no.getBytes(), encrypt);
				String base64_license_no_encrypt = Base64.encode(license_no_byte);
				tmprequestmap.put("license_no", base64_license_no_encrypt);
			}
			if (!telephone.equals("")) {
				telephone_byte = RSA.encryptByPublicKey(telephone.getBytes(), encrypt);
				String base64_telephone_encrypt = Base64.encode(telephone_byte);
				tmprequestmap.put("telephone", base64_telephone_encrypt);
			}
			if (!email.equals("")) {
				email_byte = RSA.encryptByPublicKey(email.getBytes(), encrypt);
				String base64_email_encrypt = Base64.encode(email_byte);
				tmprequestmap.put("email", base64_email_encrypt);
			}
			if (!organization_no.equals("")) {
				organization_no_byte = RSA.encryptByPublicKey(organization_no.getBytes(), encrypt);
				String base64_organization_no_encrypt = Base64.encode(organization_no_byte);
				tmprequestmap.put("organization_no", base64_organization_no_encrypt);
			}
			if (!legal_person.equals("")) {
				legal_person_byte = RSA.encryptByPublicKey(legal_person.getBytes("utf-8"), encrypt);
				String base64_legal_person_encrypt = Base64.encode(legal_person_byte);
				tmprequestmap.put("legal_person", base64_legal_person_encrypt);
			}
			if (!cert_no.equals("")) {
				cert_no_byte = RSA.encryptByPublicKey(cert_no.getBytes(), encrypt);
				String base64_cert_no_encrypt = Base64.encode(cert_no_byte);
				tmprequestmap.put("cert_no", base64_cert_no_encrypt);
			}
			if (!legal_person_phone.equals("")) {
				legal_person_phone_byte = RSA.encryptByPublicKey(legal_person_phone.getBytes(), encrypt);
				String base64_legal_person_phone_encrypt = Base64.encode(legal_person_phone_byte);
				tmprequestmap.put("legal_person_phone", base64_legal_person_phone_encrypt);
			}
			if (!bank_account_no.equals("")) {
				bank_account_no_byte = RSA.encryptByPublicKey(bank_account_no.getBytes(), encrypt);
				String base64_bank_account_no_encrypt = Base64.encode(bank_account_no_byte);
				tmprequestmap.put("bank_account_no", base64_bank_account_no_encrypt);
			}

		} catch (Exception e1) {
			log.error(e1.getMessage());
		}
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("audit_order_no", audit_order_no);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		if (!member_type.equals("")) {
			tmprequestmap.put("member_type", member_type);
		}
		tmprequestmap.put("company_name", company_name);

		if (!website.equals("")) {
			tmprequestmap.put("website", website);
		}
		if (!address.equals("")) {
			tmprequestmap.put("address", address);
		}

		if (!license_address.equals("")) {
			tmprequestmap.put("license_address", license_address);
		}
		if (!license_expire_date.equals("")) {
			tmprequestmap.put("license_expire_date", license_expire_date);
		}
		if (!business_scope.equals("")) {
			tmprequestmap.put("business_scope", business_scope);
		}

		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}

		if (!cert_type.equals("")) {
			tmprequestmap.put("cert_type", cert_type);
		}

		if (!bank_code.equals("")) {
			tmprequestmap.put("bank_code", bank_code);
		}

		if (!card_type.equals("")) {
			tmprequestmap.put("card_type", card_type);
		}
		if (!card_attribute.equals("")) {
			tmprequestmap.put("card_attribute", card_attribute);
		}
		if (!province.equals("")) {
			tmprequestmap.put("province", province);
		}
		if (!city.equals("")) {
			tmprequestmap.put("city", city);
		}
		if (!bank_branch.equals("")) {
			tmprequestmap.put("bank_branch", bank_branch);
		}
		if (!fileName.equals("")) {
			tmprequestmap.put("fileName", fileName);
		}
		if (!digest.equals("")) {
			tmprequestmap.put("digest", digest);
		}

		if (!digestType.equals("")) {
			tmprequestmap.put("digestType", digestType);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);

			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.15 查询企业会员信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param member_type
	 *            会员类型（1：个人；2：企业）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_member_infos(String identity_id, String identity_type, String member_type,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_query_member_infos;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!member_type.equals("")) {
			tmprequestmap.put("member_type", member_type);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;

	}

	/***
	 * 2.16 查询企业会员审核结果
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_audit_result(String identity_id, String identity_type, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_query_audit_result;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;

	}
	
	/***
	 * 2.17	sina页面展示用户信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public String show_member_infos_sina(String identity_id, String identity_type, String extend_param) {
		String tmpResult = null;
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_show_member_infos_sina;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
						
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
		return tmpResult;

	}
	

	/**
	 * 2.18 查询冻结解冻结果
	 * 
	 * @param out_ctrl_no
	 *            冻结解冻订单号
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_ctrl_result(String out_ctrl_no, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;

		String tmpCmdId = SinaConsts.Service_query_ctrl_result;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("out_ctrl_no", out_ctrl_no);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 2.19 经办人信息
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param agent_name
	 *            经办人姓名
	 * @param license_no
	 *            经办人身份证
	 * @param license_type_code
	 *            证件类型 （ID）
	 * @param agent_mobile
	 *            经办人手机号
	 * @param email
	 *            经办人邮箱
	 * @return
	 */
	public SinaRspResult smt_fund_agent_buy(String identity_id, String identity_type, String agent_name,
			String license_no, String license_type_code, String agent_mobile, String email) {
		Map<String, String> tmprequestmap = new HashMap<String, String>();

		SinaRspResult result = new SinaRspResult();
		String tmpInterfaceUrl = Sinapay_mgs_gateway;

		String version = Sinapay_default_version;
		if (identity_type == null || "".equals(identity_type))
			identity_type = "UID";
		String tmpCmdId = SinaConsts.Service_smt_fund_agent_buy;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		String encrypt = Sina_encryptkey;
		try {

			byte[] agent_name_byte = RSA.encryptByPublicKey(agent_name.getBytes("utf-8"), encrypt);
			String base64_agent_name = Base64.encode(agent_name_byte);
			tmprequestmap.put("agent_name", base64_agent_name);

			byte[] license_no_byte = RSA.encryptByPublicKey(license_no.getBytes(), encrypt);
			String base64_license_no = Base64.encode(license_no_byte);
			tmprequestmap.put("license_no", base64_license_no);

			byte[] agent_mobile_byte = RSA.encryptByPublicKey(agent_mobile.getBytes(), encrypt);
			String base64_agent_mobile = Base64.encode(agent_mobile_byte);
			tmprequestmap.put("agent_mobile", base64_agent_mobile);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		tmprequestmap.put("license_type_code", license_type_code);

		if (!email.equals("")) {
			tmprequestmap.put("email", email);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/*********************** 订单类接口 **********************************/
	/**
	 * 3.1创建托管代收交易
	 * 
	 * @param out_trade_no
	 *            交易订单号 交易订单号
	 * @param out_trade_code
	 *            交易码 商户网站代收交易业务码，见附录 1001代收投资金1002代收还款金2001代付借款金2002代付（本金/收益）金
	 * @param summary
	 *            摘要
	 * @param trade_close_time
	 *            交易关闭时间 取值范围：1m～15d。m-分钟，h-小时，d-天不接受小数点，如1.5d，可转换为36h。
	 * @param can_repay_on_failed
	 *            支付失败后，是否可以重复发起支付取值范围：Y、N(忽略大小写)Y：可以再次支付N：不能再次支付默认值为Y
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param goods_id
	 *            商户标的号
	 * @param payer_id
	 *            付款用户ID
	 * @param payer_identity_type
	 *            标识类型
	 * @param payer_ip
	 *            付款用户IP地址
	 * @param pay_method
	 *            支付方式
	 * @return
	 */
	public SinaRspResult create_hosting_collect_trade(String out_trade_no, String out_trade_code, String summary,
			String trade_close_time, String can_repay_on_failed, String extend_param, String goods_id, String payer_id,
			String payer_identity_type, String payer_ip, String pay_method) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_hosting_collect_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String tmpNotifyurl = Sinapay_callbackurl;
		String tmpreturnUrl = Sinapay_returnurl;
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("return_url", tmpreturnUrl);
		tmprequestmap.put("notify_url", tmpNotifyurl);
		tmprequestmap.put("out_trade_no", out_trade_no);
		tmprequestmap.put("out_trade_code", out_trade_code);
		tmprequestmap.put("summary", summary);
		if (!trade_close_time.equals("")) {
			tmprequestmap.put("trade_close_time", trade_close_time);
		}
		if (!can_repay_on_failed.equals("")) {
			tmprequestmap.put("can_repay_on_failed", can_repay_on_failed);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!goods_id.equals("")) {
			tmprequestmap.put("goods_id", goods_id);
		}
		tmprequestmap.put("payer_id", payer_id);
		tmprequestmap.put("payer_identity_type", payer_identity_type);
		if (!payer_ip.equals("")) {
			tmprequestmap.put("payer_ip", payer_ip);
		}

		tmprequestmap.put("pay_method", pay_method);

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.2 创建托管代付交易
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param out_trade_code
	 *            交易码
	 * @param payee_identity_id
	 *            收款人标识
	 * @param payee_identity_type
	 *            收款人标识类型：UID
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param amount
	 *            金额
	 * @param split_list
	 *            分账信息列表
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param goods_id
	 *            商户标的号
	 * @return
	 */
	public SinaRspResult create_single_hosting_pay_trade(String out_trade_no, String out_trade_code,
			String payee_identity_id, String payee_identity_type, String account_type, BigDecimal amount,
			String split_list, String summary, String extend_param, String goods_id) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_single_hosting_pay_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String tmpNotifyurl = Sinapay_callbackurl;
		String tmpreturnUrl = Sinapay_returnurl;
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("return_url", tmpreturnUrl);
		tmprequestmap.put("notify_url", tmpNotifyurl);
		tmprequestmap.put("out_trade_no", out_trade_no);
		tmprequestmap.put("out_trade_code", out_trade_code);
		tmprequestmap.put("payee_identity_id", payee_identity_id);
		tmprequestmap.put("payee_identity_type", payee_identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		if (!split_list.equals("")) {
			tmprequestmap.put("split_list", split_list);
		}
		tmprequestmap.put("summary", summary);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!goods_id.equals("")) {
			tmprequestmap.put("goods_id", goods_id);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.3 创建批量托管代付交易
	 * 
	 * @param out_pay_no支付请求号
	 * @param out_trade_code
	 *            交易码
	 * @param trade_list
	 *            交易列表
	 * @param notify_method
	 *            通知方式 详见附录
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_batch_hosting_pay_trade(String out_pay_no, String out_trade_code, String trade_list,
			String notify_method, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_batch_hosting_pay_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("out_pay_no", out_pay_no);
		tmprequestmap.put("out_trade_code", out_trade_code);
		tmprequestmap.put("trade_list", trade_list);
		tmprequestmap.put("notify_method", notify_method);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/***
	 * 3.4 托管交易支付
	 * 
	 * @param out_pay_no支付请求号
	 * @param outer_trade_no_list
	 *            商户网站唯一交易订单号集合
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param payer_ip
	 *            用户在商户平台发起支付时候的IP地址，公网IP，不是内网IP
	 * @param pay_method
	 *            支付方式 格式：支付方式^金额^扩展|支付方式^金额^扩展。扩展信息内容以“，”分隔，针对不同支付方式的扩展定义见附录
	 *            “支付方式扩展
	 * @return
	 */
	public SinaRspResult pay_hosting_trade(String out_pay_no, String outer_trade_no_list, String extend_param,
			String payer_ip, String pay_method) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_pay_hosting_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("out_pay_no", out_pay_no);
		tmprequestmap.put("outer_trade_no_list", outer_trade_no_list);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!payer_ip.equals("")) {
			tmprequestmap.put("payer_ip", payer_ip);
		}

		tmprequestmap.put("pay_method", pay_method);

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.5 支付结果查询
	 * 
	 * @param out_pay_no支付请求号
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult query_pay_result(String out_pay_no, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_pay_result;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("out_pay_no", out_pay_no);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/***
	 * 3.6 托管交易查询
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param out_trade_no
	 *            交易订单号
	 * @param start_time
	 *            开始时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param end_time
	 *            结束时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_hosting_trade(String identity_id, String identity_type, String out_trade_no,
			String start_time, String end_time, String extend_param, String page_no, String page_size) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_hosting_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		if (!identity_id.equals("")) {
			tmprequestmap.put("identity_id", identity_id);
		}
		if (!identity_type.equals("")) {
			tmprequestmap.put("identity_type", identity_type);
		}
		if (!out_trade_no.equals("")) {
			tmprequestmap.put("out_trade_no", out_trade_no);
		}
		if (!start_time.equals("")) {
			tmprequestmap.put("start_time", start_time);
		}
		if (!end_time.equals("")) {
			tmprequestmap.put("end_time", end_time);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		if (!page_no.equals("")) {
			tmprequestmap.put("page_no", page_no);
		}
		if (!page_size.equals("")) {
			tmprequestmap.put("page_size", page_size);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.7 托管交易批次查询
	 * 
	 * @param out_batch_no
	 *            交易批次号
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_hosting_batch_trade(String out_batch_no, String page_no, String page_size) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_hosting_batch_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("out_batch_no", out_batch_no);

		if (!page_no.equals("")) {
			tmprequestmap.put("page_no", page_no);
		}
		if (!page_size.equals("")) {
			tmprequestmap.put("page_size", page_size);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.8 托管退款
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param orig_outer_trade_no
	 *            需要退款的商户订单号
	 * @param refund_amount
	 *            退款金额
	 * @param summary
	 *            摘要
	 * @param split_list
	 *            分账信息列表（目前代付不支持退款，因此退款时分账列表都为空）
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_hosting_refund(String out_trade_no, String orig_outer_trade_no,
			BigDecimal refund_amount, String summary, String split_list, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_hosting_refund;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("out_trade_no", out_trade_no);
		tmprequestmap.put("orig_outer_trade_no", orig_outer_trade_no);
		tmprequestmap.put("refund_amount", SafeUtils.formatCurrency(refund_amount, 2));
		tmprequestmap.put("summary", summary);
		if (!split_list.equals("")) {
			tmprequestmap.put("split_list", split_list);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;

	}

	/***
	 * 3.9 托管退款查询
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param out_trade_no
	 *            交易订单号
	 * @param start_time
	 *            开始时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param end_time
	 *            结束时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_hosting_refund(String identity_id, String identity_type, String out_trade_no,
			String start_time, String end_time, String extend_param, String page_no, String page_size) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_hosting_refund;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!out_trade_no.equals("")) {
			tmprequestmap.put("out_trade_no", out_trade_no);
		}
		if (!start_time.equals("")) {
			tmprequestmap.put("start_time", start_time);
		}
		if (!end_time.equals("")) {
			tmprequestmap.put("end_time", end_time);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!page_no.equals("")) {
			tmprequestmap.put("page_no", page_no);
		}
		if (!page_size.equals("")) {
			tmprequestmap.put("page_size", page_size);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/***
	 * 3.10 托管充值
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param summary
	 *            摘要
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param amount
	 *            金额
	 * @param user_fee
	 *            用户手续费 用户手续费
	 * @param payer_ip
	 *            付款用户IP地址
	 * @param pay_method_tmp
	 *            支付方式
	 * @param pay_method
	 *            格式：支付方式^金额^扩展|支付方式^金额^扩展。扩展信息内容以“，”分隔，针对不同支付方式的扩展定义见附录
	 *            “支付方式扩展”
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_hosting_deposit(String out_trade_no, String summary, String identity_id,
			String identity_type, String account_type, BigDecimal amount, BigDecimal user_fee, String payer_ip,
			String pay_method_tmp, String pay_method, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_hosting_deposit;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpreturnUrl = Sinapay_returnurl;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String tmpNotifyurl = Sinapay_callbackurl;
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("notify_url", tmpNotifyurl);
		tmprequestmap.put("return_url", tmpreturnUrl);
		tmprequestmap.put("out_trade_no", out_trade_no);
		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		if (!user_fee.equals("")) {
			tmprequestmap.put("user_fee", SafeUtils.formatCurrency(user_fee, 2));
		}
		if (!payer_ip.equals("")) {
			tmprequestmap.put("payer_ip", payer_ip);
		}
		tmprequestmap.put("pay_method", pay_method);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		if (!pay_method_tmp.equals("online_bank")) {
			try {
				String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
				log.info("Sina Response No OnlineBank:" + tmpResult);
				Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
				result.setResultCode(tmpresultcontent.get("response_code").toString());
				result.setResultDesc(tmpresultcontent.get("response_message").toString());
				result.setResultMap(tmpresultcontent);
			} catch (Exception ex) {
				log.error(ex.getMessage());
				result = null;
			}
		} else {
			String html = "<form name='sinapay_checkout' id='sinapay_checkout' method='post'>";
			for (String key : tmprequestmap.keySet()) {
				try {
					html += "<input type='hidden' name='" + key + "' value='"
							+ URLEncoder.encode(tmprequestmap.get(key), tmpCharset) + "'/>";
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			html += "<script type = 'text/javascript'>";
			html += "document.sinapay_checkout.action = '" + tmpInterfaceUrl + "';";
			html += "document.sinapay_checkout.submit();";
			html += "</script>";
			Map<String, String> return_json = new HashMap<String, String>();
			return_json.put("result", html);
			return_json.put("display", "Y");
			String re_json = JSON.toJSONString(return_json);
			log.info("Sina Response OnlineBank:" + return_json);
			result.setWebStr(re_json);
		}
		return result;
	}

	/***
	 * 3.11 托管充值查询
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param out_trade_no
	 *            交易订单号
	 * @param start_time
	 *            开始时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param end_time
	 *            结束时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_hosting_deposit(String identity_id, String identity_type, String account_type,
			String out_trade_no, String start_time, String end_time, String extend_param, String page_no,
			String page_size) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_hosting_deposit;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		if (!out_trade_no.equals("")) {
			tmprequestmap.put("out_trade_no", out_trade_no);
		}
		if (!start_time.equals("")) {
			tmprequestmap.put("start_time", start_time);
		}
		if (!end_time.equals("")) {
			tmprequestmap.put("end_time", end_time);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!page_no.equals("")) {
			tmprequestmap.put("page_no", page_no);
		}
		if (!page_size.equals("")) {
			tmprequestmap.put("page_size", page_size);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.12 托管提现
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param summary
	 *            摘要
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param amount
	 *            金额
	 * @param user_fee
	 *            用户手续费
	 * @param card_id
	 *            钱包系统卡ID
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_hosting_withdraw(String out_trade_no, String summary, String identity_id,
			String identity_type, String account_type, BigDecimal amount, BigDecimal user_fee, String card_id,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_hosting_withdraw;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;
		String tmpNotifyurl = Sinapay_callbackurl;
		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("notify_url", tmpNotifyurl);
		tmprequestmap.put("out_trade_no", out_trade_no);
		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}
		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);
		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		if (!user_fee.equals("")) {
			tmprequestmap.put("user_fee", SafeUtils.formatCurrency(user_fee, 2));
		}

		if (!card_id.equals("")) {
			tmprequestmap.put("card_id", card_id);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.13 托管提现查询
	 * 
	 * @param identity_id
	 *            用户标识信息（VIP号）
	 * @param identity_type
	 *            用户标识类型（UID）
	 * @param account_type
	 *            账户类型（基本户、保证金户、存钱罐）
	 * @param out_trade_no
	 *            交易订单号
	 * @param start_time
	 *            开始时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param end_time
	 *            结束时间格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param page_no
	 *            页号
	 * @param page_size
	 *            每页大小
	 * @return
	 */
	public SinaRspResult query_hosting_withdraw(String identity_id, String identity_type, String account_type,
			String out_trade_no, String start_time, String end_time, String extend_param, String page_no,
			String page_size) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_query_hosting_withdraw;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("identity_id", identity_id);
		tmprequestmap.put("identity_type", identity_type);

		if (!account_type.equals("")) {
			tmprequestmap.put("account_type", account_type);
		}
		if (!out_trade_no.equals("")) {
			tmprequestmap.put("out_trade_no", out_trade_no);
		}
		if (!start_time.equals("")) {
			tmprequestmap.put("start_time", start_time);
		}
		if (!end_time.equals("")) {
			tmprequestmap.put("end_time", end_time);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		tmprequestmap.put("page_no", page_no);
		tmprequestmap.put("page_size", page_size);

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/***
	 * 3.14 转账接口
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param payer_identity_id
	 *            商户系统用户ID(字母或数字)
	 * @param payer_identity_type
	 *            ID的类型，参考“标志类型：UID
	 * @param payer_account_type
	 *            付款人账户类型 账户类型（基本户、保证金户）。默认基本户，见附录
	 * @param payee_identity_id
	 *            收款人标识
	 * @param payee_identity_type
	 *            收款人标识类型：UID
	 * @param payee_account_type
	 *            收款人账户类型 账户类型（基本户、保证金户）。默认基本户，见附录
	 * @param amount
	 *            金额
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_hosting_transfer(String out_trade_no, String payer_identity_id,
			String payer_identity_type, String payer_account_type, String payee_identity_id, String payee_identity_type,
			String payee_account_type, BigDecimal amount, String summary, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_create_hosting_transfer;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("out_trade_no", out_trade_no);
		tmprequestmap.put("payer_identity_id", payer_identity_id);
		tmprequestmap.put("payer_identity_type", payer_identity_type);

		if (!payer_account_type.equals("")) {
			tmprequestmap.put("payer_account_type", payer_account_type);
		}
		tmprequestmap.put("payee_identity_id", payee_identity_id);
		tmprequestmap.put("payee_identity_type", payee_identity_type);
		if (!payee_account_type.equals("")) {
			tmprequestmap.put("payee_account_type", payee_account_type);
		}

		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}

		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.15 支付推进
	 * 
	 * @param out_advance_no
	 *            支付推进请求号
	 * @param ticket
	 *            绑卡时返回的ticket
	 * @param validate_code
	 *            短信验证码
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult advance_hosting_pay(String out_advance_no, String ticket, String validate_code,
			String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;
		String tmpCmdId = SinaConsts.Service_advance_hosting_pay;
		String tmpNotifyurl = Sinapay_callbackurl;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("notify_url", tmpNotifyurl);
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("out_advance_no", out_advance_no);
		tmprequestmap.put("ticket", ticket);
		tmprequestmap.put("validate_code", validate_code);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.16 标的录入
	 * 
	 * @param goods_id
	 *            商户标的号
	 * @param goods_name
	 *            标的名称
	 * @param annual_yield
	 *            年化收益率
	 * @param term
	 *            还款期限yyyyMMddHHmmss
	 * @param repay_method
	 *            还款方式 DEBT_MATURITY --到期还本付息MONTHLY_PAYMENT --按月付息 到期还本
	 * @param guarantee_method
	 *            担保方式 企业担保；Xx保险担保；银行担保
	 * @param debtor_list
	 *            借款人集合 详见“集合参数”。参数间用“^”分隔，各条目之间用“$”分隔
	 * @param investor_list
	 *            投资人集合
	 * @param total_amount
	 *            标的总金额
	 * @param begin_date
	 *            标的开始时间
	 * @param url
	 *            标的url
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_p2p_hosting_borrowing_target(String goods_id, String goods_name, String annual_yield,
			String term, String repay_method, String guarantee_method, String debtor_list, String investor_list,
			BigDecimal total_amount, String begin_date, String url, String summary, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;

		String tmpCmdId = SinaConsts.Service_create_p2p_hosting_borrowing_target;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("goods_id", goods_id);
		tmprequestmap.put("goods_name", goods_name);
		tmprequestmap.put("annual_yield", annual_yield);
		tmprequestmap.put("term", term);
		tmprequestmap.put("repay_method", repay_method);
		tmprequestmap.put("guarantee_method", guarantee_method);
		tmprequestmap.put("debtor_list", debtor_list);
		if (!investor_list.equals("")) {
			tmprequestmap.put("summary", investor_list);
		}
		tmprequestmap.put("total_amount", SafeUtils.formatCurrency(total_amount, 2));
		tmprequestmap.put("begin_date", begin_date);
		tmprequestmap.put("url", url);
		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.17 创建单笔代付到提现卡交易
	 * 
	 * @param out_trade_no
	 *            交易订单号
	 * @param out_trade_code
	 *            交易码
	 * @param collect_method
	 *            收款方式 格式：收款方式^扩展。扩展信息内容以“，”分隔，收款方式及扩展详见附录
	 * @param amount
	 *            金额
	 * @param summary
	 *            摘要
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @param goods_id
	 *            对应“标的录入”接口中的标的号，用来关联此笔代收和标的
	 * @return
	 */
	public SinaRspResult create_single_hosting_pay_to_card_trade(String out_trade_no, String out_trade_code,
			String collect_method, BigDecimal amount, String summary, String extend_param, String goods_id) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;

		String tmpCmdId = SinaConsts.Service_create_single_hosting_pay_to_card_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("out_trade_no", out_trade_no);
		tmprequestmap.put("out_trade_code", out_trade_code);
		tmprequestmap.put("collect_method", collect_method);

		tmprequestmap.put("amount", SafeUtils.formatCurrency(amount, 2));
		if (!summary.equals("")) {
			tmprequestmap.put("summary", summary);
		}
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		if (!goods_id.equals("")) {
			tmprequestmap.put("goods_id", goods_id);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 3.18 创建批量代付到提现卡交易
	 * 
	 * @param out_pay_no支付请求号
	 * @param out_trade_code
	 *            交易码
	 * @param trade_list
	 *            交易列表 详见“交易参数”。参数间用“~”分隔，各条目之间用“$”分隔，备注信息不要包含特殊分隔符
	 * @param notify_method
	 *            通知方式 取值范围： single_notify, batch_notify single_notify: 交易逐笔通知
	 *            batch_notify: 批量通知 详见附录”通知方式” 默认值为single_notify
	 * @param extend_param
	 *            扩展信息（参数格式：参数名1^参数值1|参数名2^参数值2|）
	 * @return
	 */
	public SinaRspResult create_batch_hosting_pay_to_card_trade(String out_pay_no, String out_trade_code,
			String trade_list, String notify_method, String extend_param) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = Sinapay_mas_gateway;

		String version = Sinapay_default_version;

		String tmpCmdId = SinaConsts.Service_create_batch_hosting_pay_to_card_trade;
		String tmpPartnerId = Sinapay_partner_id;
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = Sinapay_sign_type;

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time", SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);
		tmprequestmap.put("out_pay_no", out_pay_no);
		tmprequestmap.put("out_trade_code", out_trade_code);
		if (!extend_param.equals("")) {
			tmprequestmap.put("extend_param", extend_param);
		}
		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(Sinapay_sign_type);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams), tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code").toString());
			result.setResultDesc(tmpresultcontent.get("response_message").toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	/**
	 * 5.1 存钱罐基金收益率查询
	 * 
	 * @param version
	 * @param fund_code
	 */
	public SinaRspResult query_fund_yield(String version, String fund_code) {
		SinaRspResult result = new SinaRspResult();
		Map<String, String> tmprequestmap = new HashMap<String, String>();
		String tmpInterfaceUrl = SystemProperties.get("sinapay_mas_gateway");
		if (version == null || "".equals(version))
			version = "1.0";
		String tmpCmdId = SinaConsts.Service_query_fund_yield;
		String tmpPartnerId = SystemProperties.get("sinapay_partner_id");
		String tmpCharset = SinaConsts.CHARSET;
		String tmpSignType = SystemProperties.get("sinapay_sign_type");

		tmprequestmap.put("version", version);
		tmprequestmap.put("service", tmpCmdId);
		tmprequestmap.put("request_time",
				SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss"));
		tmprequestmap.put("partner_id", tmpPartnerId);
		tmprequestmap.put("_input_charset", tmpCharset);

		tmprequestmap.put("fund_code", fund_code);

		String content = Tools.createLinkString(tmprequestmap, false, true);

		String signKey = getSignKey(tmpSignType);
		String tmpSign = "";
		try {
			tmpSign = SignUtil.sign(content, tmpSignType, signKey, tmpCharset);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		tmprequestmap.put("sign_version", version);
		tmprequestmap.put("sign_type", tmpSignType);
		tmprequestmap.put("sign", tmpSign);

		String tmpparams = Tools.createLinkString(tmprequestmap, true, false);
		log.info("Sina Request:" + tmpparams);
		try {
			String tmpResult = URLDecoder.decode(
					CallServiceUtil.sendPost(tmpInterfaceUrl, tmpparams),
					tmpCharset);
			log.info("Sina Response:" + tmpResult);
			Map<String, String> tmpresultcontent = GsonUtil
					.fronJson2Map(tmpResult);
			result.setResultCode(tmpresultcontent.get("response_code")
					.toString());
			result.setResultDesc(tmpresultcontent.get("response_message")
					.toString());
			result.setResultMap(tmpresultcontent);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			result = null;
		}
		return result;
	}

	public String getSignKey(String signType) {
		String signKey = "";
		if ("MD5".equalsIgnoreCase(signType)) {
			signKey = SystemProperties.get("sina_sign_md5");
		} else {
			signKey = SystemProperties.get("sina_sign_nomd5");

		}
		return signKey;
	}

	/**
	 * 公钥内容解密用的秘钥
	 * 
	 * @return
	 */
	public String getPublicKey(String signType) {
		String signKey = "";
		if ("MD5".equalsIgnoreCase(signType)) {
			signKey = SystemProperties.get("sina_sign_md5");
		} else {
			signKey = SystemProperties.get("sina_rsa_public");
		}
		return signKey;
	}

}
