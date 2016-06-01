package com.github.startup.sina.model.request;

import baojinsuo.sina.utils.SinaConsts;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/5/31.
 */
public class CreateActivateMemberRequestModel extends BaseRequestModel {
    @NotNull
    @Size(max = 50)
    private String identity_id;

    @NotNull
    @Size(max = 16)
    private String identity_type;

    @Size(max = 1)
    private String member_type;

    @Size(max = 200)
    private String extend_param;

    public CreateActivateMemberRequestModel() {
        super(SinaConsts.Service_create_activate_member);
    }

    public CreateActivateMemberRequestModel(String identity_id, String identity_type) {
        this();
        this.identity_id = identity_id;
        this.identity_type = identity_type;
    }

    public CreateActivateMemberRequestModel(String identity_id, String identity_type, String member_type) {
        this();
        this.identity_id = identity_id;
        this.identity_type = identity_type;
        this.member_type = member_type;
    }

    public CreateActivateMemberRequestModel(String identity_id, String identity_type, String member_type, String extend_param) {
        this();
        this.identity_id = identity_id;
        this.identity_type = identity_type;
        this.member_type = member_type;
        this.extend_param = extend_param;
    }

    public String getIdentity_id() {
        return identity_id;
    }

    public void setIdentity_id(String identity_id) {
        this.identity_id = identity_id;
    }

    public String getIdentity_type() {
        return identity_type;
    }

    public void setIdentity_type(String identity_type) {
        this.identity_type = identity_type;
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public String getExtend_param() {
        return extend_param;
    }

    public void setExtend_param(String extend_param) {
        this.extend_param = extend_param;
    }
}
