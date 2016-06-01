package com.github.demo.JacksonTest.serializer;

import com.github.demo.JacksonTest.mixin.ExtraFieldMixin;
import com.github.startup.common.serializer.BaseSerializer;
import com.github.startup.models.User;

/**
 * Created by bresai on 16/5/26.
 */
public class UserSerializer extends BaseSerializer<User, ExtraFieldMixin> {
}
