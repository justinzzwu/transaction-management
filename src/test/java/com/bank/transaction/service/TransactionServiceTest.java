package com.bank.transaction.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Currency;
import java.util.Map;

import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.model.TransactionStatus;
import com.bank.transaction.model.TransactionType;
import com.bank.transaction.repository.ITransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private ITransactionRepository transactionRepository;

	@InjectMocks
	private TransactionService transactionService;

	private Transaction validTransaction;

	@BeforeEach
	void setUp() {
		validTransaction = new Transaction();
		validTransaction.setOrderId("ORDER123");
		validTransaction.setAmount(BigDecimal.valueOf(100));
		validTransaction.setCurrency(Currency.getInstance("CNY"));
		validTransaction.setCardId("CARD456");
		validTransaction.setType(TransactionType.TRANSFER);
	}

	@Test
	void testCreateTransactionWithValidData() {
		when(transactionRepository.createTransaction(any())).thenReturn(validTransaction);

		Transaction result = transactionService.createTransaction(validTransaction);

		assertNotNull(result);
		verify(transactionRepository).createTransaction(validTransaction);
	}

	@Test
	void testCreateTransactionThrowsWhenNull() {
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(null));
	}

	@Test
	void testCreateTransactionThrowsWhenInvalidOrderId() {
		validTransaction.setOrderId("  ");
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
	}

	@Test
	void testModifyTransactionThrowsWhenSuccessStatus() {
		Transaction existing = new Transaction();
		existing.setStatus(TransactionStatus.SUCCESS);
		when(transactionRepository.getTransactionById("123")).thenReturn(existing);

		assertThrows(IllegalArgumentException.class,
				() -> transactionService.modifyTransaction("123", validTransaction));
	}

	@Test
	void testDeleteTransactionThrowsWhenNotFound() {
		doThrow(new TransactionNotFoundException("INVALID_ID"))
				.when(transactionRepository)
				.deleteTransaction("INVALID_ID");

		assertThrows(TransactionNotFoundException.class,
				() -> transactionService.deleteTransaction("INVALID_ID"));
	}

	@Test
	void testListAllTransactionsWithPaging() {
		when(transactionRepository.listAllTransactions(0, 10, Map.of("status", "PENDING")))
				.thenReturn(new PageResult<>(Collections.singletonList(validTransaction), 1));

		var results = transactionService.listAllTransactions(0, 10, Map.of("status", "PENDING"));

		assertEquals(1, results.getTotalCount());
	}

	@Test
	void testGetTransactionByIdSuccess() {
		when(transactionRepository.getTransactionById("123")).thenReturn(validTransaction);

		Transaction result = transactionService.getTransactionById("123");

		assertNotNull(result);
		assertEquals("ORDER123", result.getOrderId());
	}

	@Test
	void testCreateTransactionThrowsWhenFutureDate() {
		validTransaction.setDate(Instant.now().plusSeconds(3600));
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
	}

	@Test
	void testCreateTransactionThrowsWhenSameCardTransfer() {
		validTransaction.setToCardId(validTransaction.getCardId());
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
	}

	@Test
	void testCreateTransactionThrowsWhenInvalidAmount() {
		// test zero amount
		validTransaction.setAmount(BigDecimal.ZERO);
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));

		// test
		validTransaction.setAmount(BigDecimal.valueOf(-100));
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
	}

	@Test
	void testCreateTransactionThrowsWhenMissingRequiredFields() {

		validTransaction.setCurrency(null);
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));

		validTransaction.setType(null);
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));

		validTransaction.setCardId("");
		assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
	}

	@Test
	void testModifyCompletedTransaction() {
		Transaction existing = new Transaction();
		existing.setStatus(TransactionStatus.SUCCESS);
		when(transactionRepository.getTransactionById("123")).thenReturn(existing);

		Transaction update = new Transaction();
		update.setStatus(TransactionStatus.PROCESSING);

		assertThrows(IllegalArgumentException.class,
				() -> transactionService.modifyTransaction("123", update));
	}

	@Test
	void testListWithInvalidPaginationParams() {
		assertDoesNotThrow(() -> transactionService.listAllTransactions(-1, 10, Map.of()));

		assertDoesNotThrow(() -> transactionService.listAllTransactions(0, 0, Map.of()));
	}

	@Test
	void testFilterWithInvalidParamTypes() {
		Map<String, Object> invalidFilters = Map.of(
				"status", 12345,
				"startDate", "invalid-date"
		);

		assertDoesNotThrow(() -> transactionService.listAllTransactions(0, 10, invalidFilters));
	}

	@Test
	void testCreateTransactionThrowsWhenMissingCardId() {
		validTransaction.setCardId("");
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
		assertEquals("Card ID is required", exception.getMessage());
	}

	@Test
	void testCreateTransactionThrowsWhenMissingTransactionType() {
		validTransaction.setType(null);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
		assertEquals("Transaction type must be specified", exception.getMessage());
	}

	@Test
	void testModifyTransactionThrowsWhenTransactionNotFound() {
		when(transactionRepository.getTransactionById("INVALID_ID")).thenReturn(null);

		TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class,
				() -> transactionService.modifyTransaction("INVALID_ID", new Transaction()));
		assertEquals("Transaction not found", exception.getMessage());
	}

	@Test
	void testCreateTransactionThrowsWhenMissingCurrency() {
		validTransaction.setCurrency(null);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> transactionService.createTransaction(validTransaction));
		assertEquals("Currency must be specified", exception.getMessage());
	}

}
