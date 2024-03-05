package com.tesi.unical.repository;

import com.tesi.unical.entity.OrderModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OrderRepo extends JpaRepository<OrderModel,Long> {

    @Query(value = "insert into migration.orders values (:id,:date,:tamount,:cid)",nativeQuery = true)
    @Transactional
    @Modifying
    void insert(@Param("id") Long id, @Param("date") Date date, @Param("tamount") double tamount, @Param("cid") Long cid);

}
