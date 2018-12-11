package com.example.dochubserver.repository;

import com.example.dochubserver.bean.DocPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocPermissionRepository extends JpaRepository<DocPermission,Long> {

    public List<DocPermission> findByDocId(long id);

}
