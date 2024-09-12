package org.example.expert.domain.comment.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {
  @Mock
  private CommentRepository commentRepository;
  @InjectMocks
  private CommentAdminService commentAdminService;

  @Test
  public void 코멘트_삭제_성공(){
    //given
    long commentId = 1L;
    //when
    commentAdminService.deleteComment(commentId);
    //then
    then(commentRepository).should(times(1)).deleteById(anyLong());
  }
}
