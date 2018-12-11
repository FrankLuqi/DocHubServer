package com.example.dochubserver.bean;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;


/**
 * 功能权限表
 */
@Entity
@DynamicUpdate
@Data
public class FunctionPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long functionId;//功能id
    @Column(name = "department_role_id")
    private String departmentRoleId;//用户部门角色id
}
