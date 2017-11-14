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
public class Payload {

	@Id
	private String payloadId;
	
	private String payloadName;
	
	private String batchId;
	
	private char payloadType;
	
	private String status;
	
	private Date createdDate;
	
	private String createdBy;
	
	private Date updatedDate;
	
	private String updatedBy;

	public String getPayloadId() {
		return payloadId;
	}

	public void setPayloadId(String payloadId) {
		this.payloadId = payloadId;
	}

	public String getPayloadName() {
		return payloadName;
	}

	public void setPayloadName(String payloadName) {
		this.payloadName = payloadName;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public char getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(char payloadType) {
		this.payloadType = payloadType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString() {
		return "Payload [payloadId=" + payloadId + ", payloadName="
				+ payloadName + ", batchId=" + batchId + ", payloadType="
				+ payloadType + ", status=" + status + ", createdDate="
				+ createdDate + ", createdBy=" + createdBy + ", updatedDate="
				+ updatedDate + ", updatedBy=" + updatedBy + "]";
	}
}