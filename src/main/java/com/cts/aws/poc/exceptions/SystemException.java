/**
 * 
 */
package com.cts.aws.poc.exceptions;


/**
 * @author Azharkhan
 *
 */
public class SystemException extends RuntimeException {

	private static final long serialVersionUID = -1762019853169920776L;

	private String errorMsg;
	
	private Throwable cause;
	
	public SystemException(String message) {
		this.errorMsg = message;
	}

	public SystemException(String message, Throwable cause) {
		
		errorMsg = message;
		this.cause = cause;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Throwable getCause() {
		return cause;
	}

	@Override
	public String toString() {
		return "SystemException [errorMsg=" + errorMsg + ", cause=" + cause + "]";
	}
}