package com.example.dochubserver.repository;

import com.example.dochubserver.bean.OwnedRole;
import com.example.dochubserver.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OwnedRoleRepository extends JpaRepository<OwnedRole,Long> {
//    public List<OwnedRole> findByUserId(long userId);

    public List<OwnedRole> findByUserId(long userid);

    public List<OwnedRole> findByUser(User user);

    // 删除部门时要在用户拥有的角色表中查找是否存在该部门，如id为2的部门被删除，departmentRoleId为DId2RId4的项也要被删除
    // 此时该方法传入的参数DId应该为 DId2%
    @Query("select o from OwnedRole o where o.departmentRoleId like :DId")
    public List<OwnedRole> findByDepartmentId(@Param("DId") String DId);

    //根据用户id和部门角色id进行查找，防止给一个用户重复添加部门角色
    public OwnedRole findByUserIdAndDepartmentRoleId(Long userId,String departmentRoleId);
}
