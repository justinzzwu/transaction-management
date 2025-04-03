package com.bank.transaction.controller;

import com.bank.transaction.exception.TransactionAlreadyExistsException;
import com.bank.transaction.exception.TransactionNotFoundException;
import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class GlobalResponseHandlerMockMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private TransactionService transactionService;

	@Test
	void shouldReturnConflictWhenTransactionExists() throws Exception {
		Transaction transaction = new Transaction();
		when(transactionService.createTransaction(any(Transaction.class)))
				.thenThrow(new TransactionAlreadyExistsException("Transaction exists"));

		mockMvc.perform(post("/api/transaction")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(transaction)))
				.andExpect(status().isConflict());
	}

	@Test
	void shouldReturnNotFoundWhenTransactionNotExists() throws Exception {
		when(transactionService.getTransactionById("123"))
				.thenThrow(new TransactionNotFoundException("Transaction not found"));

		mockMvc.perform(get("/api/transaction/123"))
				.andExpect(status().isNotFound());
	}


	@Test
	void shouldWrapSuccessResponse() throws Exception {
		Transaction mockTransaction = new Transaction();
		mockTransaction.setId("123");
		when(transactionService.createTransaction(any())).thenReturn(mockTransaction);

		mockMvc.perform(post("/api/transaction")
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(mockTransaction)))
				.andExpect(status().isOk());
	}
}
