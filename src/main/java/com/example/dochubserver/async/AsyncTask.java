package com.example.dochubserver.async;

import com.example.dochubserver.service.*;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * 异步任务类
 */
@Component
public class AsyncTask {

    @Autowired
    LoginService loginService;

    @Autowired
    OwendRoleService owendRoleService;

    @Autowired
    UserService userService;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    RoleService roleService;

    @Autowired
    DocCategoryService docCategoryService;

    @Autowired
    FunctionPermissionService functionPermissionService;

    @Autowired
    FunctionService functionService;

    @Autowired
    DocService docService;

    /**
     * 注册
     * @param username
     * @param password
     * @return
     */
    @Async
    public Future<String> Register(String username, String password)
    {
        String response = UsuallyUtil.getJsonString(loginService.Register(username,password));
        return new AsyncResult<String>(response);
    }

    /**
     * 登出
     * @param request
     * @return
     */
    @Async
    public Future<String> Logout(HttpServletRequest request)
    {
        String response = UsuallyUtil.getJsonString(loginService.Logout(request));
        return new AsyncResult<String>(response);
    }


    /**
    /**
     * 查看是否有敏感词
     * @param username
     * @return
     */
    @Async
    public Future<String> checkSensitiveWord(String username)
    {
        String response = UsuallyUtil.getJsonString(loginService.CheckUsername(username));
        return new AsyncResult<String>(response);

    }

    /**
     * 给用户添加部门角色
     * @param userId
     * @param departmentId
     * @param roleId
     * @return
     */
    @Async
    public Future<String> addOwnedRole(String userId,String departmentId, String roleId)
    {
        String response = UsuallyUtil.getJsonString(owendRoleService.addOwendRole(userId,departmentId,roleId));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除用户所拥有的部门角色
     * @param ownedRoleId
     * @return
     */
    @Async
    public Future<String> deleteOwnedRole(String ownedRoleId)
    {
        String response = UsuallyUtil.getJsonString(owendRoleService.deleteOwendRole(ownedRoleId));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @Async
    public Future<String> deleteUser(String userId)
    {
        String response = UsuallyUtil.getJsonString(userService.deleteUser(userId));
        return new AsyncResult<String>(response);
    }

    @Async
    public Future<String> getUser()
    {
        String response = userService.getUser();
        return new AsyncResult<String>(response);
    }


    /**
     * 添加部门
     * @param departmentName
     * @param parentCode
     * @return
     */
    @Async
    public Future<String> addDepartment(String departmentName,String parentCode)
    {
        String response = UsuallyUtil.getJsonString(departmentsService.addDepartment(departmentName,parentCode));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除部门
     * @param departmentId
     * @return
     */
    @Async
    public Future<String> deleteDepartment(String departmentId)
    {
        String response = UsuallyUtil.getJsonString(departmentsService.deleteDepartment(Long.parseLong(departmentId)));
        return new AsyncResult<String>(response);
    }

    /**
     * 更改部门名称
     * @param departmentId
     * @param departmentName
     * @return
     */
    @Async
    public Future<String> changeDepartmentName(String departmentId,String departmentName)
    {
        String response = UsuallyUtil.getJsonString(departmentsService.changeDepartmantName(departmentName,Long.parseLong(departmentId)));
        return new AsyncResult<String>(response);
    }

    /**
     * 获取部门树形信息
     * @return
     */
    @Async
    public Future<String> getDepartmentsInfo()
    {
        String response = departmentsService.getDepartmentsInfo();
        return new AsyncResult<String>(response);
    }

    /**
     * 添加角色
     * @param roleName
     * @return
     */
    @Async
    public Future<String> addRole(String roleName)
    {
        String response = UsuallyUtil.getJsonString(roleService.addRole(roleName));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    @Async
    public Future<String> deleteRole(String roleId)
    {
        String response = UsuallyUtil.getJsonString(roleService.deleteRole(roleId));
        return new AsyncResult<String>(response);
    }

    /**
     * 更改角色名称
     * @param roleId
     * @return
     */
    @Async
    public Future<String> changeRoleName(String roleId,String roleName)
    {
        String response = UsuallyUtil.getJsonString(roleService.changeRoleName(roleId,roleName));
        return new AsyncResult<String>(response);
    }

    /**
     * 获得角色列表
     * @return
     */
    @Async
    public Future<String> getRoles()
    {
        String response = roleService.getRoles();
        return new AsyncResult<String>(response);
    }

    /**
     * 添加文件类别
     * @param docCategoryName
     * @param parentCode
     * @return
     */
    @Async
    public Future<String> addDocCategory(String docCategoryName, String parentCode)
    {
        String response = UsuallyUtil.getJsonString(docCategoryService.addDocCategory(docCategoryName,parentCode));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除文件类别
     * @param docCategoryId
     * @return
     */
    @Async
    public Future<String> deleteDocCategory(String docCategoryId)
    {
        String response = UsuallyUtil.getJsonString(docCategoryService.deleteDocCategory(Long.parseLong(docCategoryId)));
        return new AsyncResult<String>(response);
    }

    /**
     * 获取文件类别树形结构
     * @return
     */
    @Async
    public Future<String> getDocCategoryInfo()
    {
        String response = docCategoryService.getDocCategoryInfo();
        return new AsyncResult<String>(response);
    }

    /**
     * 添加功能权限
     * @param functionId
     * @param departmentId
     * @param roleId
     * @return
     */
    @Async
    public Future<String> addFunctionPermission(String functionId,String departmentId,String roleId)
    {
        String response = UsuallyUtil.getJsonString(functionPermissionService.addFunctionPermission(functionId,departmentId,roleId));
        return new AsyncResult<String>(response);
    }

    /**
     * 删除功能权限
     * @param functionPermissionId
     * @return
     */
    @Async
    public Future<String> deleteFunctionPermission(String functionPermissionId)
    {
        String response = UsuallyUtil.getJsonString(functionPermissionService.deleteFunctionPermission(functionPermissionId));
        return new AsyncResult<String>(response);
    }

    /**
     * 获取功能信息
     * @return
     */
    @Async
    public Future<String> getFunctionPermissionInfo()
    {
        String response = functionService.getFunctionPermissionInfo();
        return new AsyncResult<String>(response);
    }

    /**
     * 获取该用户可用的功能
     * @param userId
     * @return
     */
    @Async
    public Future<String> getUsableFunction(String userId)
    {
        String response = UsuallyUtil.getJsonString(functionService.getUsableFunction(userId));
        return new AsyncResult<String>(response);
    }

    /**
     * 上传文件
     * @param request
     * @param userId
     * @param docCategoryId
     * @param powers
     * @return
     */
    @Async
    public Future<String> uploadDoc(HttpServletRequest request, Long userId, Long docCategoryId, String[] powers)
    {
        String response = UsuallyUtil.getJsonString(docService.uploadDoc(request,userId,docCategoryId,powers));
        return new AsyncResult<String>(response);
    }


    /**
     * 文件下载
     * @param httpresponse
     * @param docId
     * @param userId
     * @return
     */
    @Async
    public Future<String> downloadDoc(HttpServletResponse httpresponse, Long docId, Long userId)
    {
        String response = UsuallyUtil.getJsonString(docService.downloadDoc(httpresponse,docId,userId));
        return new AsyncResult<String>(response);
    }


}
