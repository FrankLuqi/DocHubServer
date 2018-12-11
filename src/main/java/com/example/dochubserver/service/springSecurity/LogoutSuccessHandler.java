package com.example.dochubserver.service.springSecurity;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dochubserver.service.JwtService;
import com.example.dochubserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler{

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        String token = httpServletRequest.getParameter("token");
        System.out.println("登出界面");
        if (token!=null)
        {
            DecodedJWT jwt = jwtService.VerifyToken(token);
            String userId = jwt.getClaim("userId").asString();
            userService.modifyUserChangeDate(new Date(),Long.parseLong(userId));//更改用户账号最近修改时间，使该token失效
        }
        PrintWriter out = httpServletResponse.getWriter();
        String s = "{\"code\":\"Success\",\"msg\":退出登录成功}";
        out.write(s);
        out.flush();
        out.close();
    }
}
