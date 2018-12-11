package com.example.dochubserver.service.springSecurity;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.service.JwtService;
import com.example.dochubserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserService userService;

    @Value("${com.DOMAIN}")
    private String DOMAIN;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("过滤器：doFilterInternal");

        System.out.println(httpServletRequest.getRequestURL());
        String url = httpServletRequest.getRequestURL().toString().substring(httpServletRequest.getRequestURL().toString().lastIndexOf("/")+1);
        String token = httpServletRequest.getParameter("token");
        if ("uploadDoc".equals(url))
            token = httpServletRequest.getParameterMap().get("token")[0];
        if (token!=null)
        {
            DecodedJWT jwt = jwtService.VerifyToken(token);
            if (jwt!=null)//token没过期
            {
                Map<String,Claim> map = jwt.getClaims();
                String userId = map.get("userId").asString();
                User user = userService.findByUserId(Long.parseLong(userId));

                Date expiresDate = jwt.getExpiresAt();
                Date issuedDate0 = jwt.getIssuedAt();
                Date nowData = new Date();
                if (userId!=null)
                {
                    long jwtDate = issuedDate0.getTime()/1000;//获取token签发时间
                    long changeDate = userService.getUserChangeDate(userId)/1000;//获取用户账号上一次修改的时间戳
                    if (jwtDate==changeDate)//token的生成时间要根账户修改时间相同 因为登录的时候签发token同时同步账户修改时间
                    {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        httpServletRequest.setAttribute("userId",userId);//在这里将用户id传入，之后可以在controller中直接取出用户id，不用再次解析token
                    }
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
