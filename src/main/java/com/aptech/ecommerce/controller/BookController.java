package com.aptech.ecommerce.controller;

import com.aptech.ecommerce.dao.BookRepository;
import com.aptech.ecommerce.domain.AppServiceResult;
import com.aptech.ecommerce.domain.HttpResponse;
import com.aptech.ecommerce.domain.HttpResponseError;
import com.aptech.ecommerce.domain.HttpResponseSuccess;
import com.aptech.ecommerce.dto.BookDto;
import com.aptech.ecommerce.entity.Book;
import com.aptech.ecommerce.service.BookService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookRepository bookRepository, BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<HttpResponse> createBook(@RequestBody BookDto book) {
        AppServiceResult<BookDto> result = this.bookService.saveBook(book);
        
        return result.isSuccess() ? ResponseEntity.ok(new HttpResponseSuccess<BookDto>(result.getData()))
				: ResponseEntity.badRequest().body(new HttpResponseError(null, result.getMessage()));
    }
    
    @PutMapping
    public ResponseEntity<HttpResponse> updateBook(@RequestBody BookDto book) {
        AppServiceResult<BookDto> result = this.bookService.saveBook(book);
        
        return result.isSuccess() ? ResponseEntity.ok(new HttpResponseSuccess<BookDto>(result.getData()))
				: ResponseEntity.badRequest().body(new HttpResponseError(null, result.getMessage()));
    }
    
    @GetMapping(path = "/list")
	public ResponseEntity<HttpResponse> getBooks() {

		AppServiceResult<List<BookDto>> result = bookService.getBooks();

		return result.isSuccess() ? ResponseEntity.ok(new HttpResponseSuccess<List<BookDto>>(result.getData()))
				: ResponseEntity.badRequest().body(new HttpResponseError(null, result.getMessage()));
	}
}
