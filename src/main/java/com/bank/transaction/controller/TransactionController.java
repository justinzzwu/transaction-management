package com.bank.transaction.controller;

import java.util.Map;

import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for transaction operations
 * <p>
 * Features:
 * <p>
 * 1. CRUD endpoints for transaction management
 * <p>
 * 2. Standard RESTful resource paths (/api/transaction)
 * <p>
 * 3. Pagination support for transaction listing
 * <p>
 * 4. Filter capability on list operations
 */
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {
	private final TransactionService transactionService;

	@Autowired
	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@PostMapping
	public Transaction createTransaction(@RequestBody Transaction transaction) {
		return transactionService.createTransaction(transaction);
	}

	@DeleteMapping("/{id}")
	public void deleteTransaction(@PathVariable String id) {
		transactionService.deleteTransaction(id);
	}

	@PutMapping("/{id}")
	public Transaction modifyTransaction(@PathVariable String id, @RequestBody Transaction transaction) {
		return transactionService.modifyTransaction(id, transaction);
	}

	@GetMapping("/list")
	public PageResult<Transaction> listAllTransactions(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam Map<String, Object> filters) {
		return transactionService.listAllTransactions(page, size, filters);
	}

	@GetMapping("/{id}")
	public Transaction getTransactionById(@PathVariable String id) {
		return transactionService.getTransactionById(id);
	}
}
