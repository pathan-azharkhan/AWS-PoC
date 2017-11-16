/**
 * 
 */
package com.cts.aws.poc.services.impl;

import iso.std.iso._20022.tech.xsd.pain_001_001.Document;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResult.DeferredResultHandler;

import com.cts.aws.poc.constants.PayloadStatus;
import com.cts.aws.poc.constants.PaymentStatus;
import com.cts.aws.poc.dao.Payload;
import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.exceptions.SystemException;
import com.cts.aws.poc.exceptions.ValidationException;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.services.FileFormatTransformer;
import com.cts.aws.poc.services.FileStorageService;
import com.cts.aws.poc.services.FlowOrchestrator;
import com.cts.aws.poc.services.PayloadPersistenceService;
import com.cts.aws.poc.services.PaymentDetailsPersistenceService;
import com.cts.aws.poc.validations.BusinessValidator;
import com.cts.aws.poc.validations.FormatValidator;

/**
 * @author Azharkhan
 *
 */
@Component
@SuppressWarnings("rawtypes")
public class PaymentFlowOrchestrator implements FlowOrchestrator, InitializingBean {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private InboundXMLFileParser inboundFileParser;
	
	@Autowired
	@Qualifier("xmlFormatValidator")
	private FormatValidator inputFormatValidator;

	@Autowired
	@Qualifier("inboundFileFormatTransformer")
	private FileFormatTransformer inboundFileTransformer;
	
	@Autowired
	@Qualifier("businessDateValidator")
	private BusinessValidator businessDateValidator;
	
	@Autowired
	@Qualifier("txnAmountValidator")
	private BusinessValidator txnAmntValidator;

	private Predicate<List<PaymentDetails>> businessValidationPredicate;
	
	@Autowired
	@Qualifier("proprietoryFileFormatTransformer")
	private FileFormatTransformer outboundFileTransformer;
	
	@Autowired
	@Qualifier("proprietoryFormatValidator")
	private FormatValidator outputFormatValidator;
	
	@Autowired
	private PayloadPersistenceService payloadService;
	
	@Autowired
	@Qualifier("paymentDetailsJPAService")
	private PaymentDetailsPersistenceService paymentsService;
	
	@Override
	public void process(String fileName) {
		
		CompletableFuture.runAsync(new ProcessingJob(fileName))
						.whenComplete((result, throwable) -> {
										
										if (throwable != null) {
											throwable.printStackTrace();
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
		@SuppressWarnings("unchecked")
		public void run() {
			
			// Pull file from AWS S3
			File inputFile = fileStorageService.retrieve(fileName);
			
			// Save to Inbound table
			Payload inboundPayload = payloadService.persistInboundPayload(inputFile);
			
			// Parse file
			Document document = inboundFileParser.parseFile(inputFile);
			
			// Validate the format of the incoming file
			try {
				inputFormatValidator.validate(inputFile);
			} catch (ValidationException e) {
				
				// Update status of Inbound record for failure
				payloadService.updatePayloadStatus(inboundPayload, PayloadStatus.ERROR);
				
				throw new SystemException("Format Validation of input failed", e);
			}
			
			// Transform the Inbound file to Canonical
			PaymentBatch paymentBatch = (PaymentBatch) inboundFileTransformer.transform(document);
			
			// Save payments to DB
			List<PaymentDetails> savedPayments = paymentsService.persistNewBatch(paymentBatch);
			
			// Validate the canonical for business scenarios
			try {
				businessValidationPredicate.test(savedPayments);
			} catch (SystemException se) {
				
				// Update payment statuses in case of Business validation failures
				ValidationException validationEx = (ValidationException) se.getCause();
				paymentsService.updatePaymentsOnValidationFailure(validationEx.getFailedPayments());
				
				// Remove failed payments from the payment batch canonical and Entity list
				paymentBatch = cleanPaymentBatch(paymentBatch, savedPayments, validationEx.getFailedPayments());
			}
			
			// Transform the Canonical to Outbound file format
			String outboundFileContent = (String) outboundFileTransformer.transform(paymentBatch);
			
			String fileName = new StringBuilder(LocalDate.now().toString()).append(".").append(System.currentTimeMillis()).append(".txt").toString();
			
			// Save to Outbound table
			Payload outboundPayload = payloadService.persistOutboundPayload(fileName);
			
			// Validate the format of the outgoing file
			try {
				outputFormatValidator.validate(outboundFileContent);
			} catch (ValidationException e) {
				
				// Update status of Outbound record for failure
				payloadService.updatePayloadStatus(outboundPayload, PayloadStatus.ERROR);
				
				throw new SystemException("Format Validation of output failed", e);
			}
			
			// Publish the output file to downstream S3 bucket
			DeferredResult<Boolean> deferredResult = fileStorageService.store(fileName, outboundFileContent);
			deferredResult.setResultHandler(new FileDispatchResultHandler(inboundPayload, outboundPayload, savedPayments));
		}
	}
	
	private class FileDispatchResultHandler implements DeferredResultHandler {
		
		private final Payload inboundPayload;
		
		private final Payload outboundPayload;
		
		private final List<PaymentDetails> payments;
		
		FileDispatchResultHandler(Payload inboundPayload, Payload outboundPayload, List<PaymentDetails> payments) {
			
			super();
			this.payments = payments;
			this.inboundPayload = inboundPayload;
			this.outboundPayload = outboundPayload;
		}

		@Override
		public void handleResult(Object result) {
			
			PayloadStatus payloadStatus = PayloadStatus.FAILED;
			PaymentStatus paymentStatus = PaymentStatus.ERROR;
			
			if (Boolean.class.cast(result)) {
				
				payloadStatus = PayloadStatus.SENT;
				paymentStatus = PaymentStatus.PROCESSED;
			}
			
			// Update statuses in RDS
			payloadService.updatePayloadStatus(inboundPayload, PayloadStatus.PROCESSED);
			payloadService.updatePayloadStatus(outboundPayload, payloadStatus);
			
			// TODO: Might want to save Payment Errors in case of failure
			paymentsService.updatePaymentsOnFileDispatch(payments, paymentStatus);
		}
	}
	
	private PaymentBatch cleanPaymentBatch(PaymentBatch batchCanonical, List<PaymentDetails> paymentEntities, List<FailedPayment> failedPayments) {
		
		List<String> failedPaymentIds = failedPayments.parallelStream().map(failedPayment -> failedPayment.getPayment().getPaymentId()).collect(Collectors.toList());
		
		/*List<PaymentInstruction> cleanedList = batchCanonical.getPayments().parallelStream().collect(ArrayList::new, new BiConsumer<List<PaymentInstruction>, PaymentInstruction>() {

																			@Override
																			public void accept(List<PaymentInstruction> list, PaymentInstruction pmntInstr) {
																				
																				if (!failedPaymentIds.contains(pmntInstr.getInstrctnId()))
																					list.add(pmntInstr);
																			}
																		},
																	(list1, list2) -> list1.addAll(list2));
		
		batchCanonical.setPayments(cleanedList);*/
		
		batchCanonical.getPayments().removeIf(payment -> failedPaymentIds.contains(payment.getInstrctnId()));
		paymentEntities.removeIf(entity -> failedPaymentIds.contains(entity.getPaymentId()));
		
		return batchCanonical;
	}
		
	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		
		businessValidationPredicate = (payments) -> { 
						try {
						return businessDateValidator.validate(payments) && txnAmntValidator.validate(payments);
					} catch (ValidationException e) {
						
						throw new SystemException("Business Validation failed", e);
					}
			};
	}
}