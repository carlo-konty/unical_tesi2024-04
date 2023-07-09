package com.tesi.unical.service;

import com.tesi.unical.entity.UserModel;
import com.tesi.unical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserModel> getAll() {
        return this.userRepository.findAll();
    }

}
