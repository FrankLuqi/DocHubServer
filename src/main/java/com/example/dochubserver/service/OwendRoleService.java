package com.example.dochubserver.service;

import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import com.example.dochubserver.repository.OwnedRoleRepository;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class OwendRoleService {

    @Autowired
    OwnedRoleRepository ownedRoleRepository;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    DepartmentsService departmentsService;

    public OwnedRole save(OwnedRole ownedRole)
    {
        return ownedRoleRepository.save(ownedRole);
    }


    public List<OwnedRole> findRoleByUser(User user)
    {
        return ownedRoleRepository.findByUser(user);
    }

    public List<OwnedRole> findRoleByUserId(long userId)
    {
        return ownedRoleRepository.findByUserId(userId);
    }

    public List<OwnedRole> findByDepartmentId(String DId)
    {
        return ownedRoleRepository.findByDepartmentId(DId);
    }

    public void delete(OwnedRole ownedRole)
    {
        ownedRoleRepository.delete(ownedRole);
    }

    /**
     * 给用户添加部门角色 如管理员，销售部员工
     * @param userId
     * @param departmentId
     * @param roleId
     * @return
     */
    public Map<String,Object> addOwendRole(String userId, String departmentId, String roleId)
    {
        Map<String,Object> map = new HashMap<>();
        OwnedRole ownedRole = new OwnedRole();
        User user = userService.findByUserId(Long.parseLong(userId));

        if (roleService.findRoleById(Long.parseLong(roleId)) == null)//角色不存在
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该角色不存在 添加部门角色失败");
            return map;
        }

        if (user!=null)
        {
            ownedRole.setUser(user);
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
                map.put("msg","参数输入不合法 添加部门角色失败");
                return map;
            }
            if (ownedRoleRepository.findByUserIdAndDepartmentRoleId(Long.parseLong(userId),departmentRoleId)!=null)
            {
                map.put("code",ResponseType.Error);
                map.put("msg","该用户已存在该部门角色 添加部门角色失败");
                return map;
            }

            ownedRole.setDepartmentRoleId(departmentRoleId);
            ownedRole = save(ownedRole);
            map.put("code",ResponseType.Success);
            map.put("msg","添加成功");
            map.put("ownedRoleId",ownedRole.getId());
        }
        else
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该用户不存在 添加部门角色失败");
        }
        return map;
    }

    /**
     * 删除用户已有的部门角色
     * @param owendRoleId
     * @return
     */
    public Map<String,Object> deleteOwendRole(String owendRoleId)
    {
        Map<String,Object> map = new HashMap<>();
        OwnedRole ownedRole = ownedRoleRepository.findById(Long.parseLong(owendRoleId)).get();
        if (owendRoleId == null)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","该部门角色不存在 删除部门角色失败");
            return map;
        }
        else
        {
            ownedRoleRepository.deleteById(Long.parseLong(owendRoleId));
            map.put("code",ResponseType.Success);
            map.put("msg","删除成功");
            return map;
        }
    }
}
