/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.models.PaymentInstruction;
import com.cts.aws.poc.services.FileFormatTransformer;

/**
 * @author Azharkhan
 *
 */
@Component
public class ProprietoryFileFormatTransformer implements FileFormatTransformer<PaymentBatch, String> {
	
	private static final String LINE_FEED = "\n";
	
	private static final String INSTN_ID = "JPMORG";
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Override
	public String transform(PaymentBatch input) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(generateHeaderRecord(input));
		builder.append(LINE_FEED);
		
		input.getPayments().forEach(payment -> {
			
			builder.append(generateTxnRecord(payment));
			builder.append(LINE_FEED);
		});
		
		Assert.hasText(builder.toString(), "Generated message cannot be empty");
		
		return builder.toString();
	}
	
	private String generateHeaderRecord(PaymentBatch batch) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(batch.getBatchId()).append("|");
		builder.append(INSTN_ID).append("|");
		builder.append(batch.getTotalTxns()).append("|");
		builder.append(batch.getTotalAmnt());
		
		return builder.toString();
	}
	
	private String generateTxnRecord(PaymentInstruction payment) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(payment.getInstrctnId()).append("|");
		builder.append(payment.getValueDate().format(FORMATTER)).append("|");
		builder.append(payment.getTxnAmnt()).append("|");
		builder.append(payment.getCurrency()).append("|");
		
		builder.append(payment.getDebtor().getBankCode()).append(".").append(payment.getDebtor().getBranchCode()).append("|");
		builder.append(payment.getDebtor().getAccountId()).append("|");
		builder.append(payment.getCreditor().getBankCode()).append(".").append(payment.getCreditor().getBranchCode()).append("|");
		builder.append(payment.getCreditor().getAccountId());
		
		return builder.toString();
	}
}