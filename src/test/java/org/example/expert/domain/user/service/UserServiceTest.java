package org.example.expert.domain.user.service;

import static org.example.expert.SharedData.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
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
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserService userService;

  @Nested
  @DisplayName("유저_조회")
  class GetUser {

    @Test
    public void 유저_조회_성공() {
      //given
      long userId = 1L;
      User user = TEST_USER;

      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      //when
      UserResponse userResponse = userService.getUser(userId);

      //then
      assertNotNull(userResponse);
    }

    @Test
    public void 유저가_없을_경우_IRE_에러를_던진다() {
      //given
      long userId = 1L;

      given(userRepository.findById(anyLong())).willReturn(Optional.empty());
      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> userService.getUser(userId));
      assertEquals("User not found", exception.getMessage());
    }
  }

  @Nested
  @DisplayName("비밀번호_변경")
  class ChangePassword {

    @Test
    public void 비밀번호_변경_성공() {
      //given
      long userId = 1L;
      User user = spy(TEST_USER);
      ReflectionTestUtils.setField(user, "id", userId);

      UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(
          "oldPwd123",
          "newPwd123");

      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),
          user.getPassword())).willReturn(false);
      given(passwordEncoder.matches(userChangePasswordRequest.getOldPassword(),
          user.getPassword())).willReturn(true);
      given(passwordEncoder.encode(anyString())).willReturn("12314123adsfasd");
      //when
      userService.changePassword(userId, userChangePasswordRequest);

      //then
      then(user).should(times(1)).changePassword(anyString());
    }

    @Test
    public void 새_비밀번호와_기존_비밀번호가_같을_시_IRE_에러를_던진다() {
      //given
      long userId = 1L;
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", userId);

      UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(
          "oldPwd123",
          "newPwd123");
      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),
          user.getPassword())).willReturn(true);
      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> userService.changePassword(userId, userChangePasswordRequest));
      assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 비밀번호가_다를시_IRE_에러를_던진다() {
      //given
      long userId = 1L;
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", userId);

      UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(
          "oldPwd123",
          "newPwd123");
      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      given(passwordEncoder.matches(userChangePasswordRequest.getNewPassword(),
          user.getPassword())).willReturn(false);
      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> userService.changePassword(userId, userChangePasswordRequest));
      assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void 비밀번호_형식이_다를시_IRE_에러를_던진다(){
      //given
      long userId = 1L;
      UserChangePasswordRequest userChangePasswordRequest = new UserChangePasswordRequest(
          "oldPwd",
          "newPwd");

      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> userService.changePassword(userId, userChangePasswordRequest));
      assertEquals("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.", exception.getMessage());
    }
  }
}


