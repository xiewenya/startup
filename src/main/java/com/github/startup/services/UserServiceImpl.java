package com.github.startup.services;

import com.github.startup.common.service.BaseServiceImpl;
import com.github.startup.models.User;
import com.github.startup.repositories.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Created by bresai on 16/5/25.
 */
@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, Long>
        implements GreetingService{

    private UserRepository getGreetingRepository() {
        return (UserRepository) repository;
    }


}
