package org.example.expert.domain.auth.controller;

import static org.example.expert.SharedData.SIGN_IN_REQUEST;
import static org.example.expert.SharedData.SIGN_UP_REQUEST;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = AuthController.class)
public class AuthControllerTest {

  @MockBean
  private AuthService authService;
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void 회원가입() throws Exception {
    //given
    SignupResponse signupResponse = new SignupResponse("Bearer Token");
    SignupRequest request = SIGN_UP_REQUEST;
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(request);

    given(authService.signup(any())).willReturn(signupResponse);
    //when
    ResultActions resultActions = mockMvc.perform(
        post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("bearerToken").value(startsWith("Bearer ")));
  }

  @Test
  public void 로그인() throws Exception {
    //given
    SigninResponse signinResponse = new SigninResponse("Bearer Token");
    SigninRequest signinRequest = SIGN_IN_REQUEST;
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(signinRequest);

    given(authService.signin(any())).willReturn(signinResponse);
    //when
    ResultActions resultActions = mockMvc.perform(
        post("/auth/signin").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("bearerToken").value(startsWith("Bearer ")));
  }

}
