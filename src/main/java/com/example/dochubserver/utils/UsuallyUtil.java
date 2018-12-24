package com.example.dochubserver.utils;

import com.alibaba.fastjson.JSONObject;
import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.service.OwendRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsuallyUtil {

    private static final Logger logger = LoggerFactory.getLogger(UsuallyUtil.class);

    public static String[] ImgFileExt = new String[] {"png", "bmp", "jpg", "jpeg"};

    public static String[] VedioFileExt = new String[] {"mp4", "avi", "wmv"};

    public static String[] OfficeFileExt = new String[] {"doc", "docx", "ppt"};


    public static String getJsonString(int code){
        JSONObject json = new JSONObject();
        json.put("code",code);
        return json.toString();
    }

    public static String getJsonString(int code,String msg)
    {
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        return json.toJSONString();
    }

    public static String getJsonString(Map<String,Object> map)
    {
        JSONObject json = new JSONObject();
        for(Map.Entry<String,Object> entry:map.entrySet())
            json.put(entry.getKey(),entry.getValue());
        return json.toJSONString();
    }

    //判断上传的文件的后缀名是否合法，并返回文件类型方便前端分类型显示
    public static String isFileAllowed(String fileExt)
    {
        if (fileExt.equals("pdf"))
            return "pdf文档";
        for (String ext:OfficeFileExt)
        {
            if (ext.equals(fileExt))
                return "office文档";
        }
        for(String ext:ImgFileExt)
        {
            if (ext.equals(fileExt))
                return "图片";
        }
        for (String ext:VedioFileExt)
        {
            if (ext.equals(fileExt))
                return "视频";
        }
        return "其他文件";
    }

    /**
     * 生成部门角色id，调用此方法说明当前功能需要特定部门的特定角色才能访问，如生产部经理
     * @param departmentId 部门id
     * @param roleId 角色id
     * @return
     */
    public static String generateDepartmentRoleNeedsId(long departmentId,long roleId)
    {
        String DepartmentRoleId = "DId"+departmentId+"RId"+roleId;
        return DepartmentRoleId;
    }

    /**
     * 生成部门角色id，调用此方法说明当前功能需要特定部门的才能访问，而对角色没有要求，如生产部
     * @param departmentId 部门或角色id
     * @return
     */
    public static String generateDepartmentNeedsId(long departmentId)
    {
        String DepartmentRoleId = "DId"+departmentId;
        return DepartmentRoleId;
    }

    /**
     * 生成部门角色id，调用此方法说明当前功能需要特定角色的才能访问，而对部门没有要求，如经理
     * @param roleId
     * @return
     */
    public static String generateRoleNeedsId(long roleId)
    {
        String DepartmentRoleId = "RId"+roleId;
        return DepartmentRoleId;
    }


    /**
     * 解析部门角色id
     * @param DepartmentRoleId
     * @return
     */
    public static Map<String,Long> parseDepartmentRoleId(String DepartmentRoleId)
    {
        HashMap<String,Long> map = new HashMap<>();
        String reg1 = "^DId(\\d+)+RId(\\d+)";
        String reg2 = "^DId(\\d+)";
        String reg3 = "^RId(\\d+)";
        Pattern r1 = Pattern.compile(reg1);
        Pattern r2 = Pattern.compile(reg2);
        Pattern r3 = Pattern.compile(reg3);
        Matcher m1 = r1.matcher(DepartmentRoleId);
        Matcher m2 = r2.matcher(DepartmentRoleId);
        Matcher m3 = r3.matcher(DepartmentRoleId);
        if (m1.find())
        {
            map.put("DepartmentId",Long.parseLong(m1.group(1)));
            map.put("RoleId",Long.parseLong(m1.group(2)));
        }
        else if (m2.find())
        {
            map.put("DepartmentId",Long.parseLong(m2.group(1)));
        }
        else if (m3.find())
        {
            map.put("RoleId",Long.parseLong(m3.group(1)));
        }

        return map;
    }

    /**
     * 判断当前用户是否有权限
     * @param departmentRoleid 用户拥有的部门角色id编码
     * @param departmentRoleidNeeds 所需要的部门角色id编码
     * @return
     */
    public static boolean hasPower(String departmentRoleid,String departmentRoleidNeeds)
    {
        Map<String,Long> map1 = new HashMap<>();
        Map<String,Long> map2 = new HashMap<>();
        map1 = parseDepartmentRoleId(departmentRoleid);
        map2 = parseDepartmentRoleId(departmentRoleidNeeds);
        if (departmentRoleid==departmentRoleidNeeds)//如果对部门角色都有要求则必须完全相等
            return true;
        else if (map2.containsKey("DepartmentId")&&!map2.containsKey("RoleId"))//如果对部门有要求对角色没要求，查看用户部门是否与之一致
        {
            if (map1.get("DepartmentId").equals(map2.get("DepartmentId")))
                return true;
            else
                return false;
        }
        else if (!map2.containsKey("DepartmentId")&&map2.containsKey("RoleId"))//如果对角色有要求对部门没要求，查看用户角色是否与之一致
        {
            if (map1.get("RoleId").equals(map2.get("RoleId")))
                return true;
            else
                return false;
        }
        return false;
    }





}
