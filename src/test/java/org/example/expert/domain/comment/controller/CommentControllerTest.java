package org.example.expert.domain.comment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(value = CommentController.class)
public class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private CommentService commentService;
  @Autowired
  private CommentController commentController;
  @Mock
  private AuthUserArgumentResolver authUserArgumentResolver;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(commentController)
        .setCustomArgumentResolvers(authUserArgumentResolver).build();
  }

  @Test
  public void 댓글_등록() throws Exception {
    //given
    long todoId = 1L;
    CommentSaveResponse commentSaveResponse = new CommentSaveResponse(1L, "contents",
        new UserResponse(1L, "a@a.com"));

    CommentSaveRequest request = new CommentSaveRequest("contents");
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(request);

    given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
        new AuthUser(1L, "a@a.com",
            UserRole.USER));
    given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);

    given(commentService.saveComment(any(AuthUser.class), anyLong(),
        any(CommentSaveRequest.class))).willReturn(
        commentSaveResponse);
    //when
    ResultActions resultActions = mockMvc.perform(
        post("/todos/{todoId}/comments", todoId)
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("id").value(commentSaveResponse.getId()))
        .andExpect(jsonPath("contents").value(commentSaveResponse.getContents()))
        .andExpect(jsonPath("user.email").value("a@a.com"));
  }

  @Test
  public void 댓글_조회() throws Exception {
    //given
    long todoId = 1L;
    CommentResponse commentResponse1 = new CommentResponse(1L, "Contents1",
        new UserResponse(1L, "a@a.com"));
    CommentResponse commentResponse2 = new CommentResponse(2L, "Contents2",
        new UserResponse(1L, "a@a.com"));

    List<CommentResponse> commentResponseList = new ArrayList<>();
    commentResponseList.add(commentResponse1);
    commentResponseList.add(commentResponse2);

    given(commentService.getComments(anyLong())).willReturn(commentResponseList);
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/todos/{todoId}/comments", todoId));
    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(commentResponse1.getId()))
        .andExpect(jsonPath("$[0].contents").value(commentResponse1.getContents()))
        .andExpect(jsonPath("$[0].user.email").value(commentResponse1.getUser().getEmail()))
        .andExpect(jsonPath("$[1].id").value(commentResponse2.getId()))
        .andExpect(jsonPath("$[1].contents").value(commentResponse2.getContents()))
        .andExpect(jsonPath("$[1].user.email").value(commentResponse2.getUser().getEmail()));
  }

}
