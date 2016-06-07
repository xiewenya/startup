package com.github.startup.sina.model;

import com.github.startup.sina.Tools;
import com.github.startup.sina.utils.SignUtil;
import com.github.startup.sina.utils.SinaConsts;
import com.github.startup.systemconfig.SystemProperties;

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
    private String sign;

    @NotNull
    @Size(max = 10)
    private String sign_type;

    @Size(max = 5)
    private String sign_version;

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

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void generateSign(Map map) throws Exception{
        String content = Tools.createLinkString(map, false, true);

        this.sign = SignUtil.sign(content, sign_type,
                getSignKey(sign_type), _input_charset);
    }
    public void set_input_charset(String _input_charset) {
        this._input_charset = _input_charset;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public void setSign_version(String sign_version) {
        this.sign_version = sign_version;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getSign_version() {
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
