package com.github.startup.serializers.classSerializer;

import com.github.startup.common.serializer.BaseSerializerClass;
import com.github.startup.models.User;
import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by bresai on 16/5/23.
 */

@Component
public class UserSerializerClass extends BaseSerializerClass<User> {

    @Override
    protected void addExtraField(JsonGenerator jsonGenerator) throws IOException {
        //add extra field to json
        jsonGenerator.writeObjectField("my_extra_field", "some data");
    }

    @Override
    public Class<User> handledType() {
        return User.class;
    }
}
