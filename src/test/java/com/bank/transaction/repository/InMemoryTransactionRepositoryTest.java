package com.bank.transaction.repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bank.transaction.exception.TransactionAlreadyExistsException;
import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.model.TransactionChannel;
import com.bank.transaction.model.TransactionStatus;
import com.bank.transaction.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTransactionRepositoryTest {

	private InMemoryTransactionRepository repository;
	private Transaction testTransaction;

	@BeforeEach
	public void setUp() {
		repository = new InMemoryTransactionRepository();
		testTransaction = new Transaction();
		testTransaction.setOrderId("testOrderId");
		testTransaction.setType(TransactionType.DEPOSIT);
		testTransaction.setCurrency(Currency.getInstance("USD"));
		testTransaction.setAmount(BigDecimal.valueOf(100));
		testTransaction.setDate(Instant.now());
		testTransaction.setCardId("testCardId");
		testTransaction.setToCardId("testToCardId");
		testTransaction.setDescription("Test transaction");
		testTransaction.setStatus(TransactionStatus.PROCESSING);
		testTransaction.setChannel(TransactionChannel.WEB_PORTAL);
	}

	@Test
	public void testCreateTransaction() {
		Transaction created = repository.createTransaction(testTransaction);

		assertNotNull(created.getId());
		assertEquals(testTransaction.getOrderId(), created.getOrderId());
		assertEquals(testTransaction.getType(), created.getType());
		assertEquals(testTransaction.getCurrency(), created.getCurrency());
		assertEquals(testTransaction.getAmount(), created.getAmount());
		assertEquals(testTransaction.getDate(), created.getDate());
		assertEquals(testTransaction.getCardId(), created.getCardId());
		assertEquals(testTransaction.getToCardId(), created.getToCardId());
		assertEquals(testTransaction.getDescription(), created.getDescription());
		assertEquals(testTransaction.getStatus(), created.getStatus());
		assertEquals(testTransaction.getChannel(), created.getChannel());
	}

	@Test
	public void testCreateTransactionWithExistingOrderId() {
		repository.createTransaction(testTransaction);

		assertThrows(TransactionAlreadyExistsException.class, () -> repository.createTransaction(testTransaction));
	}

	@Test
	public void testDeleteTransaction() {
		Transaction created = repository.createTransaction(testTransaction);
		String id = created.getId();

		repository.deleteTransaction(id);

		assertNull(repository.getTransactionById(id));
	}

	@Test
	public void testModifyTransaction() {
		Transaction created = repository.createTransaction(testTransaction);
		String id = created.getId();
		Transaction updated = new Transaction();
		updated.setStatus(TransactionStatus.SUCCESS);

		Transaction result = repository.modifyTransaction(id, updated);

		assertEquals(TransactionStatus.SUCCESS, result.getStatus());
	}

	@Test
	public void testListAllTransactions() {
		repository.createTransaction(testTransaction);
		Map<String, Object> filters = new HashMap<>();
		filters.put("cardId", "testCardId");
		filters.put("status", TransactionStatus.PROCESSING);

		PageResult<Transaction> result = repository.listAllTransactions(0, 10, filters);

		assertFalse(result.getData().isEmpty());
	}

	@Test
	public void testGetTransactionById() {
		Transaction created = repository.createTransaction(testTransaction);
		String id = created.getId();

		Transaction result = repository.getTransactionById(id);

		assertEquals(created, result);
	}

	@Test
	public void testExistsByOrderId() {
		repository.createTransaction(testTransaction);

		assertTrue(repository.existsByOrderId("testOrderId"));
	}

	@Test
	public void testDeleteNonExistingTransaction() {
		assertThrows(TransactionNotFoundException.class, () -> repository.deleteTransaction("non-existing-id"));
	}

	@Test
	public void testModifyNonExistingTransaction() {
		Transaction updated = new Transaction();
		updated.setStatus(TransactionStatus.SUCCESS);
		assertThrows(TransactionNotFoundException.class, () -> repository.modifyTransaction("non-existing-id", updated));
	}

	@Test
	public void testGetNonExistingTransaction() {
		assertNull(repository.getTransactionById("non-existing-id"));
	}

	@Test
	public void testListWithVariousFilters() {
		Transaction t1 = createTestTransactionWith("card1", TransactionStatus.SUCCESS, TransactionChannel.MOBILE_APP, Instant.parse("2025-04-01T00:00:00Z"));
		Transaction t2 = createTestTransactionWith("card2", TransactionStatus.FAILED, TransactionChannel.ATM, Instant.parse("2025-04-02T00:00:00Z"));
		Transaction t3 = createTestTransactionWith("card1", TransactionStatus.PROCESSING, TransactionChannel.WEB_PORTAL, Instant.parse("2025-04-03T00:00:00Z"));

		Map<String, Object> filters = new HashMap<>();
		filters.put("channel", TransactionChannel.MOBILE_APP);
		PageResult<Transaction> results = repository.listAllTransactions(0, 10, filters);
		assertEquals(1, results.getData().size());
		assertTrue(results.getData().contains(t1));

		filters.clear();
		filters.put("startDate", Instant.parse("2025-04-02T00:00:00Z"));
		filters.put("endDate", Instant.parse("2025-04-03T00:00:00Z"));
		results = repository.listAllTransactions(0, 10, filters);
		assertEquals(2, results.getData().size());
		assertTrue(results.getData().contains(t2));
		assertTrue(results.getData().contains(t3));

		filters.put("cardId", "card1");
		filters.put("status", TransactionStatus.PROCESSING);
		results = repository.listAllTransactions(0, 10, filters);
		assertEquals(1, results.getData().size());
		assertTrue(results.getData().contains(t3));

		filters.put("invalidKey", "someValue");
		results = repository.listAllTransactions(0, 10, filters);
		assertEquals(1, results.getData().size()); // 无效key不应影响过滤结果
	}

	private Transaction createTestTransactionWith(String cardId, TransactionStatus status, TransactionChannel channel, Instant date) {
		Transaction t = new Transaction();
		t.setOrderId(UUID.randomUUID().toString());
		t.setCardId(cardId);
		t.setStatus(status);
		t.setChannel(channel);
		t.setDate(date);
		return repository.createTransaction(t);
	}

	@Test
	public void testEdgeCaseDateFilters() {
		Instant exactTime = Instant.parse("2025-04-02T12:00:00Z");
		Transaction t = createTestTransactionWith("cardX", TransactionStatus.SUCCESS, TransactionChannel.ATM, exactTime);

		Map<String, Object> filters = new HashMap<>();
		filters.put("startDate", exactTime);
		filters.put("endDate", exactTime);
		PageResult<Transaction> results = repository.listAllTransactions(0, 10, filters);
		assertEquals(1, results.getData().size());

		filters.put("startDate", exactTime.minusSeconds(1));
		filters.put("endDate", exactTime.plusSeconds(1));
		results = repository.listAllTransactions(0, 10, filters);
		assertEquals(1, results.getData().size());
	}
}
