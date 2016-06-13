package com.github.startup.sina.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.startup.common.utils.ReflectUtils;
import com.github.startup.sina.Tools;
import com.github.startup.sina.model.request.BaseRequestModel;
import com.github.startup.systemconfig.SystemProperties;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by bresai on 16/5/31.
 */
@Component
public class MemberServiceHelperImpl {
    private Logger log = Logger.getLogger(MemberServiceHelperImpl.class);
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;
    private static String Sinapay_mas_gateway = SystemProperties.get("sinapay_mas_gateway");
    private static String Sina_encryptkey = SystemProperties.get("sina_encryptkey");
    private static String Sinapay_mgs_gateway = SystemProperties.get("sinapay_mgs_gateway");

//    private T request;
    private Map modelToMap(Object myBean){
        return mapper.convertValue(myBean,Map.class);
    }

    public MemberServiceHelperImpl() {
        restTemplate = new RestTemplate();
        mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    public MemberRequestModel.Builder setRequest(String service, String identity_id, String identity_type) {
//        return new MemberRequestModel.Builder().
//                service(service).
//                identity(identity_id,identity_type);
//    }


//    public MemberRequestModel.Builder setExtra(MemberRequestModel.Builder builder, String extra) {
//        return builder.extend_param(extra);
//    }

//    public void addCreateActivateMemberParams(CreateActivateMemberRequestModel model, String member_type){
//        model.setMember_type(member_type);
//    }
//
//    public void addCreateActivateMemberParams(CreateActivateMemberRequestModel model, String member_type){
//        model.setMember_type(member_type);
//    }

    public String sendRequest(BaseRequestModel request){
        try {
            request.generateSign(modelToMap(request));
            Map map = modelToMap(request);
            String content = Tools.createLinkString(modelToMap(request), false, false);
            ResponseEntity<String> result = sendPost(content);
            HttpStatus responseCode = result.getStatusCode();
            if (responseCode.is2xxSuccessful()){
                log.info("Sina Response:" + result);
                return URLDecoder.decode(result.getBody());
            }
            else {

            }

        }
        catch (RestClientException e){
            //TODO: add notifications
            e.printStackTrace();
            System.out.println("Timeout or Connection error");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T getResult(String result) throws IOException {
        //ignore undefined properties in json
        if (result == null) return null;
        return mapper.readValue(result,
                ReflectUtils.findParameterizedType(this.getClass(),1));
    }

    private ResponseEntity<String> sendPost(String content){
        String url = Sinapay_mgs_gateway + "?" + content;
        return restTemplate.getForEntity(url, String.class);
    }
}
