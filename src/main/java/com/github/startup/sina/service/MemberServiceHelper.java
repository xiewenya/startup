package com.github.startup.sina.service;

import com.github.startup.sina.model.request.BaseRequestModel;

import java.io.IOException;

/**
 * Created by bresai on 16/5/31.
 */

public interface MemberServiceHelper {
    String sendRequest(BaseRequestModel request);

    <T> T getResult(String result) throws IOException;
}
