package com.yhzt.leasing.config.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yhzt.leasing.utils.AjaxResponse;
import com.yhzt.leasing.utils.JwtTokenUtil;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        log.debug("登陆成功");
        response.setContentType("application/json;charset=UTF-8");
        String token = JwtTokenUtil.generateToken(authentication.getName());

        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("authentication", authentication);
        response.getWriter().write(objectMapper.writeValueAsString(AjaxResponse.success(tokenMap)));
        //super.onAuthenticationSuccess(request, response, authentication);
    }

}