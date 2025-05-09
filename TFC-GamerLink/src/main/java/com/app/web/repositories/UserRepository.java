package com.app.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entities.User;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
	void deleteById(String id);
	boolean existsByUserNameAndIdNot(String username, String id);
    
}
