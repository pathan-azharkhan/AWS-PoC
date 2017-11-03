/**
 * 
 */
package com.cts.aws.poc.models;

import com.cts.aws.poc.constants.FailureType;

/**
 * @author Azharkhan
 *
 */
public class FailedPayment {

	private FailureType failureType;
	
	private String failureReason;
	
	private PaymentInstruction payment;

	public FailedPayment(FailureType failureType, String failureReason, PaymentInstruction payment) {
		
		super();
		this.payment = payment;
		this.failureType = failureType;
		this.failureReason = failureReason;
	}

	public FailureType getFailureType() {
		return failureType;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public PaymentInstruction getPayment() {
		return payment;
	}

	@Override
	public String toString() {
		return "FailedPayment [failureType=" + failureType + ", failureReason="
				+ failureReason + ", payment=" + payment + "]";
	}
}