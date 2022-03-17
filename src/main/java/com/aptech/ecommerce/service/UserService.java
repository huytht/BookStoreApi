package com.aptech.ecommerce.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.aptech.ecommerce.domain.User;
import com.aptech.ecommerce.exception.EmailExistException;
import com.aptech.ecommerce.exception.EmailNotFoundException;
import com.aptech.ecommerce.exception.NotAnImageFileException;
import com.aptech.ecommerce.exception.UserNotFoundException;
import com.aptech.ecommerce.exception.UsernameExistException;

public interface UserService {

	User findUserByUsername(String username);
	
	User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException;
	
	User addNewUser(String firstName, String lastName, String username, String email, String[] role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UsernameExistException, NotAnImageFileException, IOException;
	
	User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String[] role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UsernameExistException, NotAnImageFileException, IOException;

	User updateProfileImage(String username, MultipartFile profileImage) throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException;
	
	void resetPassword(String email) throws EmailNotFoundException;
	
	void deleteUser(long id) throws UserNotFoundException, IOException;
	
}
