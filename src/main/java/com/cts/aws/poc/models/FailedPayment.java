/**
 * 
 */
package com.cts.aws.poc.models;

import com.cts.aws.poc.constants.FailureType;
import com.cts.aws.poc.dao.PaymentDetails;

/**
 * @author Azharkhan
 *
 */
public class FailedPayment {

	private FailureType failureType;
	
	private String failureReason;
	
	private PaymentDetails payment;

	public FailedPayment(FailureType failureType, String failureReason, PaymentDetails payment) {
		
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

	public PaymentDetails getPayment() {
		return payment;
	}

	@Override
	public String toString() {
		return "FailedPayment [failureType=" + failureType + ", failureReason="
				+ failureReason + ", payment=" + payment + "]";
	}
}