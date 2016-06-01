package com.github.startup.sina;

import java.util.Map;

public class SinaRspResult {
	private String resultCode = "";
	private String resultDesc = "";
	private Map<String,String> resultMap = null;
	private String webStr = "";
	
	public String getWebStr() {
		return webStr;
	}
	public void setWebStr(String webStr) {
		this.webStr = webStr;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	public Map<String, String> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, String> resultMap) {
		this.resultMap = resultMap;
	}
	
}