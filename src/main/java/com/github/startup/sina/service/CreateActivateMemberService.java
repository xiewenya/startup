package com.github.startup.sina.service;

import com.github.startup.sina.model.request.CreateActivateMemberRequestModel;
import com.github.startup.sina.model.response.CreateActivateMemberResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bresai on 16/6/2.
 */
@Service
public class CreateActivateMemberService
        extends MemberService<CreateActivateMemberRequestModel,
        CreateActivateMemberResponseModel>{

    private CreateActivateMemberRequestModel request;

    private CreateActivateMemberResponseModel response;

    @Autowired
    public void setRequest(CreateActivateMemberRequestModel request) {
        this.request = request;
    }

    public CreateActivateMemberRequestModel getRequest() {
        return request;
    }

    public CreateActivateMemberResponseModel getResponse() {
        return response;
    }

    @Autowired
    public void setResponse(CreateActivateMemberResponseModel response) {
        this.response = response;
    }

    //    @Autowired
//    public CreateActivateMemberRequestModel setMember_type(MemberRequestModel.Builder builder, String member_type) {
//        return (CreateActivateMemberRequestModel) builder.extend_param(member_type).build();
//    }

}
