package com.github.startup.sina.model.request;

import baojinsuo.sina.model.BaseSinaModel;
import baojinsuo.sina.utils.SinaConsts;
import baojinsuo.systemconfig.SystemProperties;
import baojinsuo.utils.SafeUtils;

import javax.validation.constraints.Max;
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
    @Max(99999)
    private Number version;

    @NotNull
    @Size(max = 32)
    private String partner_id;

    @NotNull
    @Size(max = 14)
    private String request_time;

    @Max(99999)
    private Number encrypt_version;

    @Size(max = 200)
    private String notify_url;

    @Size(max = 200)
    private String return_url;

    public BaseRequestModel(@NotNull String service) {
        super();
        this.service = service;
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

    public Number getVersion() {
        return version;
    }

    public String getPartner_id() {
        return partner_id;
    }

    public String getRequest_time() {
        return request_time;
    }

    public Number getEncrypt_version() {
        return encrypt_version;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }
}
