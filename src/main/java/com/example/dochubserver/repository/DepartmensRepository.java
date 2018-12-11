package com.example.dochubserver.repository;

import com.example.dochubserver.bean.Departments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmensRepository extends JpaRepository<Departments,Long> {
    public Departments findByCode(String code);
}
