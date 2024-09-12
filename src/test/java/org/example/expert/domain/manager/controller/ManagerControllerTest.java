package org.example.expert.domain.manager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
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

@TestInstance(Lifecycle.PER_CLASS)
@WebMvcTest(value = ManagerController.class)
public class ManagerControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ManagerController managerController;
  @MockBean
  private ManagerService managerService;
  @MockBean
  private JwtUtil jwtUtil;
  @Mock
  private AuthUserArgumentResolver authUserArgumentResolver;

  @BeforeAll
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(managerController)
        .setCustomArgumentResolvers(authUserArgumentResolver).build();
    given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
        new AuthUser(1L, "a@a.com",
            UserRole.USER));
    given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
  }

  @Test
  public void 매니저_등록() throws Exception {
    //given
    long todoId = 1L;
    long managerUserId = 1L;

    ManagerSaveRequest request = new ManagerSaveRequest(managerUserId);
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(request);

    ManagerSaveResponse managerSaveResponse = new ManagerSaveResponse(1L,
        new UserResponse(1L, "a@a.com"));

    given(managerService.saveManager(any(AuthUser.class), anyLong(),
        any(ManagerSaveRequest.class))).willReturn(
        managerSaveResponse);
    //when
    ResultActions resultActions = mockMvc.perform(
        post("/todos/{todoId}/managers", todoId)
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("id").value(managerSaveResponse.getId()))
        .andExpect(jsonPath("user.id").value(managerSaveResponse.getUser().getId()))
        .andExpect(jsonPath("user.email").value(managerSaveResponse.getUser().getEmail()));
  }

  @Test
  public void 매니저_목록_조회() throws Exception {
    //given
    long todoId = 1L;

    List<ManagerResponse> managerResponseList = new ArrayList<>();
    ManagerResponse managerResponse1 = new ManagerResponse(1L, new UserResponse(1L, "a@a.com"));
    ManagerResponse managerResponse2 = new ManagerResponse(2L, new UserResponse(2L, "b@b.com"));
    managerResponseList.add(managerResponse1);
    managerResponseList.add(managerResponse2);

    given(managerService.getManagers(anyLong())).willReturn(managerResponseList);
    //when
    ResultActions resultActions = mockMvc.perform(get("/todos/{todoId}/managers", todoId));

    //then
    resultActions.andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(managerResponse1.getId()))
        .andExpect(jsonPath("$[0].user.email").value(managerResponse1.getUser().getEmail()))
        .andExpect(jsonPath("$[0].user.id").value(managerResponse1.getUser().getId()))
        .andExpect(jsonPath("$[1].id").value(managerResponse2.getId()))
        .andExpect(jsonPath("$[1].user.email").value(managerResponse2.getUser().getEmail()))
        .andExpect(jsonPath("$[1].user.id").value(managerResponse2.getUser().getId()));
  }

  @Test
  public void 매니저_삭제() throws Exception {
    //given
    long todoId = 1L;
    long managerId = 2L;

    doNothing().when(managerService).deleteManager(any(AuthUser.class), anyLong(), anyLong());
    //when
    ResultActions resultActions = mockMvc.perform(
        delete("/todos/{todoId}/managers/{managerId}", todoId, managerId));
    //then
    resultActions.andExpect(status().isOk());
    verify(managerService, times(1)).deleteManager(any(), anyLong(), anyLong());

  }
}
