package com.github.startup.common.serializer;

import com.github.startup.common.model.BaseModel;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * Created by bresai on 16/5/24.
 */

@Component
public abstract class BaseSerializerClass<T extends BaseModel> extends JsonSerializer<T> {

    @Override
    public void serialize(T item, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();

        //normal serializers of the item object;
        JavaType javaType = serializerProvider.constructType(item.getClass());
        BeanDescription beanDesc = serializerProvider.getConfig().introspect(javaType);
        JsonSerializer<Object> serializer =
                (JsonSerializer<Object>) BeanSerializerFactory.instance.findBeanSerializer(serializerProvider,
                javaType,
                beanDesc);
        // this is basically your 'writeAllFields()'-method:
        serializer.unwrappingSerializer(null).serialize(item, jsonGenerator, serializerProvider);

        addExtraField(jsonGenerator);
        jsonGenerator.writeEndObject();
    }

    protected abstract void addExtraField(JsonGenerator jsonGenerator) throws IOException;

}
