package com.opinionowl.opinionowl.integrationTests;

import com.opinionowl.opinionowl.models.AppUser;
import com.opinionowl.opinionowl.repos.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterLoginUserIntegrationTest {

    @Autowired
    private MockMvc testController;

    @Autowired
    private UserRepository userRepository;

    /**
     * Integration test the register and login user response mappings.
     * Verifies that a user can be registered, be logged in, and then logged out.
     * @throws Exception - throws Exception
     */
    @Test
    public void testRegisterAndLoginUserResponse() throws Exception {
        String postData = "{\"username\":\"testRegisterAndLoginUserResponseUser\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = userRepository.findByUsername("testRegisterAndLoginUserResponseUser").orElse(null);
        assertNotNull(loggedInUser);

        Cookie cookie = new Cookie("username", loggedInUser.getUsername().toString());

        this.testController.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        assertEquals(cookie.getValue(), loggedInUser.getUsername());
        this.testController.perform(post("/api/v1/logout")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        this.testController.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"/loginUser\" class=\"btn\">")));
    }
}
