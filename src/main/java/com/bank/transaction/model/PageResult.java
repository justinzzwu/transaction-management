package com.bank.transaction.model;

import java.util.List;

public class PageResult<T> {
	private final List<T> data;
	private final int totalCount;

	public PageResult(List<T> data, int totalCount) {
		this.data = data;
		this.totalCount = totalCount;
	}

	public List<T> getData() {
		return data;
	}

	public int getTotalCount() {
		return totalCount;
	}
}
