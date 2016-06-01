package com.github.startup.common.controller;

import com.github.startup.common.mixin.BaseMixin;
import com.github.startup.common.model.BaseModel;
import com.github.startup.common.serializer.BaseSerializer;
import com.github.startup.common.service.BaseServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bresai on 16/5/25.
 */

public abstract class BaseController<M extends BaseModel, I extends BaseMixin> {

    private BaseSerializer<M, I> serializer;

    private BaseServiceImpl<M, Long> service;

    @Autowired
    public void setService(BaseServiceImpl<M, Long> service) {
        this.service = service;
    }

    @Autowired
    public void setSerializer(BaseSerializer<M, I> serializer) {
        this.serializer = serializer;
    }

    //always explicit declare request methods
    @RequestMapping(method = RequestMethod.GET)
    //always return ResponseEntity or HttpEntity
    public ResponseEntity<String> list(
            //always include HttpServletRequest for generic design
            HttpServletRequest request
    ) throws JsonProcessingException {
        Iterable querySet = service.list(request);
        String serialized = serializer.to_represent(querySet);
        return new ResponseEntity<>(serialized, HttpStatus.OK);

    }
}
