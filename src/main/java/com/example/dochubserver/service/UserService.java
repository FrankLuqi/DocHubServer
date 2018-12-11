package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Doc;
import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.repository.UserRepository;
import com.example.dochubserver.utils.JedisAdapter;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    DocService docService;

    @Autowired
    OwendRoleService owendRoleService;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    RoleService roleService;

    public User save(User user)
    {
        return userRepository.save(user);
    }

    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username);
    }

    public User findByUserId(long userId)
    {
        return userRepository.findById(userId).get();
    }

    /**
     * 更改用户最近修改时间
     * @param date
     * @param userId
     */
    public void modifyUserChangeDate(Date date,long userId)
    {
        jedisAdapter.hset("UserChangeDate",String.valueOf(userId),String.valueOf(date.getTime()));
    }

    /**
     * 获取用户最近修改时间
     * @param userId
     * @return
     */
    public long getUserChangeDate(String userId)
    {
        String time = jedisAdapter.hget("UserChangeDate",userId);
        return time == null ? 0 : Long.parseLong(time);
    }

    /**
     * 删除用户，级联删除该用户上传的文件和该用户具有的部门角色
     * @param userId
     * @return
     */
    public Map<String,Object> deleteUser(String userId)
    {
        Map<String,Object> map = new HashMap<>();
        try{
            userRepository.deleteById(Long.parseLong(userId));
        }catch (Exception e)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","删除用户失败");
            return map;
        }
        //删除该用户所上传的文件
        List<Doc> docs = docService.findDocsByUploadUserId(Long.parseLong(userId));
        if (docs!=null)
        {
            for (Doc doc : docs)
            {
                docService.deleteDocById(doc.getId());
            }
        }
        //删除该用户所具有的部门角色
        List<OwnedRole> ownedRoleList = owendRoleService.findRoleByUserId(Long.parseLong(userId));
        if (ownedRoleList!=null)
        {
            for (OwnedRole ownedRole : ownedRoleList)
            {
                owendRoleService.delete(ownedRole);
            }
        }
        map.put("code",ResponseType.Success);
        map.put("msg","删除用户成功，同时该用户上传的文件也被删除");
        return map;
    }

    public String getUser()
    {
        JSONArray jsonArray = new JSONArray();
        List<User> userList = userRepository.findAll();
        for (User user : userList)
        {
            JSONObject object = new JSONObject();
            object.put("name",user.getUsername());
            JSONArray array = new JSONArray();
            if (user.getOwnedRoles()!=null)
            {
                for (OwnedRole ownedRole : user.getOwnedRoles())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",ownedRole.getId());
                    StringBuffer roleName = new StringBuffer();
                    Map<String,Long> map = UsuallyUtil.parseDepartmentRoleId(ownedRole.getDepartmentRoleId());
                    if (map.containsKey("DepartmentId"))
                        roleName.append(departmentsService.findDepartmentById(map.get("DepartmentId"))+" ");
                    if (map.containsKey("RoleId"))
                        roleName.append(roleService.findRoleById(map.get("RoleId")));
                    jsonObject.put("name",roleName);

                    array.add(jsonObject);
                }
                object.put("role",array.toJSONString());
            }
            else
                object.put("role","");

            object.put("id",user.getId());
            jsonArray.add(object);
        }
        return jsonArray.toJSONString();
    }


}
