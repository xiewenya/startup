package com.github.startup.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by bresai on 16/5/24.
 */
public interface BaseService {

    Iterable list(HttpServletRequest request) throws JsonProcessingException;

    String retrieve(Long id, HttpServletRequest request);

    String create(Object data, HttpServletRequest request);

    String update(Long id, Object data, HttpServletRequest request);

    String delete(Long id, HttpServletRequest request);

}
