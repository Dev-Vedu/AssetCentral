package com.itasset.management.service;

import com.itasset.management.model.User;
import com.itasset.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password)
                .orElse(null);
    }

    public User findByEmployeeId(String id) {
        return userRepository.findByEmployeeId(id)
                .orElse(null);
    }

    public long countAllUsers() {
        return userRepository.count();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

}
