package com.tesi.unical.repository;

import com.tesi.unical.entity.OrderitemModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderitemRepo extends JpaRepository<OrderitemModel,Long> {

    @Query(value = "insert into migration.orderitems values (:id,:oid,:pid,:qnt)",nativeQuery = true)
    @Transactional
    @Modifying
    void insert(@Param("id") Long id, @Param("oid") Long oid, @Param("pid") Long pid, @Param("qnt") int quantity);
}
