package com.aptech.ecommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.aptech.ecommerce.constant.AppError;
import com.aptech.ecommerce.dao.BookRepository;
import com.aptech.ecommerce.dao.CategoryRepository;
import com.aptech.ecommerce.domain.AppServiceResult;
import com.aptech.ecommerce.dto.BookDto;
import com.aptech.ecommerce.dto.CategoryDto;
import com.aptech.ecommerce.entity.Book;
import com.aptech.ecommerce.entity.Category;
	
@Service
public class BookServiceImpl implements BookService{

	private BookRepository bookRepository;
	private CategoryRepository categoryRepository;
	
	public BookServiceImpl(BookRepository bookRepository, CategoryRepository categoryRepository) {
		super();
		this.bookRepository = bookRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public AppServiceResult<List<BookDto>> getBooks() {		
		try {
			List<Book> books = bookRepository.findAll();
			List<BookDto> result = new ArrayList<BookDto>();
			
			books.forEach(item -> result.add(BookDto.CreateFromEntity(item)));
			return new AppServiceResult<List<BookDto>>(true, 0, "Succeed!", result);
		} catch (Exception e) {
			e.printStackTrace();
			return new AppServiceResult<List<BookDto>>(false, AppError.Unknown.errorCode(),
					AppError.Unknown.errorMessage(), null);
		}
	}
	
	@Override
	public AppServiceResult<BookDto> saveBook(BookDto bookDto) {
		try {
			
			Book newBook = new Book();
			
			if (bookDto.getId() != null) 
				newBook = bookRepository.getById(bookDto.getId());
			
			newBook.setTitle(bookDto.getTitle());
			newBook.setAuthor(bookDto.getAuthor());
			if (bookDto.getCategory() != null) {
				Category category = categoryRepository.findById(bookDto.getCategory().getId()).orElse(null);	
				newBook.setCategory(category);
			}
			newBook.setFavorite(bookDto.isFavorite());
			newBook.setImage(bookDto.getImage());
			newBook.setPrice(bookDto.getPrice());
			newBook.setPublicationDate(bookDto.getPublicationDate());
			newBook.setSummaryContent(bookDto.getSummaryContent());

			bookRepository.save(newBook);

			return new AppServiceResult<BookDto>(true, 0, "Succeed!", BookDto.CreateFromEntity(newBook));

		} catch (Exception e) {
			e.printStackTrace();

			return new AppServiceResult<BookDto>(false, AppError.Unknown.errorCode(),
					AppError.Unknown.errorMessage(), null);
		}
	}

//	@Override
//	public AppServiceResult<BookDto> updateBook(BookDto bookDto) {
//		try {
//			
//			Book newBook = new Book();
//			
//			newBook.setTitle(bookDto.getTitle());
//			newBook.setAuthor(bookDto.getAuthor());
//			if (bookDto.getCategory().getId() != null) {
//				Category category = categoryRepository.findById(bookDto.getCategory().getId()).orElse(null);	
//			}
//			newBook.setFavorite(Boolean.FALSE);
//			newBook.setImage(bookDto.getImage());
//			newBook.setPrice(bookDto.getPrice());
//			newBook.setPublicationDate(bookDto.getPublicationDate());
//			newBook.setSummaryContent(bookDto.getSummaryContent());
//
//			bookRepository.save(newBook);
//
//			return new AppServiceResult<BookDto>(true, 0, "Succeed!", BookDto.CreateFromEntity(newBook));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			return new AppServiceResult<BookDto>(false, AppError.Unknown.errorCode(),
//					AppError.Unknown.errorMessage(), null);
//		}
//	}

}
