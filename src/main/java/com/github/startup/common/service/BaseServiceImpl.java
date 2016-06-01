package com.github.startup.common.service;

import com.github.startup.common.model.BaseModel;
import com.github.startup.common.repository.BaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by bresai on 16/5/24.
 */

@Service
public abstract class BaseServiceImpl<
        M extends BaseModel, ID extends Serializable>
        implements BaseService{

    protected BaseRepository<M,ID> repository;

    @Autowired
    public void setRepository(BaseRepository<M, ID> repository) {
        this.repository = repository;
    }

    private Iterable getQuerySet(HttpServletRequest request) {
        return repository.findAll();
    }

    public Object getObject(ID id) {
        return repository.findOne(id);
    }

//    private Object deserializer(String json) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(item.getClass(), deserializerClass);
//        mapper.registerModule(module);
//
//        return mapper.readValue(json, item.getClass());
//    }

    @Override
    public Iterable list(HttpServletRequest request) throws JsonProcessingException {
        Iterable querySet = getQuerySet(request);
//        TODO: add filter function to querySet
//        TODO: add pagination function to querySet
        return querySet;
//        return null;
    }

    @Override
    public String retrieve(Long id, HttpServletRequest request) {
        return null;
    }

    @Override
    public String create(Object data, HttpServletRequest request) {
        return null;
    }

    @Override
    public String update(Long id, Object data, HttpServletRequest request) {
        return null;
    }

    @Override
    public String delete(Long id, HttpServletRequest request) {
        return null;
    }

}
