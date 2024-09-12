package org.example.expert;

import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;

public class SharedData {

  public static User TEST_USER = new User("dnwls111@naver.com", "dnwls111",
      UserRole.USER);
  public static SigninRequest signinRequest = new SigninRequest("dnwls111@naver.com", "dnwls111");
  public static SignupRequest signupRequest = new SignupRequest("dnwls111@naver.com", "dnwls111",
      UserRole.USER.toString());
}
