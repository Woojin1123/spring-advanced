package org.example.expert.domain.manager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Optional;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {

  @Mock
  private ManagerRepository managerRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private TodoRepository todoRepository;
  @InjectMocks
  private ManagerService managerService;

  @Nested
  @DisplayName("매니저_등록")
  class ManagerRegistration {

    @Test
      // 테스트코드 샘플
    void todo가_정상적으로_등록된다() {
      // given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

      long todoId = 1L;
      Todo todo = new Todo("Test Title", "Test Contents", "Sunny", user);

      long managerUserId = 2L;
      User managerUser = new User("b@b.com", "password", UserRole.USER);  // 매니저로 등록할 유저
      ReflectionTestUtils.setField(managerUser, "id", managerUserId);

      ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(
          managerUserId); // request dto 생성

      given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
      given(userRepository.findById(managerUserId)).willReturn(Optional.of(managerUser));
      given(managerRepository.save(any(Manager.class))).willAnswer(
          invocation -> invocation.getArgument(0));

      // when
      ManagerSaveResponse response = managerService.saveManager(authUser, todoId,
          managerSaveRequest);

      // then
      assertNotNull(response);
      assertEquals(managerUser.getId(), response.getUser().getId());
      assertEquals(managerUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void todo의_user가_null인_경우_예외가_발생한다() {
      // given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      long todoId = 1L;
      long managerUserId = 2L;

      Todo todo = new Todo();
      ReflectionTestUtils.setField(todo, "user", null);

      ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(managerUserId);

      given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));

      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
          managerService.saveManager(authUser, todoId, managerSaveRequest)
      );

      assertEquals("담당자를 등록하려고 하는 유저가 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());
    }

    @Test
    public void 본인을_담당자로_등록시_IRE_에러를_던진다() {
      //given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);
      long todoId = 1L;
      Todo todo = new Todo();
      ReflectionTestUtils.setField(todo, "user", user);
      ManagerSaveRequest managerSaveRequest = new ManagerSaveRequest(authUser.getId());

      given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
      given(userRepository.findById(managerSaveRequest.getManagerUserId())).willReturn(
          Optional.of(user));

      //when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
          managerService.saveManager(authUser, todoId, managerSaveRequest)
      );

      assertEquals("일정 작성자는 본인을 담당자로 등록할 수 없습니다.", exception.getMessage());
    }
  }

  /*
   * 2-2 유닛테스트 - 1
   * getManagers에서 IRE를 Throw하고 있기 때문에 NPE->IRE 수정 & Exception 메시지 변경
   * 메서드 명 NPE->IRE 수정
   */
  @Test
  public void manager_목록_조회_시_Todo가_없다면_IRE_에러를_던진다() {
    // given
    long todoId = 1L;
    given(todoRepository.findById(todoId)).willReturn(Optional.empty());

    // when & then
    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
        () -> managerService.getManagers(todoId));
    assertEquals("Todo not found", exception.getMessage());
  }


  @Nested
  @DisplayName("매니저_조회")
  class ManagerInquiry {

    @Test // 테스트코드 샘플
    public void manager_목록_조회에_성공한다() {
      // given
      long todoId = 1L;
      User user = new User("user1@example.com", "password", UserRole.USER);
      Todo todo = new Todo("Title", "Contents", "Sunny", user);
      ReflectionTestUtils.setField(todo, "id", todoId);

      Manager mockManager = new Manager(todo.getUser(), todo);
      List<Manager> managerList = List.of(mockManager);

      given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
      given(managerRepository.findByTodoIdWithUser(todoId)).willReturn(managerList);

      // when
      List<ManagerResponse> managerResponses = managerService.getManagers(todoId);

      // then
      assertEquals(1, managerResponses.size());
      assertEquals(mockManager.getId(), managerResponses.get(0).getId());
      assertEquals(mockManager.getUser().getEmail(), managerResponses.get(0).getUser().getEmail());
    }
  }

  @Nested
  @DisplayName("매니저_삭제")
  class ManagerDelete {

    @Test
// 테스트코드 샘플
    void 매니저_삭제_성공() {
      // given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);  // 일정을 만든 유저

      User managerUser = new User("anana@naver.com", "apasd", UserRole.USER);//매니저

      long todoId = 1;
      Todo todo = new Todo("제목", "내용", "날씨", user);
      ReflectionTestUtils.setField(todo, "id", todoId);

      long managerId = 1;
      Manager mockManager = new Manager(managerUser, todo);
      ReflectionTestUtils.setField(mockManager, "id", managerId);

      given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
      given(todoRepository.findById(todoId)).willReturn(Optional.of(todo));
      given(managerRepository.findById(managerId)).willReturn(Optional.of(mockManager));

      //when
      managerService.deleteManager(authUser, todoId, managerId);
      //then
      then(managerRepository).should(times(1)).delete(mockManager);
    }

    @Test
    public void 일정을_만든_유저가_유효하지_않을_때_IRE_에러를_던진다() {
      //given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);
      User user2 = new User("ababa@naver.com", "ababa", UserRole.USER);  // 일정을 만든 유저
      ReflectionTestUtils.setField(user2, "id", 2L);

      long todoId = 1L;
      Todo todo = new Todo("제목", "내용", "날씨", user2);

      long managerId = 1L;

      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));

      //when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> managerService.deleteManager(authUser,todoId,managerId));
      assertEquals("해당 일정을 만든 유저가 유효하지 않습니다.", exception.getMessage());

    }

    @Test
    public void 일정에_등록된_담당자가_아닐때_IRE_에러를_던진다() {
      //given
      AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
      User user = User.fromAuthUser(authUser);
      User user2 = new User("ababa@naver.com", "ababa", UserRole.USER);  // 일정을 만든 유저
      ReflectionTestUtils.setField(user2, "id", 2L);

      long todoId = 1L;
      Todo todo = new Todo("제목", "내용", "날씨", user);
      ReflectionTestUtils.setField(todo,"id",todoId);
      long todoId2 = 2L;
      Todo todo2 = new Todo("제목", "내용", "날씨", user);
      ReflectionTestUtils.setField(todo2,"id",todoId2);

      long managerId = 1L;
      Manager manager = new Manager(user2,todo2);

      given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
      given(todoRepository.findById(anyLong())).willReturn(Optional.of(todo));
      given(managerRepository.findById(anyLong())).willReturn(Optional.of(manager));
      // when & then
      InvalidRequestException exception = assertThrows(InvalidRequestException.class,
          () -> managerService.deleteManager(authUser,todoId,managerId));
      assertEquals("해당 일정에 등록된 담당자가 아닙니다.", exception.getMessage());

    }
  }


}


