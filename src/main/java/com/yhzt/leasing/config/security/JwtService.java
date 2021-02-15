package com.yhzt.leasing.config.security;

import javax.annotation.Resource;

import com.yhzt.leasing.utils.JwtTokenUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private SysUserDetailsService sysUserDetailsService;

    /**
     * 根据 用户密码 返回 token
     * 
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password) throws AuthenticationException {

        //生成JWT
        UserDetails userDetails = sysUserDetailsService.loadUserByUsername(username);
        String token = JwtTokenUtil.generateToken(username);

        try {
            //使用用户名密码进行登录验证
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
            
            //使用用户名密码进行登录验证
            //UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
            //Authentication authentication = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        
        log.info("Token : {}", token);

        return token;
        
    }

    /**
     * 刷新 Token
     * @param token
     * @return
     */
    public String refreshToken(String token){
        if(!JwtTokenUtil.isTokenExpired(token)){
            return JwtTokenUtil.refreshToken(token);
        }
        
        return "";
    }
}