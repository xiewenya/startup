package com.github.startup.systemconfig;

public class Consts {
	
	public final static int  SUCCESS = 0;
	public final static int FAIL = -1;
	
	public final static int PRODUCTFAQTYPE_PM=0;//bjsportal项目信息，标题+详情+图片
	public final static int PRODUCTFAQTYPE_RM=1;//bjsportal保理信息，标题+详情+图片
	public final static int PRODUCTFAQTYPE_FM=2;//bjsportal风险信息，标题+详情
	
	
	public final static int PASSWORD_MINLEN=6;//密码限制至少6位
	public final static int SINA_PASSWORD_MINLEN=6;
	public final static int FUNDORGID_HUIFU=1;
	public final static int FUNDORGID_SINA=2;
	
	public final static String CUSTIDTYPE_PKID="PKID";
	public final static String CUSTIDTYPE_CODE="CODE";
	public final static String CUSTIDTYPE_FUNDACCT="FUNDACCT";
	
	public final static String CUSTTYPE_AUTO="";
	public final static String CUSTTYPE_CUSTOMER="CUSTOMER";
	public final static String CUSTTYPE_CUSTOMER_SINA="CUSTOMER_SINA";
	public final static String CUSTTYPE_SELLER="SELLER";
	public final static String CUSTTYPE_DEBTOR="DEBTOR";
	public final static String CUSTTYPE_BORROWER="BORROWER";
	public final static String CUSTTYPE_GUARANTOR="GUARANTOR";
	public final static String CUSTTYPE_CORPORATE="CORPORATE";
	
	public final static String OPERATETYPE_INSERT="INSERT";
	public final static String OPERATETYPE_UPDATE="UPDATE";
	
	public final static String ACTION_REGISTER="REGISTER";
	public final static String ACTION_LOGIN="LOGIN";
	
	public final static String ACTION_FUNDREGISTER="FUNDREGISTER";
	public final static String ACTION_RECHARGE="RECHARGE";
	public final static String ACTION_WITHDRAW="WITHDRAW";
	public final static String ACTION_INITIATIVETENDER="INITIATIVETENDER";
	public final static String ACTION_CONFIRMTENDER="CONFIRMTENDER";
	public final static String ACTION_BINDBANKCARD="BIND_BANKCARD";
	public final static String ACTION_UNBINDBANKCARD="UNBIND_BANKCARD";
	
	public final static String ACTION_INVITE="INVITE";
	public final static String ACTION_ACCEPTINVITE="ACCEPTINVITE";

	
	//充值时资金记录的客户类型
	public final static int FINANCIALRECORD_CUSTOMER=0;
	public final static int FINANCIALRECORD_SELLER=1;
	public final static int FINANCIALRECORD_COMPANY=5;
	public final static int FINANCIALRECORD_PLATFORM=99;
	//充值时资金记录的类型
	public final static int FINANCIALRECORD_FINANCIALTYPE=4;
	//充值时的网关业务代号
	public final static String GATEBUSIID_ONLINE="网银 online_bank";
	public final static String GATEBUSIID_BINDING="快捷 binding_pay";
	//充值时的手续费免除
	public final static int GATEBUSIID_FeeAmt=0;
	//充值时的服务类型
	public final static int SERVICETYPE_DEPOSIT=3;
	//充值时异步消息表的消息类型
	public final static String RESPFORMAT="JSON";
	
	//资金记录的类型：提现
	public final static int FINANCIALRECORD_FINANCIALTYPE_CARD=6;
	//提现时的服务类型
	public final static int SERVICETYPE_CARD=4;
	
	//资金记录的类型：活期购买
	public final static int FINANCIALRECORD_CURRENT_PAY=6;
	//资金记录的类型：收款
	public final static int FINANCIALRECORD_FINANCIALTYPE_COLLECTION=1;
	//资金记录的类型：付款
	public final static int FINANCIALRECORD_FINANCIALTYPE_PAY=2;
	//仓单宝 ：活期购买
	public final static int FINANCIALRECORD__PAY=5;
	//仓单宝 ：活期赎回
	public final static int FINANCIALRECORD__Ransom=6;
	
	//投资的投资方式
	public final static int INVESTTYPE_CUSTOMER = 0;
	public final static int INVESTTYPE_AUTO = 1;
	
	public final static int REQUESTSTATUS_SENT=0;
	public final static int REQUESTSTATUS_GETRSP=0;
	
