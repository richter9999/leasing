package com.yhzt.leasing.config.security;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.yhzt.leasing.mapper.SysUserDetailsServiceMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component("rbacService")
public class RBACService {

    @Resource
    private SysUserDetailsServiceMapper sysUserDetailsServiceMapper;

    private AntPathMatcher antPathMatcher;

    /**
     * 判断是否 有该 request 的 访问权限
     * 
     * @param request
     * @param authentication
     * @return
     */
    public boolean hasPermission(final HttpServletRequest request, final Authentication authentication) {

        final Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            final String username = ((UserDetails) principal).getUsername();
            final List<String> urlList = sysUserDetailsServiceMapper.getAuthenticationByName(username);

            return urlList.stream().anyMatch(url -> antPathMatcher.match(url, request.getRequestURI()));
        }

        return false;
    }
}