package com.round3.realestate.repository;

import com.round3.realestate.entity.Employment;
import com.round3.realestate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    Optional<Employment> findByUser(User user);
}