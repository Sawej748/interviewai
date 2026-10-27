

package com.interviewAi.Backend.repository;

import com.interviewAi.Backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Email se user find karte hain
    Optional<User> findByEmail(String email);

    // Check karte hain ki email pehle se registered hai ya nahi
    boolean existsByEmail(String email);
}

