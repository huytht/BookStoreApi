package com.aptech.ecommerce.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestParam;

import com.aptech.ecommerce.entity.Book;

@CrossOrigin("http://localhost:4200")
@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
	// Select * from book where category_id = ?
	Page<Book> findByCategoryId(@RequestParam("id") Long id, Pageable pageable);
	
	// Select * from book where title = ?
	Page<Book> findByTitleContaining(@RequestParam("name") String name, Pageable pageable);
}
