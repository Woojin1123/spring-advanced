package org.example.expert.domain.auth.service;

import static org.example.expert.SharedData.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.example.expert.config.GlobalExceptionHandler;
import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private JwtUtil jwtUtil;
  @Mock
  GlobalExceptionHandler globalExceptionHandler;
  @InjectMocks
  private AuthService authService;

  @Nested
  @DisplayName("회원가입")
  class 회원가입 {

    @Test
    public void 회원가입_정상동작() {
      //given
      SignupRequest signupRequest = new SignupRequest("dnwls111@naver.com", "dnwls111",
          UserRole.USER.toString());
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", 1L);
      given(userRepository.save(any())).willReturn(user);
      given(jwtUtil.createToken(anyLong(), anyString(), any())).willReturn("Bearer token");

      //when
      SignupResponse response = authService.signup(signupRequest);
      String bearerToken = response.getBearerToken();

      //then
      assertNotNull(response);
      assertTrue(bearerToken.startsWith("Bearer "));
    }

    @Test
    public void 이메일이_중복될_경우_IRE_에러를_던진다() {
      //given
      SignupRequest signupRequest = new SignupRequest("dnwls111@naver.com", "dnwls111",
          UserRole.USER.toString());
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", 1L);
      given(userRepository.existsByEmail(anyString())).willReturn(true);

      //when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> authService.signup(signupRequest));
      assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("로그인")
  class 로그인 {

    @Test
    public void 로그인_정상동작() {
      //given
      SigninRequest signinRequest = new SigninRequest("dnwls111@naver.com", "dnwls111");
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", 1L);
      given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
      given(jwtUtil.createToken(anyLong(), anyString(), any())).willReturn("Bearer token");
      given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

      //when
      SigninResponse response = authService.signin(signinRequest);
      String bearerToken = response.getBearerToken();

      //then
      assertNotNull(response);
      assertTrue(bearerToken.startsWith("Bearer "));
    }

    @Test
    public void 가입되지_않은_유저의_경우_IRE_에러를_던진다(){
      //given
      SigninRequest signinRequest = new SigninRequest("dnwls111@naver.com", "dnwls111");
      given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

      //when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> authService.signin(signinRequest));
      assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void 이메일과_비밀번호가_일치하지_않을_경우_401을_반환한다() {
      //given
      SigninRequest signinRequest = new SigninRequest("dnwls111@naver.com", "dnwls111");
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", 1L);
      given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
      given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

      //when & then
      AuthException exception = assertThrows(AuthException.class,
          () -> authService.signin(signinRequest));
      assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }
  }
}
