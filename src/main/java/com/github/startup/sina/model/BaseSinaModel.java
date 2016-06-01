package com.github.startup.sina.model;

import baojinsuo.sina.Tools;
import baojinsuo.sina.utils.SignUtil;
import baojinsuo.sina.utils.SinaConsts;
import baojinsuo.systemconfig.SystemProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * Created by bresai on 16/5/31.
 */
public class BaseSinaModel {

    @NotNull
    @Size(max = 10)
    private String _input_charset;

    @NotNull
    @Size(max = 256)
    private String sign;

    @NotNull
    @Size(max = 10)
    private String sign_type;

    @Max(99999)
    private Number sign_version;

    @Size(max = 1000)
    private String memo;

    public BaseSinaModel() {
        this._input_charset = SinaConsts.CHARSET;
        this.sign_version = SinaConsts.SIGN_VERSION;
        this.sign_type = SystemProperties.get("sinapay_sign_type");
    }

    public String get_input_charset() {
        return _input_charset;
    }

    public String getSign() {
        return sign;
    }

    public String setSign(Map<String, String> map) throws Exception{
        String content = Tools.createLinkString(map, false, true);

        this.sign = SignUtil.sign(content, sign_type,
                getSignKey(sign_type), _input_charset);
        return content;
    }

    public String getSign_type() {
        return sign_type;
    }

    public Number getSign_version() {
        return sign_version;
    }

    public String getMemo() {
        return memo;
    }

    public String getSignKey(String signType) {
        String signKey;
        if ("MD5".equalsIgnoreCase(signType)) {
            signKey = SystemProperties.get("sina_sign_md5");
        } else {
            signKey = SystemProperties.get("sina_sign_nomd5");

        }
        return signKey;
    }

}
