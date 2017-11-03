/**
 * 
 */
package com.cts.aws.poc.exceptions;

import java.util.List;
import java.util.Set;

import com.cts.aws.poc.models.FailedPayment;

/**
 * @author Azharkhan
 *
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 1127489429266198196L;

	private String errorMsg;
	
	private Throwable cause;
	
	private Set<String> errors;
	
	private List<FailedPayment> failedPayments;
	
	public ValidationException(String message) {
		errorMsg = message;
	}

	public ValidationException(String message, Throwable cause) {
		
		errorMsg = message;
		this.cause = cause;
	}
	
	public ValidationException(String message, Set<String> errors) {
		
		errorMsg = message;
		this.errors = errors;
	}
	
	public ValidationException(String message, List<FailedPayment> failedPayments) {
		
		errorMsg = message;
		this.failedPayments = failedPayments;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Throwable getCause() {
		return cause;
	}

	public Set<String> getErrors() {
		return errors;
	}

	public List<FailedPayment> getFailedPayments() {
		return failedPayments;
	}

	@Override
	public String toString() {
		return "ValidationException [errorMsg=" + errorMsg + ", cause=" + cause
				+ ", errors=" + errors + ", failedPayments=" + failedPayments
				+ "]";
	}
}