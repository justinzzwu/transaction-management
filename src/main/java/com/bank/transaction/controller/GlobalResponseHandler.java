package com.bank.transaction.controller;

import com.bank.transaction.exception.TransactionAlreadyExistsException;
import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.ApiResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * Global controller advice for handling responses and exceptions across all controllers
 * <p>
 * Features:
 * <p>
 * 1. Uniform API response wrapping for successful requests
 * <p>
 * 2. Centralized exception handling with proper HTTP status codes
 * <p>
 * 3. Custom error responses for business exceptions
 */
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return !returnType.getParameterType().equals(ResponseEntity.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		return ApiResponse.success(body);
	}

	@ExceptionHandler(TransactionAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleTransactionAlreadyExistsException(TransactionAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleTransactionNotFoundException(TransactionNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("general error: " + ex.getMessage()));
	}
}
