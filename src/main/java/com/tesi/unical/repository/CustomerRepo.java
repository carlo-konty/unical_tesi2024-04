package com.tesi.unical.repository;

import com.tesi.unical.entity.CustomersModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepo extends JpaRepository<CustomersModel,Long> {

    @Modifying
    @Query(value = "insert into migration.customers values(:id,:name,:email)",nativeQuery = true)
    @Transactional
    void insert(@Param("id") Long id, @Param("name") String name, @Param("email") String email);


}
