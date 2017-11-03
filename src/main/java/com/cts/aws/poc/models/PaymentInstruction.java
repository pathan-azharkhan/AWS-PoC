/**
 * 
 */
package com.cts.aws.poc.models;

import java.time.LocalDate;

/**
 * @author Azharkhan
 *
 */
public class PaymentInstruction {

	private String instrctnId;
	
	private LocalDate valueDate;
	
	private double txnAmnt;
	
	private String currency;
	
	private PaymentParty debtor;
	
	private PaymentParty creditor;

	public String getInstrctnId() {
		return instrctnId;
	}

	public void setInstrctnId(String instrctnId) {
		this.instrctnId = instrctnId;
	}

	public LocalDate getValueDate() {
		return valueDate;
	}

	public void setValueDate(LocalDate valueDate) {
		this.valueDate = valueDate;
	}

	public double getTxnAmnt() {
		return txnAmnt;
	}

	public void setTxnAmnt(double txnAmnt) {
		this.txnAmnt = txnAmnt;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public PaymentParty getDebtor() {
		return debtor;
	}

	public void setDebtor(PaymentParty debtor) {
		this.debtor = debtor;
	}

	public PaymentParty getCreditor() {
		return creditor;
	}

	public void setCreditor(PaymentParty creditor) {
		this.creditor = creditor;
	}

	@Override
	public String toString() {
		return "PaymentInstruction [instrctnId=" + instrctnId + ", valueDate="
				+ valueDate + ", txnAmnt=" + txnAmnt + ", currency=" + currency
				+ ", debtor=" + debtor + ", creditor=" + creditor + "]";
	}
}