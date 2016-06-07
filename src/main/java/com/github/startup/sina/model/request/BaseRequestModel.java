package com.github.startup.sina.model.request;

import com.github.startup.sina.model.BaseSinaModel;
import com.github.startup.sina.utils.SafeUtils;
import com.github.startup.sina.utils.SinaConsts;
import com.github.startup.systemconfig.SystemProperties;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/5/31.
 */

public class BaseRequestModel extends BaseSinaModel {
    @NotNull
    @Size(max = 20)
    private String service;

    @NotNull
    @Size(max = 5)
    private String version;

    @NotNull
    @Size(max = 32)
    private String partner_id;

    @NotNull
    @Size(max = 14)
    private String request_time;

    @Size(max = 5)
    private String encrypt_version;

    @Size(max = 200)
    private String notify_url;

    @Size(max = 200)
    private String return_url;

    BaseRequestModel() {
        super();
        this.version = SinaConsts.VERSION;
        this.partner_id = SystemProperties.get("sinapay_partner_id");
        this.encrypt_version = SinaConsts.ENCRYPT_VERSION;
        this.request_time = SafeUtils.getCurrentTimeStr("yyyyMMddHHmmss");
        this.notify_url = SystemProperties.get("sinapay_callbackurl");
        this.return_url= SystemProperties.get("sinapay_returnurl");
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public String getRequest_time() {
        return request_time;
    }

    public String getEncrypt_version() {
        return encrypt_version;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }
}
