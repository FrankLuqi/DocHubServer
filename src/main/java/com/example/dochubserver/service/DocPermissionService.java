package com.example.dochubserver.service;

import com.example.dochubserver.bean.DocPermission;
import com.example.dochubserver.repository.DocPermissionRepository;
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
        if (departmentId.equals(""))
            docPermission.setDepartmentRoleId(UsuallyUtil.generateRoleNeedsId(roleId));
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

    public List<DocPermission> findByDocId(Long docId)
    {
        return docPermissionRepository.findByDocId(docId);
    }

}
