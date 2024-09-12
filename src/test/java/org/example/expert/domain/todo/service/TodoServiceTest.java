package org.example.expert.domain.todo.service;

import static org.example.expert.SharedData.AUTH_USER;
import static org.example.expert.SharedData.TEST_TODO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

  @Mock
  private TodoRepository todoRepository;
  @Mock
  private WeatherClient weatherClient;
  @InjectMocks
  private TodoService todoService;

  @Nested
  @DisplayName("일정_저장")
  class SaveTodo {

    @Test
    public void 일정_저장_성공() {
      //given
      TodoSaveRequest todoSaveRequest = new TodoSaveRequest("제목","내용");
      AuthUser authUser = AUTH_USER;
      User user = User.fromAuthUser(authUser);

      Todo todo = TEST_TODO;
      ReflectionTestUtils.setField(todo,"id",1L);

      given(todoRepository.save(any())).willReturn(todo);
      //when
      TodoSaveResponse todoSaveResponse = todoService.saveTodo(authUser,todoSaveRequest);

      //then
      assertNotNull(todoSaveResponse);
    }

  }
  @Nested
  @DisplayName("일정_조회")
  class GetTodo {

    @Test
    public void 일정_조회_성공() {
      //given
      long todoId = 1L;
      Todo todo = TEST_TODO;
      ReflectionTestUtils.setField(todo,"id",todoId);

      given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));
      //when
      TodoResponse todoResponse = todoService.getTodo(todoId);

      //then
      assertNotNull(todoResponse);
    }
    @Test
    public void todoID_일정이_존재하지_않을_경우_IRE_에러를_던진다() {
      //given
      long todoId = 1L;
      Todo todo = TEST_TODO;
      ReflectionTestUtils.setField(todo,"id",todoId);

      given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());
      //when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
          todoService.getTodo(todoId)
      );

      assertEquals("Todo not found", exception.getMessage());
    }

  }

  @Nested
  @DisplayName("일정_목록_조회")
  class GetTodos {

    @Test
    public void 일정_목록_조회_성공() {
      //given
      long todoId = 1L;
      Todo todo = TEST_TODO;
      List<Todo> todos = new ArrayList<>();
      todos.add(todo);
      Page<Todo> todoPage = new PageImpl<>(todos);
      ReflectionTestUtils.setField(todo,"id",todoId);

      given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(todoPage);
      //when
      Page<TodoResponse> todoResponsePage = todoService.getTodos(1,10);

      //then
      assertNotNull(todoResponsePage);
    }

  }

}
