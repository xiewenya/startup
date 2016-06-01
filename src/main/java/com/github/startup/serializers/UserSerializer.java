package com.github.startup.serializers;

import com.github.startup.common.serializer.BaseSerializer;
import com.github.startup.models.User;
import org.springframework.stereotype.Component;

/**
 * Created by bresai on 16/5/26.
 */

@Component
public class UserSerializer extends BaseSerializer<User, UserMixin> {
}
