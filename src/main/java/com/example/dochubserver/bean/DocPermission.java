package com.example.dochubserver.bean;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
public class DocPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long docId;//文件id

    @Column(name = "departmentrole_id")
    private String DepartmentRoleId;//部门角色id

}
