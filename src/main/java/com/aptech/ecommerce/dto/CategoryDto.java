package com.aptech.ecommerce.dto;

import com.aptech.ecommerce.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

	private Long id;

	private String name;

	public static CategoryDto CreateFromEntity(Category categoryData) {
		CategoryDto category = new CategoryDto();

		category.id = categoryData.getId();
		category.name = categoryData.getName();

		return category;
	}
}
