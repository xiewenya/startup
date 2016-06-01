package com.github.startup.rest_base;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bresai on 16/5/24.
 */
public interface Retrieve {
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity retrieve(@PathVariable long id, HttpServletRequest request) throws JsonProcessingException;

}
