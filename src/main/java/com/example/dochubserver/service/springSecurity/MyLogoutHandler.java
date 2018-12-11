package com.example.dochubserver.service.springSecurity;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler{
    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        System.out.println("登出");
    }
}
