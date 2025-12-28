package com.hutech.demo.service;

import com.hutech.demo.model.User;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    // Add delete if needed by admin
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