	public final static int ETYPE_SELLER=1;//销售商类型
	public final static int ETYPE_BORROWER=2;//借款人
	public final static int ETYPE_DEBTOR=3;//债务人
	public final static int ETYPE_GUARANTOR=4;//担保人
	//积分规则状态
	public final static int SCORERULE_STATUS_INVALID=0;
	public final static int SCORERULE_STATUS_NORMAL=1;
	
	//会员类型
	public final static String MEMBERCLASSTYPE_ENTERPRISE="ENTERPRISE";
	public final static String MEMBERCLASSTYPE_GUARANTEE_CORP="GUARANTEE_CORP";
	//:待验证;1:正常;2:暂停;3:注销;
	public final static int CUSTOMER_STATUS_WAITFORVERIFY=0;
	public final static int CUSTOMER_STATUS_NORMAL=1;
	public final static int CUSTOMER_STATUS_PAUSE=2;
	public final static int CUSTOMER_STATUS_REVOKED=3;
	
	public final static int BANK_STATUS_INVALID=0;
	public final static int BANK_STATUS_NORMAL=1;
	
	//客户绑定银行卡状态 0:待确认;1:已确认;2:已取消;
	public final static int CUSTOMERBANK_STATUS_WAITFORCONFIRM=0;
	public final static int CUSTOMERBANK_STATUS_OK=1;
	public final static int CUSTOMERBANK_STATUS_CANCEL=2;
	
	
	
	//验证码状态 0:待验证;1:已验证;2:已过期;
	public final static int VERIFYCODE_STATUS_WAITFORVERIFY=0;
	public final static int VERIFYCODE_STATUS_VERIFIED=1;
	public final static int VERIFYCODE_STATUS_EXPIRE=2;
	
	//验证码类型 1:注册验证码;2:找回密码验证码;3:交易验证码;
	public final static int VCTYPE_REGISTER=1;
	public final static int VCTYPE_GETPASSWORD=2;
	public final static int VCTYPE_TRANSACTION=3;
	
	//产品状态 0:创建中;1:待审核;2:已审核;3:待发布;4:已发布;5:已到期;6:已撤销;
	public final static int PRODUCT_STATUS_INIT=0;
	public final static int PRODUCT_STATUS_PUBLISHED=4;
	public final static int PRODUCT_STATUS_EXPIRED=5;
	public final static int PRODUCT_STATUS_REVOKED=6;

	//投诉与建议
	public final static int COMPLAIN_STATUS_NEW=0;
	
	public final static int RESULT_SUCCESS=0;
	
	public final static int RESULT_CURRENTPAY_SUCCESS=4;
	
	public final static int RESULT_CURRENTRANSOM_SUCCESS=2;
	
	
	public final static int RESULT_SYSTEMERROR=500;
	
	public final static int RESULT_CUSTOMER_EXISTS=600;
	public final static int RESULT_CUSTOMER_EMPTYLOGINNAME=601;
	public final static int RESULT_CUSTOMER_VC_EXPIRE=602;
	public final static int RESULT_CUSTOMER_VC_NOTEXISTS=603;
	public final static int RESULT_CUSTOMER_NOTEXISTS=604;
	public final static int RESULT_CUSTOMER_PWDERROR=604;
	public final static int RESULT_CUSTOMER_PWDTOOSIMPLE=604;
	public final static int RESULT_CUSTOMER_STATUSINVALID=605;
	
	public final static int RESULT_CUSTOMER_BANKCARDBINDALREADY=605;
	public final static int RESULT_CUSTOMER_FUNDACCT_NOTEXIST=606;
	
	public final static int RESULT_PRODUCT_NOTEXIST=620;
	public final static int RESULT_PRODUCT_NOTINPUBLISH=621;
	
	public final static int RESULT_ORDER_AMOUNTUPLIMIT=630;//金额到了上限
	
	public final static int RESULT_ENTERPISE_NOTEXIST=650;//不存在
	
	public final static int RESULT_YEEPAY_CBRSTFMTERROR=660;//返回格式不正确
	public final static int RESULT_YEEPAY_SIGNERROR=661;//签名验证失败
	
	public final static int RESULT_ORDERPRODUCT_LOWLIMIT=670;
	public final static int RESULT_ORDERPRODUCT_HIGHLIMIT=671;
	public final static int RESULT_ORDERPRODUCT_REMAINLIMIT=672;
	public final static int RESULT_ORDERPRODUCT_FUNCDISABLE=673;//系统禁用
	
	
}
