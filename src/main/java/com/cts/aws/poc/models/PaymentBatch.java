/**
 * 
 */
package com.cts.aws.poc.models;

import java.util.List;

/**
 * @author Azharkhan
 *
 */
public class PaymentBatch {
	
	private String batchId;
	
	private int totalTxns;
	
	private double totalAmnt;

	private List<PaymentInstruction> payments;

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public int getTotalTxns() {
		return totalTxns;
	}

	public void setTotalTxns(int totalTxns) {
		this.totalTxns = totalTxns;
	}

	public double getTotalAmnt() {
		return totalAmnt;
	}

	public void setTotalAmnt(double totalAmnt) {
		this.totalAmnt = totalAmnt;
	}

	public List<PaymentInstruction> getPayments() {
		return payments;
	}

	public void setPayments(List<PaymentInstruction> payments) {
		this.payments = payments;
	}

	@Override
	public String toString() {
		return "PaymentBatch [batchId=" + batchId + ", totalTxns=" + totalTxns
				+ ", totalAmnt=" + totalAmnt + ", payments=" + payments + "]";
	}
}