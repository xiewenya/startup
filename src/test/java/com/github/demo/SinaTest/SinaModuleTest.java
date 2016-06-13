package com.github.demo.SinaTest;

import com.github.demo.StartUpApplicationTest;
import com.github.startup.sina.model.request.CreateActivateMemberRequestModel;
import com.github.startup.sina.model.request.SetRealNameRequestModel;
import com.github.startup.sina.model.response.CreateActivateMemberResponseModel;
import com.github.startup.sina.model.response.SetRealNameResponseModel;
import com.github.startup.sina.service.MemberServiceHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

/**
 * Created by bresai on 16/6/1.
 */
public class SinaModuleTest extends StartUpApplicationTest{

    @Autowired
    private MemberServiceHelper helper;

    @Test
    public void createMember() throws Exception {
        CreateActivateMemberRequestModel model =
                new CreateActivateMemberRequestModel();
        model.setIdentity("2412312312312", "UID");
        String result= helper.sendRequest(model);
        System.out.println(result);

        CreateActivateMemberResponseModel response = helper.<CreateActivateMemberResponseModel>getResult(result);

        System.out.println(response);
        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
    }

//    @Test
//    public void testResultToResponse() throws IOException {
//        String result = "{\"response_time\":\"20160606182118\",\"partner_id\":\"200004227922\",\"_input_charset\":\"UTF-8\",\"sign_type\":\"RSA\",\"sign_version\":\"1.0\",\"response_code\":\"DUPLICATE_IDENTITY_ID\",\"response_message\":\"用户标识信息重复\"}";
//        CreateActivateMemberResponseModel response = service.getResult(result);
//        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
//    }

    @Test
    public void setRealNameMember() throws Exception {
        SetRealNameRequestModel model =
                new SetRealNameRequestModel();
        model.setIdentity("2412312312312", "UID");
        model.setUserInfo("XAIDFJAASDF", "IC", "XAIDFJAASDF", "Y" );
        MemberServiceHelper service = new MemberServiceHelper();
        String result= service.sendRequest(model);
        System.out.println(result);

        SetRealNameResponseModel response = service.<SetRealNameResponseModel>getResult(result);

        System.out.println(response);
        assertTrue("DUPLICATE_IDENTITY_ID".equals(response.getResponse_code()));
    }
}
