package org.example.expert.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(value = UserController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserController userController;
  @MockBean
  private UserService userService;
  @Mock
  private AuthUserArgumentResolver authUserArgumentResolver;

  @BeforeAll
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setCustomArgumentResolvers(authUserArgumentResolver).build();
    given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
        new AuthUser(1L, "a@a.com",
            UserRole.USER));
    given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
  }

  @Test
  public void 유저_단건_조회() throws Exception {
    //given
    long userId = 1L;

    UserResponse userResponse = new UserResponse(1L, "Test@email.com");
    given(userService.getUser(anyLong())).willReturn(userResponse);
    //when
    ResultActions resultActions = mockMvc.perform(get("/users/{userId}", userId));
    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("id").value(1L))
        .andExpect(jsonPath("email").value("Test@email.com"));
  }

  @Test
  public void 비밀번호_변경() throws Exception {
    //given
    UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(
        "oldPassword", "newPassword");
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(userChangePasswordRequest);

    doNothing().when(userService).changePassword(anyLong(), any());
    //when
    ResultActions resultActions = mockMvc.perform(
        put("/users").content(requestJson).contentType(
            MediaType.APPLICATION_JSON));
    //then
    resultActions.andExpect(status().isOk());
    verify(userService, times(1)).changePassword(anyLong(), any());
  }
}
