package com.github.startup.controllers;

import com.github.startup.common.controller.BaseController;
import com.github.startup.models.User;
import com.github.startup.serializers.UserMixin;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bresai on 16/5/23.
 */

@RestController
//define a resource path name according to model
@RequestMapping("/greeting")
public class UserController extends BaseController<User, UserMixin> {

    @Override
    @JsonView(UserController.class)
    public ResponseEntity list(HttpServletRequest request) throws JsonProcessingException {
//        String remoteAddress = request.getRemoteAddr();
//        request.getParameterMap();
////        Greeting greeting = new Greeting(, remoteAddress);
////        String test = new ObjectMapper().writeValueAsString(greeting);
//
////        return new ResponseEntity.ok(test);
//        return new ResponseEntity(remoteAddress, HttpStatus.OK);
        return super.list(request);
    }
//
//    @Override
//    public ResponseEntity retrieve(@PathVariable long id, HttpServletRequest request) throws JsonProcessingException {
//        return new ResponseEntity(HttpStatus.OK);
//    }
}