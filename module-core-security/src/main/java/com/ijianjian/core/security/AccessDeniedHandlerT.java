package com.ijianjian.core.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ijianjian.core.common.object.CommonResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ijianjian.core.common.constant.ResultType.AuthError.forbidden;

import java.io.IOException;

@Component
public class AccessDeniedHandlerT extends AccessDeniedHandlerImpl {
@Override
public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
 //super.handle(request, response, accessDeniedException);
 ObjectMapper mapper = new ObjectMapper();
 //response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
 response.setHeader("content-type", "application/json;charset=UTF-8");
 response.setStatus(HttpStatus.FORBIDDEN.value());
 mapper.writeValue(response.getWriter(), CommonResult.errorResult(forbidden));
 System.out.println("____AccessDeniedHandlerT");
}
}
