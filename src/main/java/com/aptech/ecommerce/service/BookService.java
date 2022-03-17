package com.aptech.ecommerce.service;

import com.aptech.ecommerce.domain.AppServiceResult;
import com.aptech.ecommerce.dto.BookDto;
import java.util.List;

public interface BookService {
	AppServiceResult<List<BookDto>> getBooks();

	AppServiceResult<BookDto> saveBook(BookDto bookDto);

//	AppServiceResult<BookDto> updateBook(BookDto bookDto); 
}
