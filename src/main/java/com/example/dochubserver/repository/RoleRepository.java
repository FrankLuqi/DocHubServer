package com.example.dochubserver.repository;

import com.example.dochubserver.bean.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByName(String name);

}
