package com.github.startup.serializers.fieldSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by bresai on 16/5/23.
 */
public class MessageSerializerField<T> extends JsonSerializer<T> {

//    @Override
//    public void serialize(String tmp,
//                          JsonGenerator jsonGenerator,
//                          SerializerProvider serializerProvider)
//            throws IOException, JsonProcessingException {
//        jsonGenerator.writeObject("test_of_custom_field_serializer");
//    }

    @Override
    public void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
    }
}
