package com.github.startup.sina.model.response;

import com.github.startup.sina.model.BaseSinaModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bresai on 16/5/31.
 */

public class BaseResponseModel extends BaseSinaModel {

    @Size(max = 32)
    private String partner_id;

    @NotNull
    @Size(max = 14)
    private String response_time;

    @NotNull
    @Size(max = 30)
    private String response_code;

    @Size(max = 200)
    private String response_message;

    public String getPartner_id() {
        return partner_id;
    }

    public String getResponse_time() {
        return response_time;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public BaseResponseModel() {
        super();
    }

    @Override
    public String toString() {
        return "BaseResponseModel{" +
                "partner_id='" + partner_id + '\'' +
                ", response_time='" + response_time + '\'' +
                ", response_code='" + response_code + '\'' +
                ", response_message='" + response_message + '\'' +
                '}';
    }
}
