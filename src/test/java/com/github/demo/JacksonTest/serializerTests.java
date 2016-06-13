package com.github.demo.JacksonTest;

import com.github.demo.StartUpApplicationTest;
import com.github.demo.JacksonTest.mixin.ExtraFieldMixin;
import com.github.startup.models.User;
import com.github.startup.utils.OrderState;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by bresai on 16/5/26.
 */
public class serializerTests extends StartUpApplicationTest {

    private ObjectMapper mapper;
    private ObjectWriter writer;
    private Validator validator;
    private User user;
    private String json;

    @Before
    public void setUp () {
        mapper = new ObjectMapper();
        mapper.setMixIns(ImmutableMap.of(User.class,
                ExtraFieldMixin.class));
        writer = mapper.writer().with(SerializationFeature.INDENT_OUTPUT);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User();
        user.setId(1);
        user.setUsername("bresai");
        user.setPassword("1111111");
        user.setState(OrderState.created);

        json = "{\"id\":1, \"username\":\"bresai\", " +
                "\"password\":\"1111111\", \"state\":\"created\"}";

    }

    @Test
    public void EnumSerializerTest() throws JsonProcessingException {
        String json = writer.writeValueAsString(user);
        System.out.println(json);

        assertNotEquals(json,null);

        String state = JsonPath.read(json, "$.state");
        assertEquals("created", state);
    }

    @Test
    public void EnumDeserializerTest() throws IOException {
        User user = new ObjectMapper().readValue(json, User.class);
        System.out.println(user);

        OrderState state = user.getState();

        //has state created
        assertEquals(OrderState.created, state);
    }

    @Test(expected = PathNotFoundException.class)
    public void PasswordSerializerIgnoreTest() throws IOException {
        String json = writer.writeValueAsString(user);
        System.out.println(json);

//        assertNotEquals(json,null);

        String password = JsonPath.read(json, "$.password");
    }

    @Test
    public void PasswordDeserializerTest() throws IOException {
        User user = new ObjectMapper().readValue(json, User.class);
        System.out.println(user);

        String password = user.getPassword();

        //has state created
        assertEquals("1111111", password);
    }

    @Test
    public void PasswordMinValidationDeserializerTest() throws IOException {
        user.setPassword("1");
        System.out.println(user);
        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user);

        System.out.print(constraintViolations.iterator().next().getMessage());
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void UsernameMaxValidationDeserializerTest() throws IOException {
        user.setUsername("1111111111111111111111111111111111111111111111111111111111111111111111111111");
        user.setPassword("1");
        System.out.println(user);
        Set<ConstraintViolation<User>> constraintViolations =
                validator.validate(user);

        while(constraintViolations.iterator().hasNext()){
            System.out.println(constraintViolations.iterator().next().getInvalidValue());
            System.out.println(constraintViolations.iterator().next().getMessage());
        }

        assertEquals(2, constraintViolations.size());
    }

//
//    @Test(expected = IllegalArgumentException.class)
//    public void jacksonSerializerNotNullValidationTest() throws JsonProcessingException {
//        TestModel model = new TestModel(null, "333445555");
//        String json = writer.writeValueAsString(model);
//    }

}
