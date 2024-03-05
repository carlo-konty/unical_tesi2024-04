package com.tesi.unical.repository;

import com.tesi.unical.entity.AddressModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<AddressModel,Long> {

    @Query(value = "insert into migration.addresses values (:id,:st,:ci,:co,:cid)",nativeQuery = true)
    @Modifying
    @Transactional
    void insert(@Param("id") Long id, @Param("st") String st, @Param("ci") String ci, @Param("co") String co, @Param("cid") Long cid);
}
