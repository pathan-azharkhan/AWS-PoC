/**
 * 
 */
package com.cts.aws.poc.services.impl;

import iso.std.iso._20022.tech.xsd.pain_001_001.Document;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import com.cts.aws.poc.exceptions.SystemException;
import com.cts.aws.poc.exceptions.ValidationException;
import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.services.FileFormatTransformer;
import com.cts.aws.poc.services.FileStorageService;
import com.cts.aws.poc.services.FlowOrchestrator;
import com.cts.aws.poc.validations.BusinessValidator;
import com.cts.aws.poc.validations.FormatValidator;

/**
 * @author Azharkhan
 *
 */
@Component
public class PaymentFlowOrchestrator implements FlowOrchestrator, InitializingBean {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private InboundXMLFileParser inboundFileParser;
	
	@Autowired
	private FormatValidator<Document> inputFormatValidator;

	@Autowired
	private FileFormatTransformer<Document, PaymentBatch> inboundFileTransformer;
	
	@Autowired
	@Qualifier("businessDateValidator")
	private BusinessValidator<PaymentBatch> businessDateValidator;
	
	@Autowired
	@Qualifier("txnAmountValidator")
	private BusinessValidator<PaymentBatch> txnAmntValidator;

	private Predicate<PaymentBatch> businessValidationPredicate;
	
	@Autowired
	private FileFormatTransformer<PaymentBatch, String> outboundFileTransformer;
	
	@Autowired
	private FormatValidator<String> outputFormatValidator;
	
	@Override
	public void process(String fileName) {
		
		CompletableFuture.runAsync(new ProcessingJob(fileName))
						.whenComplete((result, throwable) -> {
										
										if (throwable != null) {
											// TODO: Mark the payment file as error-ed
										}
									});
	}
	
	private class ProcessingJob implements Runnable {
		
		private final String fileName;

		public ProcessingJob(String fileName) {
			
			super();
			this.fileName = fileName;
		}

		@Override
		public void run() {
			
			// Pull file from AWS S3
			File inputFile = fileStorageService.retrieve(fileName);
			
			// TODO: Save to Inbound table
			
			// Parse file
			Document document = inboundFileParser.parseFile(inputFile);
			
			// Validate the format of the incoming file
			try {
				inputFormatValidator.validate(document);
			} catch (ValidationException e) {
				
				// TODO: Update status of Inbound record for failure
				throw new SystemException("Format Validation of input failed", e);
			}
			
			// Transform the Inbound file to Canonical
			PaymentBatch paymentBatch = inboundFileTransformer.transform(document);
			
			// TODO: Save payments to DB
			
			// Validate the canonical for business scenarios
			businessValidationPredicate.test(paymentBatch);
			
			// TODO: Update payment statuses in case of Business validation failures
			
			// Transform the Canonical to Outbound file format
			String outboundFileContent = outboundFileTransformer.transform(paymentBatch);
			
			// TODO: Save to Outbound table
			
			// Validate the format of the outgoing file
			try {
				outputFormatValidator.validate(outboundFileContent);
			} catch (ValidationException e) {
				
				// TODO: Update status of Outbound record for failure
				throw new SystemException("Format Validation of output failed", e);
			}
			
			// Publish the output file to downstream S3 bucket
			DeferredResult<Boolean> deferredResult = fileStorageService.store(outboundFileContent);
			
			// TODO: Update statuses in RDS
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		businessValidationPredicate = (paymentBatch) -> { 
						try {
						return businessDateValidator.validate(paymentBatch) && txnAmntValidator.validate(paymentBatch);
					} catch (ValidationException e) {
						
						throw new SystemException("Business Validation failed", e);
					}
			};
	}
}