package com.example.dochubserver.service.springSecurity;

import com.example.dochubserver.bean.Function;
import com.example.dochubserver.bean.FunctionPermission;
import com.example.dochubserver.service.FunctionPermissionService;
import com.example.dochubserver.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;


/**
 * 获取当前的请求地址获取当前请求地址需要的用户角色
 */
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    FunctionService functionService;

    @Autowired
    FunctionPermissionService functionPermissionService;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        System.out.println("过滤器：UrlFilterInvocationSecurityMetadataSource");
        String requestUrl = ((FilterInvocation)o).getRequestUrl();//获取当前请求的url地址
        if ("/login".equals(requestUrl) || "/user".equals(requestUrl) || "/checkUsername".equals(requestUrl))
        {
            return null;//登录页不需要任何权限
        }
        List<Function> functions = functionService.findAll();//获取功能表中的所有功能

        for (Function function : functions)
        {
            if (antPathMatcher.match(function.getUrl(),requestUrl))
            {
                List<FunctionPermission> permissionList = functionPermissionService.findFunctionPermission(function.getId());
                if (permissionList.size()>0)
                {
                    int size = permissionList.size();
                    String[] rolesName = new String[size];
                    for (int i=0;i<size;i++)
                    {
                        rolesName[i] = permissionList.get(i).getDepartmentRoleId();//获取部门角色id
                    }
                    return SecurityConfig.createList(rolesName);
                }
            }
        }
        return SecurityConfig.createList("ROLE_LOGIN");//没有匹配到资源默认登录后即可访问
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
