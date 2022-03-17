package com.aptech.ecommerce.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.aptech.ecommerce.entity.Book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
	
	private Long id;
	 
	private String title;
	    
	private String summaryContent;
	    
	private BigDecimal price;
	    
	private String author;

	private Date publicationDate;
	    
	private String image;
	    
	private boolean isFavorite;
	    
	private CategoryDto category = new CategoryDto();
	
	public static BookDto CreateFromEntity(Book bookData) {
		
		BookDto book = new BookDto();
		
		book.id = bookData.getId();
		book.title = bookData.getTitle();
		book.author = bookData.getAuthor();
		book.image = bookData.getImage();
		book.price = bookData.getPrice();
		book.publicationDate = bookData.getPublicationDate();
		book.summaryContent = bookData.getSummaryContent();
		book.isFavorite = bookData.isFavorite();	
		if (bookData.getCategory() != null)
			book.category = CategoryDto.CreateFromEntity(bookData.getCategory());
		
		return book;
	}
}
