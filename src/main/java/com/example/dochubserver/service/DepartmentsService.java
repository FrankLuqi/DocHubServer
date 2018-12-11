package com.example.dochubserver.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.Departments;
import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.repository.DepartmensRepository;
import com.example.dochubserver.utils.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DepartmentsService {

    @Autowired
    DepartmensRepository departmensRepository;

    @Autowired
    OwendRoleService owendRoleService;

    public Departments findDepartmentById(long id)
    {
        return departmensRepository.findById(id).get();
    }

    public Departments findDepartmentByCode(String code)
    {
        return departmensRepository.findByCode(code);
    }

    public Departments save(Departments departments)
    {
        return departmensRepository.save(departments);
    }

    public void delete(long id)
    {
        departmensRepository.deleteById(id);
    }

    /**
     * 生成部门编码 如 1D15D16D
     * @param parentCode
     * @param departmentId
     * @return
     */
    public String generateDepartmentCode(String parentCode, long departmentId)
    {
        if (parentCode.equals(""))
            return String.valueOf(departmentId)+"D";
        else
            return parentCode+String.valueOf(departmentId)+"D";
    }

    /**
     * 添加部门
     * @param departmentName
     * @param parentCode
     * @return
     */
    public Map<String,Object> addDepartment(String departmentName, String parentCode)
    {
        Departments departments = new Departments();
        departments.setName(departmentName);
        departments = save(departments);
        long id = departments.getId();
        String code = generateDepartmentCode(parentCode,id);//生成部门编码
        departments.setCode(code);
        save(departments);

        Map<String,Object> map = new HashMap<>();
        map.put("id",String.valueOf(id));
        map.put("departmentCode",code);
        map.put("name",departmentName);
        map.put("code",ResponseType.Success);
        map.put("msg","添加部门成功");
        return map;
    }

    /**
     * 删除部门，需要将牵扯到的用户拥有的部门角色id删除，如删除研发部，就要删除研发部经理
     * @param id 部门id
     */
    public Map<String,Object> deleteDepartment(long id)
    {
        Map<String ,Object> map = new HashMap<>();
        try{
            delete(id);
            //将用户角色id删除
            List<OwnedRole> ownedRoleList = owendRoleService.findByDepartmentId("DId"+id+"%");
            for (OwnedRole ownedRole:ownedRoleList)
            {
                owendRoleService.delete(ownedRole);
            }
            StringBuffer childrenName = new StringBuffer();//存储其被级联删除的子节点名称
            List<Departments> list = departmensRepository.findAll();
            for (Departments d: list)
            {
                String code = d.getCode();
                String[] codes = code.split("D");
                for (String s: codes)
                {
                    if (s.equals(String.valueOf(id)))
                    {
                        List<OwnedRole> ownedRoleList2 = owendRoleService.findByDepartmentId("DId"+id+"%");
                        for (OwnedRole ownedRole:ownedRoleList2)
                        {
                            owendRoleService.delete(ownedRole);
                        }
                        childrenName.append(d.getName()+" ");
                        delete(d.getId());//级联删除
                        break;
                    }
                }
            }
            map.put("code","Success");
            if (childrenName.toString().equals(""))
                map.put("msg","删除成功，具有该部门角色的用户该部门角色也已被删除");
            else
                map.put("msg","删除成功，具有该部门角色的用户该部门角色也已被删除，以下子节点也被删除 "+childrenName);
        }catch (Exception e)
        {
            //防止该id不存在
            map.put("code","Error");
            map.put("msg","删除失败");
        }
        return map;
    }

    /**
     * 更改部门名称
     * @param name
     * @param id
     */
    public Map<String,Object> changeDepartmantName(String name, long id)
    {
        Map<String,Object> map = new HashMap<>();
        Departments departments = findDepartmentById(id);
        if (departments!=null)
        {
            departments.setName(name);
            save(departments);
            map.put("code",ResponseType.Success);
            map.put("msg","更改部门名称成功");
            return map;
        }
        map.put("code",ResponseType.Error);
        map.put("msg","修改失败，该部门不存在");
        return map;
    }

    /**
     * 获取部门树形信息
     * @return
     */
    public String getDepartmentsInfo()
    {
        //用hashmap构建部门前缀树
        Map<String,List<Departments>> nodeMap = new HashMap<>();//map存储父节点，该节点下的子节点列表

        List<Departments> departmentsList = departmensRepository.findAll();
        for (Departments departments : departmentsList)
        {
            String code = departments.getCode();
            String[] codes = code.split("D");
            if (codes.length==1)//说明这个部门没有父节点
            {
                if (nodeMap.get("0") == null)
                {
                    List<Departments> nodelist = new ArrayList<>();
                    nodelist.add(departments);
                    nodeMap.put("0",nodelist);
                }
                else
                {
                    nodeMap.get("0").add(departments);
                }
            }
            else if (codes.length > 1)
            {
                String parentid = codes[codes.length-2];
                if (nodeMap.get(parentid) == null)
                {
                    List<Departments> nodelist = new ArrayList<>();
                    nodelist.add(departments);
                    nodeMap.put(parentid,nodelist);
                }
                else
                {
                    nodeMap.get(parentid).add(departments);
                }
            }
        }


        return getTreeByParent("0",nodeMap).toJSONString();
    }

    /**
     * 获取部门信息
     * @param parentId 父节点id 如果要获取整棵树父节点id输入0
     * @param map 部门前缀树
     * @return
     */
    private JSONArray getTreeByParent(String parentId,Map<String,List<Departments>> map){
        JSONArray array = new JSONArray();
        List<Departments> departmentsList = map.get(parentId);
        for (Departments departments: departmentsList)
        {
            JSONObject obj = new JSONObject();
            obj.put("value",departments.getId());
            obj.put("code",departments.getCode());
            obj.put("label",departments.getName());
            if (map.get(String.valueOf(departments.getId())) != null)
                obj.put("children",getTreeByParent(String.valueOf(departments.getId()),map));
            array.add(obj);
        }
        return array;
    }


}
