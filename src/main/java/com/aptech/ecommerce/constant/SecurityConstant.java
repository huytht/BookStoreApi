package com.aptech.ecommerce.constant;

public class SecurityConstant {
	public static final long EXPIRATION_TIME = 432000000; // 5 days in miliseconds
	public static final String COMPANY = "Huy THT";
	public static final String APPLICATION_NAME = "eCommerce Portal";
	public static final String AUTHORITIES = "authorities";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified ";
//	public static final String[] PUBLIC_URLS = {"/api/user/login", "/api/user/register", "/api/user/image/**", "/api/user/resetpassword"};
	public static final String[] PUBLIC_URLS = { "**" };
	public static final String[] PUBLIC_GET_URLS = { "/api/product-category/**", "/api/products/**", "/api/states/**", "/api/countries/**" };
}
