package com.itasset.management.repository;

import com.itasset.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findByRole(String role);

    Optional<User> findByEmployeeId(String employeeId);
}
