package org.example.expert.domain.user.service;

import static org.example.expert.SharedData.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.Optional;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {

  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private UserAdminService userAdminService;

  @Test
  public void 유저_역할_변경_성공() {
    //given
    long userId = 1L;
    User user = spy(TEST_USER);

    UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

    given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
    //when
    userAdminService.changeUserRole(userId,userRoleChangeRequest);
    //then
    then(user).should(times(1)).updateRole(any());
  }
  @Test
  public void 유효하지_않은_유저일_경우_IRE_에러를_던진다(){
    //given
    long userId = 1L;
    UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest("ADMIN");

    given(userRepository.findById(anyLong())).willReturn(Optional.empty());
    //when & then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
      userAdminService.changeUserRole(userId,userRoleChangeRequest);
    });

    assertEquals("User not found", exception.getMessage());
  }

}
