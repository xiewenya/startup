package com.github.demo.mockTest;

import com.github.demo.StartUpApplicationTest;
import com.github.startup.models.User;
import com.github.startup.repositories.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

/**
 * Created by bresai on 16/5/26.
 */

public class MockTests extends StartUpApplicationTest {

    private UserRepository repository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("bresai");
        user.setPassword("123456");
        user.setActive(true);
        user.setAddress("12312312");
        user.setCreated(new Date());
        user.setState();

        MockitoAnnotations.initMocks(this);
        repository = Mockito.mock(UserRepository.class);
        Mockito.when(repository.findOne(0L)).thenReturn(user);
    }

    @Test
    public void publishersExist() {
        assertEquals(1L, repository.count());
    }

    @After
    public void resetPublisherRepositoryMock() {
        Mockito.reset(repository);
    }
}
