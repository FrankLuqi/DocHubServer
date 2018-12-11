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
public class DepartmentManageController {

    @Autowired
    AsyncTask asyncTask;

    /**
     * 添加部门
     * @param request
     * @return
     */
    @PostMapping(value = "/DepartmentManage/addDepartment")
    @ResponseBody
    public String addDepartment(HttpServletRequest request)
    {
        String departmentName = request.getParameter("departmentName");
        String parentCode = request.getParameter("parentCode");
        if (departmentName!=null&&parentCode!=null)
        {
            Future<String> future = asyncTask.addDepartment(departmentName,parentCode);
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
     * 删除部门
     * @param request
     * @return
     */
    @PostMapping(value = "/DepartmentManage/deleteDepartment")
    @ResponseBody
    public String deleteDepartment(HttpServletRequest request)
    {
        String departmentId = request.getParameter("departmentId");
        if (departmentId!=null)
        {
            Future<String> future = asyncTask.deleteDepartment(departmentId);
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
     * 获取部门树形信息
     * @return
     */
    @PostMapping(value = "/getDepartmentsInfo")
    @ResponseBody
    public String getDepartmentsInfo()
    {
        Future<String> future = asyncTask.getDepartmentsInfo();
        try {
            return future.get();
        }catch (Exception e)
        {
            return "错误"+e.getMessage();
        }
    }

}
