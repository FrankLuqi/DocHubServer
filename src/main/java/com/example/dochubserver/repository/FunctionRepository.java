package com.example.dochubserver.repository;

import com.example.dochubserver.bean.Function;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionRepository extends JpaRepository<Function,Long> {
    public List<Function> findByCategory(Integer category);
}
