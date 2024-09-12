package org.example.expert.domain.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = UserAdminController.class)
public class UserAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserAdminService userAdminService;

  @Test
  public void 유저_권한_변경() throws Exception {
    //given
    long userId = 1L;
    UserRoleChangeRequest userRoleChangeRequest = new UserRoleChangeRequest(
        UserRole.ADMIN.toString());
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(userRoleChangeRequest);

    doNothing().when(userAdminService).changeUserRole(anyLong(), any());
    //when
    ResultActions resultActions = mockMvc.perform(
        patch("/admin/users/{userId}", userId).content(requestJson).contentType(
            MediaType.APPLICATION_JSON));
    //then
    resultActions.andExpect(status().isOk());
    verify(userAdminService, times(1)).changeUserRole(anyLong(), any());
  }

}