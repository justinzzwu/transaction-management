package com.bank.transaction.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

/**
 * Represents a financial transaction in the banking system.
 * <p>
 * This entity captures all essential details of monetary operations including
 * fund transfers, payments, and withdrawals. Each transaction is uniquely
 * identified and tracks financial flow between accounts.
 */
public class Transaction {
	/** Unique identifier for the transaction (UUID format) */
	private String id;

	/** Reference number linking to associated order/payment */
	private String orderId;

	/** Type of transaction (e.g., TRANSFER, DEPOSIT, WITHDRAW) */
	private TransactionType type;

	/** Currency ISO code for the transaction amount */
	private Currency currency;

	/** Monetary value with precision handling (positive value) */
	private BigDecimal amount;

	/** Timestamp of transaction initiation (UTC format) */
	private Instant date;

	/** Source account/card identifier */
	private String cardId;

	/** Destination account/card identifier (for transfers) */
	private String toCardId;

	/** Human-readable transaction description */
	private String description;

	/** Current processing state (e.g., SUCCESS, PROCESSING, FAILED) */
	private TransactionStatus status;

	/** Origination channel (e.g., MOBILE_APP, WEB_PORTAL, ATM) */
	private TransactionChannel channel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getToCardId() {
		return toCardId;
	}

	public void setToCardId(String toCardId) {
		this.toCardId = toCardId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public TransactionChannel getChannel() {
		return channel;
	}

	public void setChannel(TransactionChannel channel) {
		this.channel = channel;
	}
}
