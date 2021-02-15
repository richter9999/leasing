package com.yhzt.leasing.config.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.yhzt.leasing.mapper.SysUserDetailsServiceMapper;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SysUserDetailsService implements UserDetailsService {
  
  @Resource
  private SysUserDetailsServiceMapper sysUserDetailsServiceMapper;

  @Override
  @Cacheable(value = "Authentication", key = "'sysUserDetails'")
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //加载 用户 基本信息
    SysUserDetails sysUserDetails = sysUserDetailsServiceMapper.getUserByName(username);

    // 进行密码的比对
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    boolean flag = bCryptPasswordEncoder.matches("admin", sysUserDetails.getPassword());

    log.info("测试 密码是否正确 : passWord :{} , flag : {} ",  sysUserDetails.getPassword(), flag );

    //加载 用户 权限列表
    List<String> roleCodeList  = sysUserDetailsServiceMapper.getRoleByUserName(username);

    log.info("roleCodeList :  {}", roleCodeList);

    //通过权限列表 加载用户 权限
    List<String> authorites = sysUserDetailsServiceMapper.getAuthenticationByRoleCode(roleCodeList);

    log.info("authorites :  {}", authorites);

    // Spring 权限 + ROLE_
    roleCodeList = roleCodeList.stream().map(rc -> "ROLE_" + rc ).collect(Collectors.toList());
    
    authorites.addAll(roleCodeList);

    sysUserDetails.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", authorites)));

    return sysUserDetails;
  }

}