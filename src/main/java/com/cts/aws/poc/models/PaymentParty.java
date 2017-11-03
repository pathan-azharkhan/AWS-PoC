/**
 * 
 */
package com.cts.aws.poc.models;

/**
 * @author Azharkhan
 *
 */
public class PaymentParty {
	
	private String partyId;
	
	private String accountId;
	
	private String bankCode;
	
	private String branchCode;

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	@Override
	public String toString() {
		return "PaymentParty [partyId=" + partyId + ", accountId=" + accountId
				+ ", bankCode=" + bankCode + ", branchCode=" + branchCode + "]";
	}
}