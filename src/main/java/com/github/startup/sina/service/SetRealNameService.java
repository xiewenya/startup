package com.github.startup.sina.service;

import com.github.startup.sina.model.request.SetRealNameRequestModel;
import com.github.startup.sina.model.response.SetRealNameResponseModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bresai on 16/6/7.
 */
public class SetRealNameService extends MemberService<
        SetRealNameRequestModel, SetRealNameResponseModel>{

    private SetRealNameRequestModel request;

    private SetRealNameResponseModel response;

    @Autowired
    public void setRequest(SetRealNameRequestModel request) {
        this.request = request;
    }

    @Autowired
    public void setResponse(SetRealNameResponseModel response) {
        this.response = response;
    }
}
