package com.github.startup.common.serializer;

import com.github.startup.common.mixin.BaseMixin;
import com.github.startup.common.model.BaseModel;
import com.github.startup.common.utils.ReflectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * Created by bresai on 16/5/25.
 */

@Component
public abstract class BaseSerializer<M extends BaseModel, I extends BaseMixin> {

    private BaseSerializerClass<M> baseSerializerClass;

    private BaseDeserializerClass<M> baseDeserializerClass;

    private String data;

    //converted from json data
    private M validatingModelInstance;

    //validated model, ready for save to database
    private M validatedModelInstance;

    private Class<M> baseModel;
    private Class<I> baseMixin;

    private ObjectMapper mapper;
    private SimpleModule module;

    private Validator validator;
    private Set<ConstraintViolation<M>> errors;

    @Autowired
    public void setBaseSerializerClass(BaseSerializerClass<M> baseSerializerClass) {
        this.baseSerializerClass = baseSerializerClass;
    }

    @Autowired
    public void setBaseDeserializerClass(BaseDeserializerClass<M> baseDeserializerClass) {
        this.baseDeserializerClass = baseDeserializerClass;
    }

    private void initSerializer(){
        this.errors = Collections.emptySet();

        this.baseModel = ReflectUtils.findParameterizedType(getClass(), 0);
        this.baseMixin = ReflectUtils.findParameterizedType(getClass(), 1);
        mapper = new ObjectMapper();
        module = new SimpleModule();
        module.setMixInAnnotation(baseModel.getClass(), baseMixin.getClass());

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public BaseSerializer(M instance, String data) {
        this.data = data;
        this.validatingModelInstance = instance;
        initSerializer();
    }

    public BaseSerializer(String data) {
        this(null, data);
    }

    public BaseSerializer(M instance) {
        this(instance, null);
    }

    public String to_represent(Iterable instances) throws JsonProcessingException {

        module.addSerializer(
                ReflectUtils.findParameterizedType(this.getClass(), 0),
                baseSerializerClass);
        mapper.registerModule(module);

        return mapper.writeValueAsString(instances);
    }

    private M to_internal_value(String data) throws IOException {
        module.addDeserializer(
                ReflectUtils.findParameterizedType(this.getClass(), 0),
                baseDeserializerClass);
        mapper.registerModule(module);

        return mapper.readValue(data, baseModel);
    }

    private Set<ConstraintViolation<M>> run_validation(M model){
        return validator.validate(model);
    }

    public boolean isValid(){
        if (validatingModelInstance == null){
            //TODO: add custom Exception and throw it
            return false;
        }
        if (validatedModelInstance == null){
            errors = run_validation(validatingModelInstance);
            validatedModelInstance = errors.size() == 0 ? validatingModelInstance : null;
            errors = errors.size() == 0 ? Collections.emptySet() : errors;
        }
        return errors.size() == 0;
    }
}
