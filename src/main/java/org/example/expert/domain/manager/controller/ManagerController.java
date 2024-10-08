package org.example.expert.domain.manager.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ManagerController {

  private final ManagerService managerService;
  private final JwtUtil jwtUtil;

  @PostMapping("/todos/{todoId}/managers")
  public ResponseEntity<ManagerSaveResponse> saveManager(
      @Auth AuthUser authUser,
      @PathVariable long todoId,
      @Valid @RequestBody ManagerSaveRequest managerSaveRequest
  ) {
    return ResponseEntity.ok(managerService.saveManager(authUser, todoId, managerSaveRequest));
  }

  @GetMapping("/todos/{todoId}/managers")
  public ResponseEntity<List<ManagerResponse>> getMembers(@PathVariable long todoId) {
    return ResponseEntity.ok(managerService.getManagers(todoId));
  }

  /*
   * 1-4 JWT 유효성 검사 로직 수정
   * 필터에서 JWT 인증 수행
   * 이후 Argument Resolver를 통해 인증된 user의 ID,EMAIL,UserRole 가져옴
   */
  @DeleteMapping("/todos/{todoId}/managers/{managerId}")
  public void deleteManager(
      @Auth AuthUser authUser,
      @PathVariable long todoId,
      @PathVariable long managerId
  ) {
    managerService.deleteManager(authUser, todoId, managerId);
  }
}
