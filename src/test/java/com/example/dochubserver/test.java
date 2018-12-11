package com.example.dochubserver;

import com.example.dochubserver.bean.Doc;
import com.example.dochubserver.bean.Role;
import com.example.dochubserver.repository.UserRepository;
import com.example.dochubserver.service.DocCategoryService;
import com.example.dochubserver.service.FunctionPermissionService;
import com.example.dochubserver.service.FunctionService;
import com.example.dochubserver.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Sql("/init_database3.sql")
public class test {

    @Autowired
    RoleService roleService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FunctionService functionService;

    @Autowired
    FunctionPermissionService functionPermissionService;

    @Autowired
    DocCategoryService docCategoryService;

    @Test
    public void test1()
    {
//        departmentsService.addDepartment("董事会","");
//        departmentsService.addDepartment("市场部","1D");
//        departmentsService.addDepartment("研发部","1D");
//        departmentsService.addDepartment("销售部","1D");
//        departmentsService.addDepartment("行政部门","1D");
//        departmentsService.addDepartment("开发部","1D3D");
//        departmentsService.addDepartment("技术部","1D3D");
//        departmentsService.addDepartment("测试部","1D3D");
//        departmentsService.addDepartment("人力资源部","1D5D");
//        departmentsService.addDepartment("财务部","1D5D");
//        departmentsService.addDepartment("质量部","1D5D");

//        System.out.println(departmentsService.getDepartmentsInfo());
//        roleService.addRole("管理员");
//        roleService.addRole("实习生");
//        roleService.addRole("员工");
//        roleService.addRole("部门经理");
//        roleService.addRole("总经理");
//        functionService.addFunction(2,"用户管理","/UserManage/**","userManage",1);
//        functionService.addFunction(2,"部门管理","/DepartmentManage/**","departmentManage",1);
//        functionService.addFunction(2,"角色管理","/RoleManage/**","RoleManage",1);
//        functionService.addFunction(2,"文件类别管理","/DocCategoryManage/**","DocCategoryManage",1);
//        functionService.addFunction(2,"功能管理","/FunctionPermissionManage/**","FunctionPermissionManage",1);

//        functionPermissionService.addFunctionPermission("1","","1");
//        functionPermissionService.addFunctionPermission("2","","1");
//        functionPermissionService.addFunctionPermission("3","","1");
//        functionPermissionService.addFunctionPermission("4","","1");
//        functionPermissionService.addFunctionPermission("5","","1");

//        docCategoryService.addDocCategory("普通文件类","");
//        docCategoryService.addDocCategory("公司资产证据类","");
//        docCategoryService.addDocCategory("公文类","");
//        docCategoryService.addDocCategory("合同","2D");
//        docCategoryService.addDocCategory("许可证","2D");
//        docCategoryService.addDocCategory("公告","3D");
//        docCategoryService.addDocCategory("报告","3D");
//        docCategoryService.addDocCategory("会议纪要","3D");

    }

    @Test
    public void test2()
    {
    }

}
