package org.example.expert.domain.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(value = TodoController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TodoControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private TodoController todoController;
  @MockBean
  private TodoService todoService;
  @Mock
  private AuthUserArgumentResolver authUserArgumentResolver;

  @BeforeAll
  public void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(todoController)
        .setCustomArgumentResolvers(authUserArgumentResolver).build();
    given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(
        new AuthUser(1L, "a@a.com",
            UserRole.USER));
    given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);
  }

  @Test
  public void 일정_저장() throws Exception {
    //given
    TodoSaveRequest todoSaveRequest = new TodoSaveRequest("제목", "내용");
    ObjectMapper objectMapper = new ObjectMapper();
    String requestJson = objectMapper.writeValueAsString(todoSaveRequest);

    TodoSaveResponse todoSaveResponse = new TodoSaveResponse(1L, "제목", "내용", "날씨",
        new UserResponse(1L, "email@email.com"));
    given(todoService.saveTodo(any(), any())).willReturn(todoSaveResponse);
    //when
    ResultActions resultActions = mockMvc.perform(
        post("/todos").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    //then
    resultActions.andExpect(status().isOk()).andExpect(jsonPath("id").value(1L))
        .andExpect(jsonPath("title").value("제목"))
        .andExpect(jsonPath("contents").value("내용"))
        .andExpect(jsonPath("weather").value("날씨"))
        .andExpect(jsonPath("user.id").value(1L))
        .andExpect(jsonPath("user.email").value("email@email.com"));
  }

  @Test
  public void 일정_목록_조회() throws Exception {
    //given
    long todoId = 1L;

    List<TodoResponse> todoResponseList = new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();
    TodoResponse todoResponse1 = new TodoResponse(1L, "제목", "내용", "날씨",
        new UserResponse(1L, "a@a.com"), now, now);
    TodoResponse todoResponse2 = new TodoResponse(1L, "제목2", "내용2", "날씨2",
        new UserResponse(2L, "b@a.com"), now, now);
    todoResponseList.add(todoResponse1);
    todoResponseList.add(todoResponse2);

    PageRequest pageRequest = PageRequest.of(1,10);
    Page<TodoResponse> todoResponsePage = new PageImpl<>(todoResponseList,pageRequest,2);

    given(todoService.getTodos(anyInt(), anyInt())).willReturn(todoResponsePage);
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/todos"));

    //then
    resultActions.andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(todoResponse1.getId()))
        .andExpect(jsonPath("$.content[0].title").value(todoResponse1.getTitle()))
        .andExpect(jsonPath("$.content[0].contents").value(todoResponse1.getContents()))
        .andExpect(jsonPath("$.content[0].weather").value(todoResponse1.getWeather()))
        .andExpect(jsonPath("$.content[0].user.email").value(todoResponse1.getUser().getEmail()))
        .andExpect(jsonPath("$.content[0].user.id").value(todoResponse1.getUser().getId()));
  }

  @Test
  public void 일정_단건_조회() throws Exception {
    //given
    long todoId = 1L;
    LocalDateTime now = LocalDateTime.now();
    TodoResponse todoResponse = new TodoResponse(1L, "제목", "내용", "날씨",
        new UserResponse(1L, "a@a.com"), now, now);

    given(todoService.getTodo(anyLong())).willReturn(todoResponse);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/todos/{todoId}",todoId));

    //then
    resultActions.andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(todoResponse.getId()))
        .andExpect(jsonPath("$.title").value(todoResponse.getTitle()))
        .andExpect(jsonPath("$.contents").value(todoResponse.getContents()))
        .andExpect(jsonPath("$.weather").value(todoResponse.getWeather()))
        .andExpect(jsonPath("$.user.email").value(todoResponse.getUser().getEmail()))
        .andExpect(jsonPath("$.user.id").value(todoResponse.getUser().getId()));
  }
}
