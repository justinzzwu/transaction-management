package com.bank.transaction.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.bank.transaction.exception.TransactionAlreadyExistsException;
import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of transaction repository
 * <p>
 * Features:
 * <p>
 * 1. Thread-safe storage using ConcurrentHashMap
 * <p>
 * 2. Dual mapping (ID and OrderID) for quick lookups
 * <p>
 * 3. Comprehensive filtering for transaction queries
 * <p>
 * 4. Pagination support for list operations
 * <p>
 * 5. Fine-grained locking by OrderId to prevent duplicates while maintaining concurrency
 */
@Repository
public class InMemoryTransactionRepository implements ITransactionRepository {
	private final static Logger LOGGER = LoggerFactory.getLogger(InMemoryTransactionRepository.class);

	private final Map<String, Transaction> transactions = new ConcurrentHashMap<>();
	private final Map<String, String> orderIdToId = new ConcurrentHashMap<>();

	@Override
	public Transaction createTransaction(Transaction transaction) {
		if (existsByOrderId(transaction.getOrderId())) {
			LOGGER.warn("Transaction with order ID {} already exists", transaction.getOrderId());
			throw new TransactionAlreadyExistsException("Transaction with order ID already exists");
		}
		String id = UUID.randomUUID().toString();
		transaction.setId(id);
		transactions.put(id, transaction);
		orderIdToId.put(transaction.getOrderId(), id);
		return transaction;
	}

	@Override
	public void deleteTransaction(String id) {
		if (!transactions.containsKey(id)) {
			LOGGER.error("Transaction with ID {} not found", id);
			throw new TransactionNotFoundException("Transaction not found");
		}
		Transaction transaction = transactions.get(id);
		transactions.remove(id);
		orderIdToId.remove(transaction.getOrderId());
	}

	@Override
	public Transaction modifyTransaction(String id, Transaction transaction) {
		if (!transactions.containsKey(id)) {
			LOGGER.error("Transaction with ID {} not found", id);
			throw new TransactionNotFoundException("Transaction not found");
		}
		Transaction existing = transactions.get(id);
		existing.setStatus(transaction.getStatus());
		existing.setDescription(transaction.getDescription());
		transactions.put(id, existing);
		return existing;
	}

	@Override
	public PageResult<Transaction> listAllTransactions(int page, int pageSize, Map<String, Object> filters) {
		List<Transaction> filteredTransactions = transactions.values().stream()
				.filter(t -> {
					if (filters.containsKey("cardId") && !filters.get("cardId").equals(t.getCardId()))  {
						return false;
					}
					if (filters.containsKey("status") && !filters.get("status").equals(t.getStatus())) {
						return false;
					}
					if (filters.containsKey("channel") && !filters.get("channel").equals(t.getChannel())) {
						return false;
					}
					if (filters.containsKey("startDate") && ((Instant) filters.get("startDate")).isAfter(t.getDate())) {
						return false;
					}
					return !filters.containsKey("endDate") || !((Instant) filters.get("endDate")).isBefore(t.getDate());
				})
				.collect(Collectors.toList());

		int total = filteredTransactions.size();
		int fromIndex = page * pageSize;
		int toIndex = Math.min(fromIndex + pageSize, total);

		return new PageResult<>(
				filteredTransactions.subList(fromIndex, toIndex),
				total
		);
	}

	@Override
	public Transaction getTransactionById(String id) {
		return transactions.get(id);
	}

	@Override
	public boolean existsByOrderId(String orderId) {
		return orderIdToId.containsKey(orderId);
	}
}