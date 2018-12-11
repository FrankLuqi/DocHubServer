package com.example.dochubserver.repository;

import com.example.dochubserver.bean.Doc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocRepository extends JpaRepository<Doc,Long> {

    public List<Doc> findByUploadUserid(long id);

    public List<Doc> findByCategoryId(long id);

    public List<Doc> findByOpen(int open);

}
