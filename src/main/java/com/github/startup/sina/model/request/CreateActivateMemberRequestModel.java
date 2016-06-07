package com.github.startup.sina.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.startup.sina.utils.SinaConsts;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/5/31.
 */
@Component
//ignore null properties when serializing
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateActivateMemberRequestModel extends MemberRequestModel {

    @Size(max = 1)
    private String member_type;

    public CreateActivateMemberRequestModel() {
        super();
        this.setService(SinaConsts.Service_create_activate_member);
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    @Override
    public String toString() {
        return "CreateActivateMemberRequestModel{" +
                "member_type='" + member_type + '\'' +
                '}';
    }
}
