package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Function;
import com.example.dochubserver.bean.FunctionPermission;
import com.example.dochubserver.repository.FunctionPermissionRepository;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FunctionPermissionService {

    @Autowired
    FunctionPermissionRepository functionPermissionRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    FunctionService functionService;

    public List<FunctionPermission> findFunctionPermission(long functionID)
    {
        return functionPermissionRepository.findByFunctionId(functionID);
    }

    public FunctionPermission findByFunctionIdAndDepartmentRoleId(long functionId,String departmentRoleId)
    {
        return functionPermissionRepository.findByFunctionIdAndDepartmentRoleId(functionId,departmentRoleId);
    }

    /**
     * 添加功能权限
     * @param functionId
     * @param departmentId
     * @param roleId
     * @return
     */
    public Map<String,Object> addFunctionPermission(String functionId,String departmentId,String roleId)
    {
        Map<String,Object> map = new HashMap<>();
       if (functionService.findByFunctionId(Long.parseLong(functionId)) ==null)
       {
           map.put("code",ResponseType.Error);
           map.put("msg","该功能不存在 添加权限失败");
           return map;
       }

        if (roleService.findRoleById(Long.parseLong(roleId)) == null)//角色不存在
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该角色不存在 添加部门角色失败");
            return map;
        }
        String departmentRoleId = null;
        if (departmentId.equals("") && !roleId.equals(""))//只有角色 如实习生，管理员
            departmentRoleId = UsuallyUtil.generateRoleNeedsId(Long.parseLong(roleId));
        else if (!departmentId.equals("") && !roleId.equals(""))// 既有角色也有部门 如研发部 经理
        {
            if (departmentsService.findDepartmentById(Long.parseLong(departmentId)) == null)//该部门不存在
            {
                map.put("code",ResponseType.Error);
                map.put("msg","该部门不存在 添加部门角色失败");
                return map;
            }
            departmentRoleId = UsuallyUtil.generateDepartmentRoleNeedsId(Long.parseLong(departmentId),Long.parseLong(roleId));
        }
        else // 不能只有部门没有角色，也不能二者都没有
        {
            map.put("code",ResponseType.Error);
            map.put("msg","参数输入不合法 添加功能权限失败");
            return map;
        }
        if (findByFunctionIdAndDepartmentRoleId(Long.parseLong(functionId),departmentRoleId) != null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该功能已存在该权限 添加权限失败");
            return map;
        }
        FunctionPermission functionPermission = new FunctionPermission();
        functionPermission.setFunctionId(Long.parseLong(functionId));
        functionPermission.setDepartmentRoleId(departmentRoleId);
        functionPermission = functionPermissionRepository.save(functionPermission);
        map.put("code",ResponseType.Success);
        map.put("msg","添加成功");
        map.put("functionPermissionId",functionPermission.getId());
        return map;
    }

    /**
     * 删除功能权限
     * @param functionId
     * @return
     */
    public Map<String,Object> deleteFunctionPermission(String functionId)
    {
        Map<String,Object> map = new HashMap<>();
        FunctionPermission functionPermission = functionPermissionRepository.findById(Long.parseLong(functionId)).get();
        if (functionPermission == null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该功能权限不存在，删除失败");
            return map;
        }
        functionPermissionRepository.deleteById(Long.parseLong(functionId));
        map.put("code",ResponseType.Success);
        map.put("msg","删除成功");
        return map;
    }


}
