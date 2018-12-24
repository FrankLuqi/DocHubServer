package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Function;
import com.example.dochubserver.bean.FunctionPermission;
import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.repository.FunctionPermissionRepository;
import com.example.dochubserver.repository.FunctionRepository;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FunctionService {

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    FunctionPermissionRepository functionPermissionRepository;


    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    RoleService roleService;

    @Autowired
    UserService userService;

    public List<Function> findByCategory(Integer category)
    {
        return functionRepository.findByCategory(category);
    }

    public List<Function> findAll()
    {
        return functionRepository.findAll();
    }

    public Function findByFunctionId(Long id)
    {
        return functionRepository.findById(id).get();
    }

    /**
     * 添加功能
     * @param category
     * @param name
     * @param url
     * @param identity
     * @param status
     */
    public void addFunction(int category,String name,String url,String identity,int status)
    {
        Function function = new Function();
        function.setCategory(category);
        function.setName(name);
        function.setUrl(url);
        function.setIdentity(identity);
        function.setStatus(status);

        functionRepository.save(function);
    }

    /**
     * 获取功能权限信息
     * @return
     */
    public String getFunctionPermissionInfo()
    {
        JSONArray jsonArray = new JSONArray();
        List<Function> functionList = functionRepository.findAll();
        for (Function function:functionList)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",function.getId());
            jsonObject.put("name",function.getName());
            jsonObject.put("identity",function.getIdentity());
            StringBuffer roleName2 = new StringBuffer();
            JSONArray array = new JSONArray();
            if (functionPermissionRepository.findByFunctionId(function.getId())!=null)
            {
                for (FunctionPermission functionPermission:functionPermissionRepository.findByFunctionId(function.getId()))
                {
                    JSONObject object = new JSONObject();
                    object.put("id",functionPermission.getId());
                    StringBuffer roleName = new StringBuffer();
                    Map<String,Long> map = UsuallyUtil.parseDepartmentRoleId(functionPermission.getDepartmentRoleId());
                    if (map.containsKey("DepartmentId"))
                        roleName.append(departmentsService.findDepartmentById(map.get("DepartmentId")).getName()+" ");
                    if (map.containsKey("RoleId"))
                        roleName.append(roleService.findRoleById(map.get("RoleId")).getName());
                    object.put("rolename",roleName);
                    roleName.append(" ");
                    roleName2.append(roleName);
                    array.add(object.toString());
                }
                jsonObject.put("role",array);
            }
            else
                jsonObject.put("role","");
            jsonObject.put("rolename",roleName2.toString());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();

    }

    /**
     * 根据用户id 返回对于该用户各个功能是否有权限，前端根据返回的各个功能是否有权限，来渲染页面
     * @param userId
     * @return
     */
    public Map<String,Object> getUsableFunction(String userId)
    {
        Map<String,Object> map = new HashMap<>();
        User user = userService.findByUserId(Long.parseLong(userId));
        if (user!=null)
        {
            List<Function> functions =functionRepository.findAll();
            for (Function function:functions)
            {
                List<FunctionPermission> functionPermissionList = functionPermissionRepository.findByFunctionId(function.getId());
                if (functionPermissionList!=null)
                {
                    for (FunctionPermission functionPermission:functionPermissionList)
                    {
                        if (map.containsKey(function.getIdentity()))
                            break;
                        for (OwnedRole ownedRole:user.getOwnedRoles())
                        {
                            if (UsuallyUtil.hasPower(ownedRole.getDepartmentRoleId(),functionPermission.getDepartmentRoleId()))
                            {
                                map.put(function.getIdentity(),true);
                                break;
                            }
                        }
                    }
                }
                if (!map.containsKey(function.getIdentity()))
                    map.put(function.getIdentity(),false);
            }
            map.put("code",ResponseType.Success);
            map.put("msg","获取成功");
        }
        else
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该用户不存在");
        }
        return map;
    }

}
