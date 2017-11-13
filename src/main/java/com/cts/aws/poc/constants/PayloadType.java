/**
 * 
 */
package com.cts.aws.poc.constants;

/**
 * @author Azharkhan
 *
 */
public enum PayloadType {
	
	INBOUND('I'),
	
	OUTBOUND('O');
	
	private char identifier;
	
	PayloadType(char identifier) {
		this.identifier = identifier;
	}

	public char getIdentifier() {
		return identifier;
	}
}