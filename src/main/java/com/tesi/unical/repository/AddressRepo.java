package com.tesi.unical.repository;

import com.tesi.unical.entity.AddressModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepo extends JpaRepository<AddressModel,Long> {
}
