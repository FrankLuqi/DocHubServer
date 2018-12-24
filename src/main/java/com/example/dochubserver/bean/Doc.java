package com.example.dochubserver.bean;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@DynamicUpdate
public class Doc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "docname")
    private String docName;

    private Long uploadUserid;//文件上传者id

    private Date date;//上传时间

    private Long downloads;//下载次数

    private Long categoryId;//文件所属类别id

    private String type;//文件所属类型

    private int open;//文件是否公开。1代表公开，0代表需要特定权限

    private String downloadUrl; //下载文件时文件在服务器的地址

    private String previewUrl; //预览文件时文件在服务器的地址

    private String uploadUser;//上传者用户名（反规范化）

    private String category; //文件所属类别名称（反规范化）

    private String permission;//权限名称列表 （反规范化）

    @Transient
    private String uploadDate;//上传时间
}
