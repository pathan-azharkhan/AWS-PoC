/**
 * 
 */
package com.cts.aws.poc.dao;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Azharkhan
 *
 */
@Entity
public class PaymentErrors {

	@Id
	private String id;
	
	private String paymentId;
	
	private String errorCode;
	
	private String errorType;
	
	private String errorDescription;
	
	private Date createdDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@Override
	public String toString() {
		return "PaymentErrors [id=" + id + ", paymentId=" + paymentId
				+ ", errorCode=" + errorCode + ", errorType=" + errorType
				+ ", errorDescription=" + errorDescription + ", createdDate="
				+ createdDate + "]";
	}
}