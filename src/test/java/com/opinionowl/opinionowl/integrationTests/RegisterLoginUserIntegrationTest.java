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

import static java.lang.Long.parseLong;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
     * @throws Exception
     */
    @Test
    public void testRegisterAndLoginUserResponse() throws Exception {
        String postData = "{\"username\":\"maxcurkovic\",\"password\":\"sysc4806\"}";
        this.testController.perform(post("/api/v1/createUser")
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        AppUser loggedInUser = null;
        for (AppUser user : userRepository.findAll()) {
            if (user.getUsername().equals("maxcurkovic") && user.getPassword().equals("sysc4806")) {
                loggedInUser = user;
                break;
            }
        }

        Cookie cookie = new Cookie("userId", loggedInUser.getId().toString());

        this.testController.perform(post("/api/v1/loginUser")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        assertEquals(parseLong(cookie.getValue()), loggedInUser.getId());
        this.testController.perform(post("/api/v1/logout")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON).content(postData))
                .andExpect(status().isOk());

        this.testController.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("<a href=\"/loginUser\" class=\"btn\">")));
    }
}
