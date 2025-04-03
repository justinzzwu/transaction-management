package com.bank.transaction.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bank.transaction.model.PageResult;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.model.TransactionChannel;
import com.bank.transaction.model.TransactionStatus;
import com.bank.transaction.model.TransactionType;
import com.bank.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

	@Mock
	private TransactionService transactionService;

	@InjectMocks
	private TransactionController transactionController;

	private Transaction testTransaction;

	@BeforeEach
	public void setUp() {
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
		when(transactionService.createTransaction(testTransaction)).thenReturn(testTransaction);

		Transaction result = transactionController.createTransaction(testTransaction);

		assertEquals(testTransaction, result);
		verify(transactionService, times(1)).createTransaction(testTransaction);
	}

	@Test
	public void testDeleteTransaction() {
		transactionController.deleteTransaction("testId");

		verify(transactionService, times(1)).deleteTransaction("testId");
	}

	@Test
	public void testModifyTransaction() {
		when(transactionService.modifyTransaction("testId", testTransaction)).thenReturn(testTransaction);

		Transaction result = transactionController.modifyTransaction("testId", testTransaction);

		assertEquals(testTransaction, result);
		verify(transactionService, times(1)).modifyTransaction("testId", testTransaction);
	}

	@Test
	public void testListAllTransactions() {
		Map<String, Object> filters = new HashMap<>();
		filters.put("cardId", "testCardId");
		filters.put("status", TransactionStatus.PROCESSING);
		filters.put("channel", TransactionChannel.WEB_PORTAL);

		when(transactionService.listAllTransactions(0, 10, filters)).thenReturn(new PageResult<>(List.of(testTransaction), 1));

		PageResult<Transaction> result = transactionController.listAllTransactions(0, 10, filters);

		assertEquals(List.of(testTransaction), result.getData());
		verify(transactionService, times(1)).listAllTransactions(0, 10, filters);
	}

	@Test
	public void testGetTransactionById() {
		when(transactionService.getTransactionById("testId")).thenReturn(testTransaction);

		Transaction result = transactionController.getTransactionById("testId");

		assertEquals(testTransaction, result);
		verify(transactionService, times(1)).getTransactionById("testId");
	}
}