package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
@Aspect
public class AspectAdminAccessLogging {

  /*
   * 2-5 AOP
   * 어노테이션 기반 포인트컷을 통해 원하는 Method에만 적용
   */
  @Pointcut("@annotation(org.example.expert.aop.annotation.AdminAccess)")
  private void adminAccessAnnotation() {
  }

  /*
  * API접근 시 Log출력
  */
  @Before("adminAccessAnnotation()")
  public void adminAccessLogging() {
    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    Long userId = (Long) request.getAttribute("userId");

    log.info("Admin API Access");
    log.info("User ID : {}", userId);
    log.info("API ACCESS TIME : {}", now);
    log.info("API ACCESS URL : {}",request.getRequestURI());
  }
}
