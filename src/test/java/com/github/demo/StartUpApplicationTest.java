package com.github.demo;

import com.github.startup.StartUpApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StartUpApplication.class)
@WebIntegrationTest("server.port:8123")
public class StartUpApplicationTest {

    @Test
    public void LoaderContext() {
    }


}

