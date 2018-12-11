package com.example.dochubserver.service.springSecurity;

import com.example.dochubserver.service.DepartmentsService;
import com.example.dochubserver.service.RoleService;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentsService departmentsService;

    /**
     * @param authentication 用户所具有的的权限，由User类中的getAuthorities方法返回
     * @param o
     * @param collection 该功能所需要的权限
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        System.out.println("过滤器：UrlAccessDecisionManager");
        Iterator<ConfigAttribute> iterator = collection.iterator();
        while (iterator.hasNext())
        {
            ConfigAttribute ca = iterator.next();
            //当期请求所需要的权限（部门角色id）
            String DepartmentRoleIdNeeds = ca.getAttribute();
//            HashMap<String,Integer> map = UsuallyUtil.parseDepartmentRoleId(DepartmentRoleId);
//            String DepartmentNeeds = null;
//            String RoleNeeds = null;
//            if (map.containsKey("DepartmentId"))
//            {
//                //获取用户所需要在的部门名称
//                DepartmentNeeds = departmentsService.findDepartmentById(map.get("DepartmentId")).getName();
//            }
//            if (map.containsKey("RoleId"))
//            {
//                //获取用户所需要具有的角色名称
//                RoleNeeds = roleService.findRoleById(map.get("RoleId")).getName();
//            }
            if ("ROLE_LOGIN".equals(DepartmentRoleIdNeeds))
            {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("未登录");
                } else
                    return;
//                if (SecurityContextHolder.getContext().getAuthentication()!=null)
//                {
//                    return;
//                }
//                else
//                    throw new BadCredentialsException("未登录");
            }
            //在用户已具有的部门角色中进行查找
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (UsuallyUtil.hasPower(authority.getAuthority(),DepartmentRoleIdNeeds)) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
