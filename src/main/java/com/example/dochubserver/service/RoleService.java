package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Role;
import com.example.dochubserver.repository.RoleRepository;
import com.example.dochubserver.utils.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role save(Role role)
    {
        return roleRepository.save(role);
    }

    public List<Role> findAll()
    {
        return roleRepository.findAll();
    }

    public Role findRoleByName(String name)
    {
        return roleRepository.findByName(name);
    }

    public void deleteRole(Role role){
        roleRepository.delete(role);
    }

    public Role findRoleById(long id)
    {
        return roleRepository.findById(id).get();
    }

    /**
     * 添加角色
     * @param roleName
     * @return
     */
    public Map<String,Object> addRole(String roleName)
    {
        Map<String,Object> map = new HashMap<>();
        if (roleRepository.findByName(roleName)!=null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","添加失败，该角色已经存在");
            return map;
        }
        Role role = new Role();
        role.setName(roleName);
        role = save(role);
        map.put("code",ResponseType.Success);
        map.put("msg","添加角色成功");
        map.put("Id",role.getId());
        map.put("name",role.getName());
        return map;
    }

    /**
     * 删除角色
     * @param roleId
     * @return
     */
    public Map<String,Object> deleteRole(String roleId)
    {
        Map<String,Object> map = new HashMap<>();
        Role role = findRoleById(Long.parseLong(roleId));
        if (role==null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","删除失败，该角色不存在");
            return map;
        }
        roleRepository.delete(role);
        map.put("code",ResponseType.Success);
        map.put("msg","删除角色成功");
        return map;
    }

    public Map<String,Object> changeRoleName(String roleId,String roleName)
    {
        Map<String,Object> map = new HashMap<>();

        if (roleName.equals(""))
        {
            map.put("code",ResponseType.Error);
            map.put("msg","更改角色名失败，参数输入错误");
            return map;
        }

        Role role = findRoleById(Long.parseLong(roleId));
        if (role==null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","更改角色名失败，该角色不存在");
            return map;
        }
        role.setName(roleName);
        roleRepository.save(role);
        map.put("code",ResponseType.Success);
        map.put("msg","更改角色角色名成功");
        return map;
    }

    /**
     * 获得角色列表
     * @return
     */
    public String getRoles()
    {
        List<Role> roleList = roleRepository.findAll();
        JSONArray jsonArray = new JSONArray();
        for (Role role : roleList)
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",role.getName());
            jsonObject.put("id",role.getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }
}
