package com.yhzt.leasing.config.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yhzt.leasing.utils.JwtTokenUtil;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFliter extends OncePerRequestFilter {

    @Resource
    private SysUserDetailsService sysUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {   
            
            String token = request.getHeader("Authorization");
            
            log.info("获取 request 中的 Header 信息 : Authorization : {}",  token);

            // 验证token是否存在
            if(StringUtils.hasText(token)){
                // 根据token 获取用户名
                String username = JwtTokenUtil.getUsernameFromToken(token);
                if(StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                    // 通过用户名 获取用户的信息
                    UserDetails userDetails = sysUserDetailsService.loadUserByUsername(username);
                     // 验证JWT是否过期
                    if(JwtTokenUtil.validateToken(token, userDetails)){
                        //加载用户、角色、权限信息，Spring Security根据这些信息判断接口的访问权限
                        UsernamePasswordAuthenticationToken authenticationToken 
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }

            filterChain.doFilter(request, response);
    }
    
}