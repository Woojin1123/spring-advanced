package org.example.expert.config;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

  @Mock
  private JwtUtil jwtUtil;
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpServletResponse response;
  @Mock
  private FilterChain filterChain;
  @InjectMocks
  private JwtFilter jwtFilter;

  @Test
  public void API요청이_AUTH로_시작할_경우_필터_통과() throws ServletException, IOException {
    //given
    given(request.getRequestURI()).willReturn("/auth");
    //when
    jwtFilter.doFilter(request, response, filterChain);
    //then
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  public void 토큰이_없을_경우_400에러_반환() throws ServletException, IOException {
    //given
    given(request.getRequestURI()).willReturn("/api/test");
    given(request.getHeader(anyString())).willReturn(null);
    //when
    jwtFilter.doFilter(request, response, filterChain);
    //then
    verify(response, times(1)).sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
    verify(filterChain, never()).doFilter(request, response);
  }
}
