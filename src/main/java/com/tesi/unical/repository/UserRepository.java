package com.tesi.unical.repository;

import com.tesi.unical.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {


}
