package com.example.dochubserver.service;

import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class LoginService {

    @Autowired
    UserService userService;

    @Autowired
    OwendRoleService owendRoleService;

    @Autowired
    SensitiveWordsFilter sensitiveWordsFilter;


    /**
     * 注册
     * @param username
     * @param password
     * @return
     */
    public Map<String,Object> Register(String username,String password)
    {
        Map<String,Object> map = new HashMap<>();
        if (username==null||password==null)
        {
            map.put("code",ResponseType.Error);
            map.put("message","输入错误");
            return map;
        }
        User user = userService.findByUsername(username);
        if (user!=null)
        {
            map.put("code",ResponseType.Error);
            map.put("message","用户名已被注册");
            return map;
        }
        if (username.length()>15||username.length()<2)
        {
            map.put("code",ResponseType.Error);
            map.put("message","用户名长度违规");
            return map;
        }
        if (password.length()>20||password.length()<6)
        {
            map.put("code",ResponseType.Error);
            map.put("message","密码长度违规");
            return map;
        }

        if (sensitiveWordsFilter.HasSensitiveWord(username))
        {
            map.put("code",ResponseType.Error);
            map.put("message","用户名包含敏感词汇");
        }
        User newUser = new User();
        newUser.setUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        newUser.setPassword(encoder.encode(password));
        newUser.setUserface(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        newUser = userService.save(newUser);

        OwnedRole ownedRole = new OwnedRole();
        ownedRole.setUser(newUser);
        ownedRole.setDepartmentRoleId(UsuallyUtil.generateRoleNeedsId(1));
        owendRoleService.save(ownedRole);

        map.put("code",ResponseType.Success);
        map.put("message","注册成功");
        return map;
    }

    public Map<String,Object> Logout(HttpServletRequest httpServletRequest)
    {
        Map<String,Object> map = new HashMap<>();
        String userId = (String) httpServletRequest.getAttribute("userId");
        userService.modifyUserChangeDate(new Date(),Long.parseLong(userId));
        map.put("code",ResponseType.Success);
        map.put("msg","退出登录成功");
        return map;
    }

    /**
     * 提供接口判断当前用户名是否已被注册、当前用户名是否包含敏感字符
     * @param username
     * @return
     */
    public Map<String,Object> CheckUsername(String username)
    {
        Map<String,Object> map = new HashMap<>();
        User user = userService.findByUsername(username);
        if (user!=null)
        {
            map.put("code",ResponseType.Error);
            map.put("message","该用户名已被注册");
            return map;
        }
        if (sensitiveWordsFilter.HasSensitiveWord(username))
        {
            map.put("code",ResponseType.Error);
            map.put("message","用户名中存在敏感词汇");
            return map;
        }
        map.put("code",ResponseType.Success);
        map.put("message","该用户名合法");
        return map;
    }

}
