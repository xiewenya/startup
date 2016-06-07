package com.github.demo.SinaTest;

import com.github.demo.StartUpApplicationTest;
import com.github.startup.sina.model.request.CreateActivateMemberRequestModel;
import com.github.startup.sina.model.request.SetRealNameRequestModel;
import com.github.startup.sina.model.response.CreateActivateMemberResponseModel;
import com.github.startup.sina.model.response.SetRealNameResponseModel;
import com.github.startup.sina.service.CreateActivateMemberService;
import com.github.startup.sina.service.SetRealNameService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by bresai on 16/6/1.
 */
public class SinaModuleTest extends StartUpApplicationTest{

    @Autowired
    private CreateActivateMemberService service;

    @Before
    public void setUp() {
        CreateActivateMemberRequestModel model =
                new CreateActivateMemberRequestModel();
        model.setIdentity("2412312312312", "UID");
//        model.setExtend_param("2342342");
        System.out.println(model);
        service.setRequest(model);
    }

    @Test
    public void createMember() throws Exception {
        String result= service.sendRequest(service.getRequest());
        System.out.println(result);

        CreateActivateMemberResponseModel response = service.getResult(result);

        System.out.println(response);
        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
    }

    @Test
    public void testResultToResponse() throws IOException {
        String result = "{\"response_time\":\"20160606182118\",\"partner_id\":\"200004227922\",\"_input_charset\":\"UTF-8\",\"sign_type\":\"RSA\",\"sign_version\":\"1.0\",\"response_code\":\"DUPLICATE_IDENTITY_ID\",\"response_message\":\"用户标识信息重复\"}";
        CreateActivateMemberResponseModel response = service.getResult(result);
        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
    }

    @Test
    public void setRealNameMember() throws Exception {
        SetRealNameRequestModel model =
                new SetRealNameRequestModel();
        model.setIdentity("2412312312312", "UID");
        model.setUserInfo("XAIDFJAASDF", "IC", "XAIDFJAASDF", "Y" );
        SetRealNameService service = new SetRealNameService();
        String result= service.sendRequest(model);
        System.out.println(result);

        SetRealNameResponseModel response = service.getResult(result);

        System.out.println(response);
        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
    }
}
