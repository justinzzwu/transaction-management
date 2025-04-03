package com.bank.transaction.model;

public class ApiResponse<T> {
	private final String message;
	private final T data;

	private ApiResponse(T data, String message) {
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(data, "success");
	}

	public static <T> ApiResponse<T> error(String message) {
		return new ApiResponse<>(null, message);
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
}
