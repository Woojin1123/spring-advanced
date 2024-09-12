package org.example.expert;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;

public class SharedData {

  public static AuthUser AUTH_USER = new AuthUser(1L, "auth@nav.com", UserRole.USER);
  public static Todo TEST_TODO = new Todo("제목", "내용", "날씨", new User());
  public static User TEST_USER = new User("dnwls111@naver.com", "dnwls111",
      UserRole.USER);
  public static SigninRequest SIGN_IN_REQUEST = new SigninRequest("dnwls111@naver.com", "dnwls111");
  public static SignupRequest SIGN_UP_REQUEST = new SignupRequest("dnwls111@naver.com", "dnwls111",
      UserRole.USER.toString());
}
