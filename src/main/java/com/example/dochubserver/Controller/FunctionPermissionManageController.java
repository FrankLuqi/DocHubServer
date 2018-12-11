package com.example.dochubserver.Controller;

import com.example.dochubserver.async.AsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Future;

@CrossOrigin
@Controller
public class FunctionPermissionManageController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 添加功能权限
     * @param request
     * @return
     */
    @PostMapping(value = "/FunctionPermissionManage/addFunctionPermission")
    @ResponseBody
    public String addFunctionPermission(HttpServletRequest request)
    {
        String functionId = request.getParameter("functionId");
        String departmentId = request.getParameter("departmentId");
        String roleId = request.getParameter("roleId");
        if (functionId!=null&&departmentId!=null&&roleId!=null)
        {
            Future<String> future = asyncTask.addFunctionPermission(functionId,departmentId,roleId);
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
     * 删除功能权限
     * @param request
     * @return
     */
    @PostMapping(value = "/FunctionPermissionManage/deleteFunctionPermission")
    @ResponseBody
    public String deleteFunctionPermission(HttpServletRequest request)
    {
        String functionPermissionId = request.getParameter("functionPermissionId");

        if (functionPermissionId!=null)
        {
            Future<String> future = asyncTask.deleteFunctionPermission(functionPermissionId);
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
     * 获取功能信息
     * @param request
     * @return
     */
    @PostMapping(value = "/FunctionPermissionManage/getFunctionPermissionInfo")
    @ResponseBody
    public String getFunctionPermissionInfo(HttpServletRequest request)
    {
        Future<String> future = asyncTask.getFunctionPermissionInfo();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
