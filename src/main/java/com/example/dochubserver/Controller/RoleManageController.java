package com.example.dochubserver.Controller;


import com.example.dochubserver.async.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;

@Controller
@CrossOrigin
public class RoleManageController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 添加角色
     * @param request
     * @return
     */
    @PostMapping(value = "/RoleManage/addRole")
    @ResponseBody
    public String addRole(HttpServletRequest request)
    {
        String roleName = request.getParameter("roleName");
        if (roleName!=null)
        {
            Future<String> future = asyncTask.addRole(roleName);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        return null;
    }

    /**
     * 删除角色
     * @param request
     * @return
     */
    @PostMapping(value = "/RoleManage/deleteRole")
    @ResponseBody
    public String deleteRole(HttpServletRequest request)
    {
        String roleId = request.getParameter("roleId");
        if (roleId!=null)
        {
            Future<String> future = asyncTask.deleteRole(roleId);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        return null;
    }

    /**
     * 更改角色名称
     * @param request
     * @return
     */
    @PostMapping(value = "/RoleManage/changeRoleName")
    @ResponseBody
    public String changeRoleName(HttpServletRequest request)
    {
        String roleId = request.getParameter("roleId");
        String roleName = request.getParameter("roleName");
        if (roleId!=null&&roleName!=null)
        {
            Future<String> future = asyncTask.changeRoleName(roleId,roleName);
            try {
                return future.get();
            }catch (Exception e)
            {
                return "错误"+e.getMessage();
            }
        }
        return null;
    }

    @PostMapping(value = "/getRoles")
    @ResponseBody
    public String getRoles(HttpServletRequest request)
    {
        Future<String> future = asyncTask.getRoles();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
