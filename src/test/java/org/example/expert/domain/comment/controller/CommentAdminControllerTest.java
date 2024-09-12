package org.example.expert.domain.comment.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(value = CommentAdminController.class)
public class CommentAdminControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private CommentAdminService commentAdminService;

  @Test
  public void 댓글_삭제() throws Exception {
    //given
    long commentId = 1L;

    doNothing().when(commentAdminService).deleteComment(anyLong());
    //when
    ResultActions resultActions = mockMvc.perform(delete("/admin/comments/{commentId}", commentId));
    //then
    resultActions.andExpect(status().isOk());
  }
}
