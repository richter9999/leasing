package com.yhzt.leasing.config.security;

import java.util.Map;

import javax.annotation.Resource;

import com.yhzt.leasing.utils.AjaxResponse;

import org.bouncycastle.openssl.PasswordException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {
    
    @Resource
    private JwtService jwtService;

    //@RequestMapping(value = "/authentication", method = RequestMethod.POST)
    @PostMapping(value = "/authentication")
    public AjaxResponse loginToken(@RequestBody Map<String,String> map) {
            
        String username = map.get("username");
        String password = map.get("password");

        if(!StringUtils.hasText(username) || !StringUtils.hasText(password)){
            return AjaxResponse.error(new PasswordException("用户密码 不能为空"), 400);
        }

        return AjaxResponse.success(jwtService.login(username, password));
    }




    @RequestMapping(value = "/refreshToken")
    public AjaxResponse refreshToken(@RequestHeader("Authentication") String token){
        return AjaxResponse.success(jwtService.refreshToken(token));
    }
}