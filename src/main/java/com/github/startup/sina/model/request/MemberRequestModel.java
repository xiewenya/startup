package com.github.startup.sina.model.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/5/31.
 */
public class MemberRequestModel extends BaseRequestModel {
    @NotNull
    @Size(max = 50)
    private String identity_id;

    @NotNull
    @Size(max = 16)
    private String identity_type;

    @Size(max = 200)
    private String extend_param;

    MemberRequestModel() {
        super();
    }

    public void setIdentity(String identity_id, String identity_type) {
        this.identity_id = identity_id;
        this.identity_type = identity_type;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public String getIdentity_type() {
        return identity_type;
    }

    public String getExtend_param() {
        return extend_param;
    }

    public void setExtend_param(String extend_param) {
        this.extend_param = extend_param;
    }

}
