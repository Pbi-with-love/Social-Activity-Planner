package com.socialactivityplanner.community_service.repository;

import com.socialactivityplanner.community_service.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUserName(String userName);
}
