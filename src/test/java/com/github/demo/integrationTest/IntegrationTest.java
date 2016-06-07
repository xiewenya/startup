package com.github.demo.integrationTest;

import com.github.demo.StartUpApplicationTest;
import com.github.startup.models.User;
import com.github.startup.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationTest extends StartUpApplicationTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@Value("${local.server.port}")
	private int port;

    private String globalUrl = "http://localhost:";

	private MockMvc mockMvc;
	private RestTemplate restTemplate = new TestRestTemplate();

	@Before
	public void setupMockMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void contextLoads() {
		assertEquals(2, userRepository.count());
	}

    @Test
    public void webappBookIsbnApi() {
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity( globalUrl + port + "/user/", Object[].class);
        User[] user = (User[]) responseEntity.getBody();
        assertTrue(1 == user[0].getId());
    }

    @Test
	public void GreetingRestApiTest() throws Exception {
        mockMvc.perform(get("/greeting/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("some data")));
//                .andExpect(jsonPath("$..id[0]").value(1));
	}


}
