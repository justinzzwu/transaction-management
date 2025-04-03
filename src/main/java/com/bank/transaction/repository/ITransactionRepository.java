package com.bank.transaction.repository;

import java.util.Map;

import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;

public interface ITransactionRepository {

	Transaction createTransaction(Transaction transaction);

	void deleteTransaction(String id);

	Transaction modifyTransaction(String id, Transaction transaction);

	PageResult<Transaction> listAllTransactions(int page, int pageSize, Map<String, Object> filters);

	Transaction getTransactionById(String id);

	boolean existsByOrderId(String orderId);

}
