package com.github.startup.common.serializer;

import com.github.startup.common.model.BaseModel;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by bresai on 16/5/27.
 */

@Component
public class BaseDeserializerClass<T extends BaseModel> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException{
        return null;
    }
}
