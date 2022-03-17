package com.aptech.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.aptech.ecommerce.constant.FileConstant;
import com.aptech.ecommerce.dao.RoleRepository;
import com.aptech.ecommerce.dao.UserRepository;
import com.aptech.ecommerce.domain.User;
import com.aptech.ecommerce.domain.UserPrincipal;
import com.aptech.ecommerce.exception.EmailExistException;
import com.aptech.ecommerce.exception.EmailNotFoundException;
import com.aptech.ecommerce.exception.NotAnImageFileException;
import com.aptech.ecommerce.exception.UserNotFoundException;
import com.aptech.ecommerce.exception.UsernameExistException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@Qualifier("myUserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService{

	private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
	private UserRepository userRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private RoleRepository roleRepository;
	private LoginAttemptService loginAttempService;
	private EmailService emailService;

	@Autowired
	public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
			RoleRepository roleRepository, LoginAttemptService loginAttempService, EmailService emailService) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.roleRepository = roleRepository;
		this.loginAttempService = loginAttempService;
		this.emailService = emailService;
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("User not found by username: " + username);
		} else {
			
			if (user.isNotLocked()) {
				// check number of attempts and lock if exceed the maximum
				try {
					if (loginAttempService.hasExceededMaxAttempts(user.getUsername())) {
						user.setNotLocked(false);
					} else {
						user.setNotLocked(true);
					}
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				loginAttempService.evictUserFromLoginAttemptCache(user.getUsername());
			}
			
			
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
		}
		
		UserPrincipal userPrincipal = new UserPrincipal(user);
		
		return userPrincipal;
	}

	@Override
	public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		String password = RandomStringUtils.randomAlphanumeric(10);
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		User user = new User();
		user.setUserId(RandomStringUtils.randomNumeric(10));
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRoles(Stream.of(roleRepository.findByName("ROLE_USER_READ")).collect(Collectors.toSet()));
		user.setAuthorities(roleRepository.findByName("ROLE_USER_READ").getAuthorities().stream().distinct().collect(Collectors.toSet()));
		user.setProfileImageUrl(
			ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString()
		);
		
		User newUser = userRepository.save(user);
		LOGGER.info("Now user password: " + password);
//		emailService.sendSimpleMessage(email, "Your registration has been completed", "Hello" + firstName + "\n\nYour login password is " + password + "\n\nThe Support Team");
		return newUser;
	}
	
	private void saveProfileImage(User user, MultipartFile profileImage) throws NotAnImageFileException, IOException {
		if (profileImage != null) {
			if (!Arrays.asList(MimeTypeUtils.IMAGE_JPEG_VALUE, MimeTypeUtils.IMAGE_GIF_VALUE, MimeTypeUtils.IMAGE_PNG_VALUE).contains(profileImage.getContentType())) {
				throw new NotAnImageFileException(profileImage.getOriginalFilename() + " is not an image file");
			}
			
			Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
			
			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
			}
			
			Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + "." + "jpg"));
			
			// save
			Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + "." + "jpg"), REPLACE_EXISTING);
			
			// update new URL
			user.setProfileImageUrl(
				ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path(FileConstant.USER_IMAGE_PATH + user.getUsername() + "/" + user.getUsername() + "." + "jpg")
				.toUriString()
			);
			
			userRepository.save(user);
			LOGGER.info("Save profile image url: " + profileImage.getOriginalFilename());
		}
	}
	
	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) throws EmailExistException, UsernameExistException {
		
		User newUserByUsername = userRepository.findUserByUsername(newUsername);
		User newUserByEmail = userRepository.findUserByEmail(newEmail);
		
		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = userRepository.findUserByUsername(newUsername);
			
			if (currentUser == null) {
				throw new UsernameNotFoundException("No user found by username " + currentUsername);
			}
			if (newUserByUsername != null && !currentUser.getId().equals(newUserByUsername.getId())) {
				throw new UsernameExistException("Username already exist");
			}
			if (newUserByUsername != null && !currentUser.getId().equals(newUserByEmail.getId())) {
				throw new EmailExistException("Email already exist");
			}
			return currentUser;
		} else {
			if (newUserByUsername != null) {
				throw new UsernameExistException("Username already exist");
			}
			if (newUserByEmail != null) {
				throw new EmailExistException("Email already exist");
			}
			return null;
		}
	}

	@Override
	public User addNewUser(String firstName, String lastName, String username, String email, String[] role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws EmailExistException, UsernameExistException, NotAnImageFileException, IOException {
		validateNewUsernameAndEmail(StringUtils.EMPTY, username, email);
		String password = RandomStringUtils.randomAlphanumeric(10);
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		User user = new User();
		user.setUserId(RandomStringUtils.randomNumeric(10));
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(isActive);
		user.setNotLocked(isNonLocked);
		user.setRoles(Arrays.stream(role).map(r -> roleRepository.findByName(r)).collect(Collectors.toSet()));
		user.setAuthorities(Arrays.stream(role).map(r -> roleRepository.findByName(r))
				.flatMap(ro -> ro.getAuthorities().stream()).distinct().collect(Collectors.toSet()));
		user.setProfileImageUrl(
			ServletUriComponentsBuilder.fromCurrentContextPath().path(FileConstant.DEFAULT_USER_IMAGE_PATH + username).toUriString()
		);
		
		userRepository.save(user);
		LOGGER.info("Now user password: " + password);
		
		saveProfileImage(user, profileImage);
		
		return user;
	}

	@Override
	public User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername,
			String newEmail, String[] role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws EmailExistException, UsernameExistException, NotAnImageFileException, IOException {
		User currentUser = validateNewUsernameAndEmail(currentUsername, newUsername, newEmail);
		
		User user = new User();
		user.setUserId(RandomStringUtils.randomNumeric(10));
		user.setFirstName(newFirstName);
		user.setLastName(newLastName);
		user.setUsername(newUsername);
		user.setEmail(newEmail);
		user.setActive(isActive);
		user.setNotLocked(isNonLocked);
		user.setRoles(Arrays.stream(role).map(r -> roleRepository.findByName(r)).collect(Collectors.toSet()));
		user.setAuthorities(Arrays.stream(role).map(r -> roleRepository.findByName(r))
				.flatMap(ro -> ro.getAuthorities().stream()).distinct().collect(Collectors.toSet()));
		
		userRepository.save(currentUser);
		
		saveProfileImage(currentUser, profileImage);
		
		return user;
	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage)
            throws EmailExistException, UsernameExistException, IOException, NotAnImageFileException {
        User user = validateNewUsernameAndEmail(username, null, null);
        saveProfileImage(user, profileImage);
        return user;
    }

	 @Override
     public void resetPassword(String email) throws EmailNotFoundException {
        User user = userRepository.findUserByEmail(email);

        if (user == null)
            throw  new EmailNotFoundException("No user found for the email " + email);

        String password = RandomStringUtils.randomAlphanumeric(10);
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        LOGGER.info("User password: " + password);
    }

	@Override
	public void deleteUser(long id) throws UserNotFoundException, IOException {
		if (!userRepository.existsById(id)) {
			throw new UserNotFoundException("User does not exist");
		}
		User user = userRepository.findById(id).get();
		Path userFolder = Paths.get(FileConstant.USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
        userRepository.deleteById(id);
	}
}
