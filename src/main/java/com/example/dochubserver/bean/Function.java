package com.example.dochubserver.bean;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Integer category;//功能类型 1是网站页面 2是菜单 3是按钮
    @Column(name = "father_node")
    private long fatherNode;//父节点id
    private String name;//功能名称
    private String url;//功能链接地址
    private String identity;//标识该功能对应的component或按钮的名称
    private Integer status;//功能当前状态 1代表可用 0代表不可用
}
