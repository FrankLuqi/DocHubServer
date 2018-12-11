package com.example.dochubserver.repository;

import com.example.dochubserver.bean.FunctionPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionPermissionRepository extends JpaRepository<FunctionPermission,Long> {
    public List<FunctionPermission> findByFunctionId(long functionId);

    public FunctionPermission findByFunctionIdAndDepartmentRoleId(long functionId,String departmentRoleId);
}
