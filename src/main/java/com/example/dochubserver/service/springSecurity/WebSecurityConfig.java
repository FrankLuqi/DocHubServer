package com.example.dochubserver.service.springSecurity;

import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.service.DepartmentsService;
import com.example.dochubserver.service.JwtService;
import com.example.dochubserver.service.RoleService;
import com.example.dochubserver.utils.UsuallyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Autowired
    UrlAccessDecisionManager urlAccessDecisionManager;

    @Autowired
    AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;

    @Autowired
    MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Autowired
    UserLogin userLogin;

    @Autowired
    JwtService jwtService;

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentsService departmentsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLogin).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/index.html", "/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
            @Override
            public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                //设置之前写好的两个过滤器
                o.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                o.setAccessDecisionManager(urlAccessDecisionManager);
                return o;
            }
        }).and().formLogin().loginPage("/login").loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").permitAll().failureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter out = httpServletResponse.getWriter();
                StringBuffer sb = new StringBuffer();
                sb.append("{\"code\":\"Error\",\"msg\":\"");
                if (e instanceof UsernameNotFoundException || e instanceof BadCredentialsException) {
                    sb.append("用户名或密码输入错误，登录失败!");
                } else if (e instanceof DisabledException) {
                    sb.append("账户被禁用，登录失败，请联系管理员!");
                } else {
                    sb.append("登录失败!"+e.getMessage());
//                    System.out.println("错误为:"+e.getMessage());
                }
                sb.append("\"}");
                out.write(sb.toString());
                out.flush();
                out.close();

            }
        }).successHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                    User user = (User) userDetailsService().loadUserByUsername(authentication.getName());
    //                System.out.println("用户名为："+user.getUsername());
                    httpServletResponse.setContentType("application/json;charset=utf-8");
                    String rememberMe = httpServletRequest.getParameter("rememberMe");
                    Map<String,Object> map = new HashMap<>();
                    map.put("userName",user.getUsername());
                    map.put("userId",String.valueOf(user.getId()));

                    String token = null;
                    try {
                        token = jwtService.GenerateToken(map,rememberMe.equals("remember")? 7:1,user.getId());//记住登录则过期时间为7天否则为1天
                    }
                    catch (Exception e)
                    {

                    }
                    PrintWriter out = httpServletResponse.getWriter();
                    ObjectMapper objectMapper = new ObjectMapper();
//                    String s = "{\"status\":\"success\",\"msg\":" + objectMapper.writeValueAsString(HrUtils.getCurrentHr()) + "}";

                     List<OwnedRole> roles = user.getOwnedRoles();
//                  获取该用户所具有的角色
                    StringBuffer departmentRole = new StringBuffer();

                    List<String> list = new ArrayList<>();
                    List<String> list2 = new ArrayList<>();

                    for (OwnedRole role : roles)
                    {
                        String departmentRoleId = role.getDepartmentRoleId();
                        list2.add(departmentRoleId);
                        Map<String,Long> departmentRoleIdMap = UsuallyUtil.parseDepartmentRoleId(departmentRoleId);

                        if (departmentRoleIdMap.containsKey("DepartmentId"))
                        {
                            departmentRole.append(departmentsService.findDepartmentById(departmentRoleIdMap.get("DepartmentId")).getName()+" ");
                        }
                        if (departmentRoleIdMap.containsKey("RoleId"))
                        {
                            departmentRole.append(roleService.findRoleById(departmentRoleIdMap.get("RoleId")).getName());
                        }
                        list.add(departmentRole.toString());
                        departmentRole = new StringBuffer();
                    }
                    String s = "{\"code\":\"Success\",\"msg\":\"登录成功\",\"token\":\""+token+"\",\"userId\":\""+user.getId()+"\",\"userFace\":\""+user.getUserface()+"\",\"userName\":\""+user.getUsername()+"\",\"roles\":\""+list+"\",\"departmentRoleId\":\""+list2+"\"}";
                    out.write(s);
                    out.flush();
                    out.close();
            }
        }).and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint).accessDeniedHandler(authenticationAccessDeniedHandler).and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return  new LogoutSuccessHandler();
    }

    @Bean
    public MyLogoutHandler MyLogoutHandler(){
        return  new MyLogoutHandler();
    }

}
