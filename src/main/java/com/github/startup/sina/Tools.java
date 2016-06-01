package com.github.startup.sina;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;


public class Tools {
	public static final String SIGN_VERSION_NAME = "sign_version";
	public static final String SIGN_TYPE_NAME = "sign_type";
	public static final String SIGN_NAME = "sign";

	public static String dumpMapString(Map<String, String> params) {
		StringBuffer tmpsb = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		String charset = params.get("_input_charset");
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			tmpsb.append(key + "=" + value + "\r\n");
		}
		return tmpsb.toString();
	}

	/**
	 * 
	 * @param params
	 * @param encode
	 * @param isFilter
	 * @return
	 */
	public static String createLinkString(Map<String, String> params,
			boolean encode, boolean isFilter) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		String charset = params.get("_input_charset");
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (encode) {
				try {
					value = URLEncoder.encode(
							URLEncoder.encode(value, charset), charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if (isFilter) {
				if (!key.equals("sign") && !key.equals("sign_type")
						&& !key.equals("sign_version")) {
					if (i == keys.size() - 1) {
						prestr = prestr + key + "=" + value;
					} else {
						prestr = prestr + key + "=" + value + "&";
					}
				}
			} else {
				if (i == keys.size() - 1) {
					prestr = prestr + key + "=" + value;
				} else {
					prestr = prestr + key + "=" + value + "&";
				}
			}
		}
		return prestr;
	}
	 public static String createLinkString(Map<String, String> params, boolean encode) {
	        List<String> keys = new ArrayList<String>(params.keySet());
	        Collections.sort(keys);
	        String prestr = "";
	        String charset = params.get("_input_charset");
	        for (int i = 0; i < keys.size(); i++) {
	            String key = keys.get(i);
	            String value = params.get(key);
	            if (encode) {
	                try {
	                    value =URLEncoder.encode(URLEncoder.encode(value, charset),charset);
	                } catch (UnsupportedEncodingException e) {
	                    e.printStackTrace();
	                }
	            }
	            if (i == keys.size() - 1) {// ����������������������&����
	                prestr = prestr + key + "=" + value;
	            } else {
	                prestr = prestr + key + "=" + value + "&";
	            }
	            }
	        
	        return prestr;
	    }
	/**
	 * request תmap
	 * 
	 * @param request
	 * @return
	 */
	public static Map getParameterMap(HttpServletRequest request,
			boolean isFilter) {
		Map properties = request.getParameterMap();
		Map returnMap = new HashMap();
		Iterator entries = properties.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			if (isFilter) {
				if (!name.equals("sign") && !name.equals("sign_type")
						&& !name.equals("sign_version")) {
					Object valueObj = entry.getValue();
					if (null == valueObj) {
						value = "";
					} else if (valueObj instanceof String[]) {
						String[] values = (String[]) valueObj;
						for (int i = 0; i < values.length; i++) {
							value = values[i] + ",";
						}
						value = value.substring(0, value.length() - 1);
					} else {
						value = valueObj.toString();
					}
					returnMap.put(name, value);
				}
			} else {
				Object valueObj = entry.getValue();
				if (null == valueObj) {
					value = "";
				} else if (valueObj instanceof String[]) {
					String[] values = (String[]) valueObj;
					for (int i = 0; i < values.length; i++) {
						value = values[i] + ",";
					}
					value = value.substring(0, value.length() - 1);
				} else {
					value = valueObj.toString();
				}
				returnMap.put(name, value);
			}
		}
		return returnMap;
	}
	 public static Map getParameterMap(HttpServletRequest request) {
	        // ����Map
	        Map properties = request.getParameterMap();
	        // ������Map
	        Map returnMap = new HashMap();
	        Iterator entries = properties.entrySet().iterator();
	        Map.Entry entry;
	        String name = "";
	        String value = "";
	        while (entries.hasNext()) {
	            entry = (Map.Entry) entries.next();
	            name = (String) entry.getKey();
	            if(!name.equals("sign")&&!name.equals("sign_type")&&!name.equals("sign_version")){
	            Object valueObj = entry.getValue();
	            if(null == valueObj){
	                value = "";
	            }else if(valueObj instanceof String[]){
	                String[] values = (String[])valueObj;
	                for(int i=0;i<values.length;i++){
	                    value = values[i] + ",";
	                }
	                value = value.substring(0, value.length()-1);
	            }else{
	                value = valueObj.toString();
	            }
	            returnMap.put(name, value);
	        }
	        }
	        return returnMap;
	    }
}
