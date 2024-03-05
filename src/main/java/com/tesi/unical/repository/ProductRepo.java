package com.tesi.unical.repository;

import com.tesi.unical.entity.ProductModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductModel,Long> {

    @Query(value = "insert into migration.products values (:id,:name,:price)",nativeQuery = true)
    @Transactional
    @Modifying
    void insert(@Param("id") Long id, @Param("name") String name, @Param("price") double price);
}
