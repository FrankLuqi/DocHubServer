package com.example.dochubserver.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.sun.org.glassfish.gmbal.NameValue;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@Entity
public class OwnedRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
//    @Column(name = "user_id")
//    private long userId;
    @Column(name = "department_role_id")
    private String departmentRoleId;
    @Column(name = "remark")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public OwnedRole(){

    }



}
