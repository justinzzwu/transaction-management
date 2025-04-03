package com.bank.transaction.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.model.TransactionStatus;
import com.bank.transaction.repository.ITransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for managing financial transactions
 * <p>
 * Features:
 * <p>
 * 1. Transaction creation with comprehensive validation
 * <p>
 * 2. Integration with transaction repository for persistence
 */
@Service
public class TransactionService {
	private final ITransactionRepository transactionRepository;

	@Autowired
	public TransactionService(ITransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public Transaction createTransaction(Transaction transaction) {
		// Validate transaction object
		if (transaction == null) {
			throw new IllegalArgumentException("Transaction object cannot be null");
		}

		// Validate order ID
		if (transaction.getOrderId() == null || transaction.getOrderId().trim().isEmpty()) {
			throw new IllegalArgumentException("Order ID is required");
		}

		// Validate amount
		if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Amount must be a positive value");
		}

		// Validate currency
		if (transaction.getCurrency() == null) {
			throw new IllegalArgumentException("Currency must be specified");
		}

		// Validate card ID
		if (transaction.getCardId() == null || transaction.getCardId().trim().isEmpty()) {
			throw new IllegalArgumentException("Card ID is required");
		}

		// Validate transaction type
		if (transaction.getType() == null) {
			throw new IllegalArgumentException("Transaction type must be specified");
		}

		// Validate transaction date
		if (transaction.getDate() != null && transaction.getDate().isAfter(Instant.now())) {
			throw new IllegalArgumentException("Transaction date cannot be in the future");
		}

		// Validate transfer between different cards
		if (transaction.getToCardId() != null && transaction.getToCardId().equals(transaction.getCardId())) {
			throw new IllegalArgumentException("Sender and receiver cannot be the same");
		}

		return transactionRepository.createTransaction(transaction);
	}

	public void deleteTransaction(String id) {
		transactionRepository.deleteTransaction(id);
	}

	public Transaction modifyTransaction(String id, Transaction transaction) {
		// Retrieve existing transaction by ID
		Transaction existing = transactionRepository.getTransactionById(id);

		// Check if transaction exists
		if (existing == null) {
			throw new TransactionNotFoundException("Transaction not found");
		}

		// Validate transaction status, cannot modify successful transactions
		if (existing.getStatus() == TransactionStatus.SUCCESS) {
			throw new IllegalArgumentException("Cannot modify a successful transaction");
		}

		// Update and return the modified transaction
		return transactionRepository.modifyTransaction(id, transaction);
	}

	@Cacheable(value = "listAllTransactions", keyGenerator = "transactionListKeyGenerator", unless = "#result.totalCount == 0")
	public PageResult<Transaction> listAllTransactions(int page, int pageSize, Map<String, Object> filters) {
		return transactionRepository.listAllTransactions(page, pageSize, filters);
	}

	public Transaction getTransactionById(String id) {
		return transactionRepository.getTransactionById(id);
	}
}
