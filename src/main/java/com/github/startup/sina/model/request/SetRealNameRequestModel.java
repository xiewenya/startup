package com.github.startup.sina.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.startup.sina.utils.SinaConsts;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/6/2.
 */

@Component
//ignore null properties when serializing
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SetRealNameRequestModel extends MemberRequestModel{

    @NotNull
    @Size(max = 50)
    private String real_name;

    @NotNull
    @Size(max = 18)
    private String cert_type;

    @NotNull
    @Size(max = 18)
    private String cert_no;

    @NotNull
    @Size(max = 1)
    private String need_confirm;

    public SetRealNameRequestModel() {
        super();
        this.setService(SinaConsts.Service_set_real_name);
    }

    public void setUserInfo(String real_name, String cert_type, String cert_no, String need_confirm) {
        this.real_name = real_name;
        this.cert_no = cert_no;
        this.cert_type = cert_type;
        this.need_confirm = need_confirm;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getCert_type() {
        return cert_type;
    }

    public void setCert_type(String cert_type) {
        this.cert_type = cert_type;
    }

    public String getCert_no() {
        return cert_no;
    }

    public void setCert_no(String cert_no) {
        this.cert_no = cert_no;
    }

    public String getNeed_confirm() {
        return need_confirm;
    }

    public void setNeed_confirm(String need_confirm) {
        this.need_confirm = need_confirm;
    }
}
