package com.example.dochubserver.service;

import com.alibaba.fastjson.JSON;
import com.example.dochubserver.bean.Doc;
import com.example.dochubserver.bean.DocPermission;
import com.example.dochubserver.bean.Power;
import com.example.dochubserver.repository.DocPermissionRepository;
import com.example.dochubserver.repository.DocRepository;
import com.example.dochubserver.utils.ResponseType;
import com.example.dochubserver.utils.UsuallyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocPermissionService {

    @Autowired
    DocPermissionRepository docPermissionRepository;

    @Autowired
    DocRepository docRepository;

    @Autowired
    DepartmentsService departmentsService;

    @Autowired
    RoleService roleService;

    /**
     * 添加文件权限
     * @param docId
     * @param roleId
     * @param departmentId
     * @param powerName
     * @return
     */
    public Map<String,Object> addDocPermission(Long docId,Long roleId,Long departmentId,String powerName)
    {
        Map<String,Object> map = new HashMap<>();
        DocPermission docPermission = new DocPermission();
        if (departmentId==null)
            docPermission.setDepartmentRoleId(UsuallyUtil.generateRoleNeedsId(roleId));
        else if (roleId == null)
            docPermission.setDepartmentRoleId(UsuallyUtil.generateDepartmentNeedsId(departmentId));
        else
            docPermission.setDepartmentRoleId(UsuallyUtil.generateDepartmentRoleNeedsId(departmentId,roleId));
        docPermission.setDocId(docId);

        List<DocPermission> docPermissions = docPermissionRepository.findByDocId(docId);
        if (docPermissions!=null)
        {
            for (DocPermission docPermission1:docPermissions)
            {
                if (docPermission1.getDepartmentRoleId().equals(docPermission.getDepartmentRoleId()))
                {
                    map.put("code",ResponseType.Error);
                    map.put("msg","该文件权限已经添加");
                    return map;
                }
            }
        }
        docPermissionRepository.save(docPermission);
        map.put("code",ResponseType.Success);
        map.put("msg","文件权限添加成功");
        map.put("powerName",powerName);
        return map;
    }

    /**
     * 修改文件权限
     * @return
     */
    public Map<String,Object> changePower(Long docId,Long userId,String[] powers)
    {
        Map<String,Object> map = new HashMap<>();
        Doc doc = docRepository.findById(docId).get();
        if (doc.getUploadUserid()!=userId)
        {
            map.put("code",ResponseType.Error);
            map.put("msg","无修改该文件的权限");
            return map;
        }
        int open = doc.getOpen();
        if (open==1)//原来是公开
        {
            if (powers.length==1&&powers[0].equals(""))
            {
                map.put("code",ResponseType.Error);
                map.put("msg","未进行修改");
                return map;
            }
            else
            {
                doc.setOpen(0);//设置为需要权限
                StringBuffer stringBuffer = new StringBuffer();
                for (String p:powers)
                {
                    Power power = JSON.parseObject(p,Power.class);
                    if (!power.departmentId.equals(""))
                        stringBuffer.append(departmentsService.findDepartmentById(Long.parseLong(power.departmentId)).getName());
                    if (!power.roleId.equals(""))
                        stringBuffer.append(roleService.findRoleById(Long.parseLong(power.roleId)).getName());
                    stringBuffer.append("  ");
                    addDocPermission(doc.getId(),!power.roleId.equals("")?Long.parseLong(power.roleId):null,!power.departmentId.equals("")?Long.parseLong(power.departmentId):null,power.powerName);
                }
                doc.setPermission(stringBuffer.toString());
                docRepository.save(doc);
                map.put("code",ResponseType.Success);
                map.put("msg","修改成功");
                map.put("permission",stringBuffer.toString());
                map.put("open",0);
                return map;
            }
        }
        else //原来是需要权限
        {
            List<DocPermission> permissionList = findByDocId(docId);
            for (int i=0;i<permissionList.size();i++)
            {
                DocPermission docPermission = permissionList.get(i);
                docPermissionRepository.delete(docPermission);
            }
            if (powers.length==1&&powers[0].equals(""))
            {
                doc.setPermission("");
                doc.setOpen(1);
                docRepository.save(doc);
                map.put("code",ResponseType.Success);
                map.put("msg","修改成功");
                map.put("permission","");
                map.put("open",1);
                return map;
            }
            else
            {
                StringBuffer stringBuffer = new StringBuffer();
                for (String p:powers)
                {
                    Power power = JSON.parseObject(p,Power.class);
                    stringBuffer.append(departmentsService.findDepartmentById(Long.parseLong(power.departmentId)));
                    stringBuffer.append(roleService.findRoleById(Long.parseLong(power.roleId)));
                    stringBuffer.append("  ");
                    addDocPermission(doc.getId(),Long.parseLong(power.roleId),Long.parseLong(power.departmentId),power.powerName);
                }
                doc.setPermission(stringBuffer.toString());
                docRepository.save(doc);
                map.put("code",ResponseType.Success);
                map.put("msg","修改成功");
                map.put("permission",stringBuffer.toString());
                map.put("open",0);
                return map;
            }
        }
    }

    public List<DocPermission> findByDocId(Long docId)
    {
        return docPermissionRepository.findByDocId(docId);
    }

}
