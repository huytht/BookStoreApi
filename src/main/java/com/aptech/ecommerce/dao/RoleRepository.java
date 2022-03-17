package com.aptech.ecommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aptech.ecommerce.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);
}
