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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

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

    @Value("${com.DOMAIN}")
    private String DOMAIN;


    @Value("${com.docDir}")
    private String docDir;

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
            StringBuffer userRoles = new StringBuffer();
            userRoles.append(" ");
            if (user.getOwnedRoles()!=null)
            {
                for (OwnedRole ownedRole : user.getOwnedRoles())
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",ownedRole.getId());
                    StringBuffer roleName = new StringBuffer();
                    Map<String,Long> map = UsuallyUtil.parseDepartmentRoleId(ownedRole.getDepartmentRoleId());
                    if (map.containsKey("DepartmentId"))
                        roleName.append(departmentsService.findDepartmentById(map.get("DepartmentId")).getName()+" ");
                    if (map.containsKey("RoleId"))
                        roleName.append(roleService.findRoleById(map.get("RoleId")).getName());
                    jsonObject.put("name",roleName);
                    userRoles.append(roleName);
                    array.add(jsonObject);
                }
                object.put("role",array.toJSONString());
            }
            else
                object.put("role","");

            object.put("id",user.getId());
            object.put("userRole",userRoles.toString());
            jsonArray.add(object);
        }
        return jsonArray.toJSONString();
    }

    /**
     * 修改用户头像
     * @param request
     * @param userId
     * @return
     */
    public Map<String,Object> uoloadUserface(HttpServletRequest request, Long userId)
    {
        Map<String,Object> map = new HashMap<>();
        Part part = null;
        try{
            part = request.getPart("file");
        }catch (Exception e)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","修改失败");
            return map;
        }
        //得到上传的文件名找到图片后缀名点的位置
        int dotpos = part.getSubmittedFileName().lastIndexOf(".");
        if (dotpos<=0)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","修改失败");
            return map;
        }
        //得到后缀名并将大写转换为小写
        String fileext = part.getSubmittedFileName().substring(dotpos+1).toLowerCase();
        String filename = UUID.randomUUID().toString().replaceAll("-","")+"."+fileext;
        try{
            //保存文件到本地
            Files.copy(part.getInputStream(),new File(docDir+filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","修改失败");
            return map;
        }
        User user = userRepository.findById(userId).get();
        user.setUserface(DOMAIN+"userface?name="+filename);
        userRepository.save(user);
        map.put("code",ResponseType.Success);
        map.put("msg","修改成功");
        map.put("userFace",DOMAIN+"userface?name="+filename);
        return map;
    }


}
