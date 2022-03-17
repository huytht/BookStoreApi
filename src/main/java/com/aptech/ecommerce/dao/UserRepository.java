package com.aptech.ecommerce.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aptech.ecommerce.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);
	
	Page<User> findAll(Pageable pageable);
}
