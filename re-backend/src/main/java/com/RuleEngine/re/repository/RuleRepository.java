package com.RuleEngine.re.repository;

import com.RuleEngine.re.model.Rule; // Import Rule model
import org.springframework.data.jpa.repository.JpaRepository; // Import Spring Data JPA repository interface

// Repository interface for Rule entity
public interface RuleRepository extends JpaRepository<Rule, Long> {
    // JpaRepository provides built-in methods for CRUD operations
}
