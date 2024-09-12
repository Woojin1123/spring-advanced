package org.example.expert.domain.comment.service;

import static org.example.expert.SharedData.AUTH_USER;
import static org.example.expert.SharedData.TEST_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;
  @Mock
  private TodoRepository todoRepository;
  @InjectMocks
  private CommentService commentService;

  @Nested
  @DisplayName("Comment_등록")
  class CommentRegistration {

    @Test
    public void 할일의_담당자가_아니면_IRE_에러를_던진다() {
      //given
      long todoId = 1;
      AuthUser authUser = AUTH_USER;
      User user = TEST_USER;
      ReflectionTestUtils.setField(user, "id", 2L);

      CommentSaveRequest request = new CommentSaveRequest("contents");
      Todo todo = new Todo("title", "title", "contents", user);

      given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
      //when
      InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
        commentService.saveComment(authUser, todoId, request);
      });
      // then
      assertEquals("할일의 담당자가 아니면 댓글을 달 수 없습니다.", exception.getMessage());
    }

    @Test
    public void comment_등록_중_할일을_찾지_못해_에러가_발생한다() {
      // given
      long todoId = 1;
      CommentSaveRequest request = new CommentSaveRequest("contents");
      AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);

      given(todoRepository.findById(anyLong())).willReturn(Optional.empty());

      // when
      /*
       * 2-3 유닛 테스트 - 2
       * saveComment가 IRE 에러를 throw하고 있으므로 assertThrows의 매개변수 변경
       */
      InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
        commentService.saveComment(authUser, todoId, request);
      });

      // then
      assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void comment를_정상적으로_등록한다() {
      // given
      long todoId = 1;
      CommentSaveRequest request = new CommentSaveRequest("contents");
      AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
      User user = User.fromAuthUser(authUser);
      Todo todo = new Todo("title", "title", "contents", user);
      Comment comment = new Comment(request.getContents(), user, todo);

      given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
      given(commentRepository.save(any())).willReturn(comment);

      // when
      CommentSaveResponse result = commentService.saveComment(authUser, todoId, request);

      // then
      assertNotNull(result);
    }
  }

  @Nested
  @DisplayName("Comment_조회")
  class CommentInquiry {

    @Test
    public void 댓글_조회_성공() {
      //given
      long todoId = 1;
      AuthUser authUser = new AuthUser(1L, "email@naver.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);
      Todo todo = new Todo("title", "title", "contents", user);
      Comment comment1 = new Comment("content1", user, todo);
      Comment comment2 = new Comment("content2", user, todo);
      List<Comment> commentList = new ArrayList<>();
      commentList.add(comment1);
      commentList.add(comment2);

      given(commentRepository.findByTodoIdWithUser(anyLong())).willReturn(commentList);

      //when
      List<CommentResponse> dtoList = commentService.getComments(todoId);

      //then
      assertNotNull(dtoList);
    }
  }

}
