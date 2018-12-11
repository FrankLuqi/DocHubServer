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
public class UserManageController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 给用户添加部门角色
     * @param request
     * @return
     */
    @PostMapping(value = "/UserManage/addOwnedRole")
    @ResponseBody
    public String addOwnedRole(HttpServletRequest request)
    {
        String userId = request.getParameter("userId");
        String departmentId = request.getParameter("departmentId");
        String roleId = request.getParameter("roleId");
        if (userId!=null&&departmentId!=null&&roleId!=null)
        {
            Future<String> future = asyncTask.addOwnedRole(userId,departmentId,roleId);
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
     * 删除用户所具有的部门角色
     * @param request
     * @return
     */
    @PostMapping(value = "/UserManage/deleteOwnedRole")
    @ResponseBody
    public String deleteOwnedRole(HttpServletRequest request)
    {
        String ownedRoleId = request.getParameter("ownedRoleId");
        if (ownedRoleId!=null)
        {
            Future<String> future = asyncTask.deleteOwnedRole(ownedRoleId);
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
     * 删除用户
     * @param request
     * @return
     */
    @PostMapping(value = "/UserManage/deleteUser")
    @ResponseBody
    public String deleteUser(HttpServletRequest request)
    {
        String userId = request.getParameter("userId");
        if (userId!=null)
        {
            Future<String> future = asyncTask.deleteUser(userId);
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
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/UserManage/getUser")
    @ResponseBody
    public String getUser(HttpServletRequest request)
    {
        Future<String> future = asyncTask.getUser();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
