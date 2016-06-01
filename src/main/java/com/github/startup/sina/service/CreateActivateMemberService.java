package com.github.startup.sina.service;

import baojinsuo.sina.SinaHelper;
import baojinsuo.sina.model.request.CreateActivateMemberRequestModel;
import baojinsuo.sina.model.response.BaseResponseModel;
import baojinsuo.sina.utils.CallServiceUtil;
import baojinsuo.systemconfig.SystemProperties;
import org.apache.commons.collections.BeanMap;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by bresai on 16/5/31.
 */
public class CreateActivateMemberService {
    private Logger log = Logger.getLogger(SinaHelper.class);

    private static String Sinapay_mas_gateway = SystemProperties.get("sinapay_mas_gateway");
    private static String Sina_encryptkey = SystemProperties.get("sina_encryptkey");
    private static String Sinapay_mgs_gateway = SystemProperties.get("sinapay_mgs_gateway");

    private Map modelToMap(Object myBean){
        return new BeanMap(myBean);
    }

    public BaseResponseModel.CreateActivateMemberResponseModel create_activate_member(String identity_id, String identity_type, String member_type,
                                                                                      String extend_param) {
        CreateActivateMemberRequestModel createActivateMemberRequestModel =
                new CreateActivateMemberRequestModel(
                        identity_id,
                        identity_type,
                        member_type,
                        extend_param
                );
        try {
            String content = createActivateMemberRequestModel.setSign(modelToMap(createActivateMemberRequestModel));
            String result = URLDecoder.decode(
                    CallServiceUtil.sendPost(Sinapay_mgs_gateway, content),
                    createActivateMemberRequestModel.get_input_charset());
            log.info("Sina Response:" + result);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(result, BaseResponseModel.CreateActivateMemberResponseModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
