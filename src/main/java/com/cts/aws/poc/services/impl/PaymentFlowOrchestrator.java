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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	
	private static final Logger LOGGER = LogManager.getLogger(PaymentFlowOrchestrator.class);
	
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
											LOGGER.error("Failed while processing file {}. Reason: {}", fileName, throwable);
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
			
			LOGGER.info("Beginning processing of file {} ...", fileName);
			
			// Pull file from AWS S3
			File inputFile = fileStorageService.retrieve(fileName);
			
			// Save to Inbound table
			Payload inboundPayload = payloadService.persistInboundPayload(inputFile);
			
			LOGGER.info("Saved new record bearing id {} to Payload table for file {}", inboundPayload.getPayloadId(), fileName);
			
			// Parse file
			Document document = inboundFileParser.parseFile(inputFile);
			
			// Validate the format of the incoming file
			try {
				inputFormatValidator.validate(inputFile);
			} catch (ValidationException e) {
				
				LOGGER.error("Format Validation of file {} failed! Reason: {}", fileName, e);
				// Update status of Inbound record for failure
				payloadService.updatePayloadStatus(inboundPayload, PayloadStatus.ERROR);
				
				throw new SystemException("Format Validation of input failed", e);
			}
			LOGGER.info("Format of file {} is valid", fileName);
			
			// Transform the Inbound file to Canonical
			PaymentBatch paymentBatch = (PaymentBatch) inboundFileTransformer.transform(document);
			
			LOGGER.info("Transformed file {} to Canonical", fileName);
			LOGGER.debug("Transformed Canonical -\n {}", paymentBatch);
			
			// Save payments to DB
			List<PaymentDetails> savedPayments = paymentsService.persistNewBatch(paymentBatch);
			
			LOGGER.info("Persisted payments for batch id {}", paymentBatch.getBatchId());
			
			// Validate the canonical for business scenarios
			try {
				businessValidationPredicate.test(savedPayments);
			} catch (SystemException se) {
				
				LOGGER.error("Business Validation of batch {} failed! Reason: {}", paymentBatch.getBatchId(), se);
				
				// Update payment statuses in case of Business validation failures
				ValidationException validationEx = (ValidationException) se.getCause();
				paymentsService.updatePaymentsOnValidationFailure(validationEx.getFailedPayments());
				
				LOGGER.info("Updated status of payments under batch {} to {}", paymentBatch.getBatchId(), PaymentStatus.REJECTED);
				
				// Remove failed payments from the payment batch canonical and Entity list
				paymentBatch = cleanPaymentBatch(paymentBatch, savedPayments, validationEx.getFailedPayments());
				
				LOGGER.info("Removed failed payments from the payment batch canonical");
				
				// Check if at all the batch has any payments remaining to be processed further
				if (paymentBatch.getTotalTxns() == 0) {

					LOGGER.warn("No payments to process in batch {}!", paymentBatch.getBatchId());
					return;
				}
			}
			
			LOGGER.debug("Processing {} payments for generating output file", paymentBatch.getTotalTxns());
			
			// Transform the Canonical to Outbound file format
			String outboundFileContent = (String) outboundFileTransformer.transform(paymentBatch);
			
			String outboundFileName = new StringBuilder(LocalDate.now().toString()).append(".").append(System.currentTimeMillis()).append(".txt").toString();
			
			// Save to Outbound table
			Payload outboundPayload = payloadService.persistOutboundPayload(outboundFileName);
			
			LOGGER.info("Saved new record for outbound payload with name {} and id {}", outboundFileName, outboundPayload.getPayloadId());
			
			// Validate the format of the outgoing file
			try {
				outputFormatValidator.validate(outboundFileContent);
			} catch (ValidationException e) {
				
				LOGGER.error("Format Validation of outbound file {} failed! Reason: {}", fileName, e);
				
				// Update status of Outbound record for failure
				payloadService.updatePayloadStatus(outboundPayload, PayloadStatus.ERROR);
				
				LOGGER.info("Updated status of payload {} to {}", outboundPayload.getPayloadId(), PayloadStatus.ERROR);
				
				throw new SystemException("Format Validation of output failed", e);
			}
			
			// Publish the output file to downstream S3 bucket
			DeferredResult<Boolean> deferredResult = fileStorageService.store(outboundFileName, outboundFileContent);
			deferredResult.setResultHandler(new FileDispatchResultHandler(inboundPayload, outboundPayload, savedPayments));
			
			LOGGER.info("Submitted outbound file {} for upload", outboundFileName);
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
				
				LOGGER.info("Successfully uploaded file {} to S3", outboundPayload.getName());
			} else {
				LOGGER.error("Failed to upload file {} to S3", outboundPayload.getName());
			}
			
			// Update statuses in RDS
			payloadService.updatePayloadStatus(inboundPayload, PayloadStatus.PROCESSED);
			LOGGER.info("Updated status of inbound payload {} to {}", inboundPayload.getPayloadId(), PayloadStatus.PROCESSED);
			
			payloadService.updatePayloadStatus(outboundPayload, payloadStatus);
			LOGGER.info("Updated status of outbound payload {} to {}", outboundPayload.getPayloadId(), payloadStatus);
			
			// TODO: Might want to save Payment Errors in case of failure
			paymentsService.updatePaymentsOnFileDispatch(payments, paymentStatus);
			LOGGER.info("Updated status of payments to {}", paymentStatus);
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
		
		// Update the NbOfTxns in the batch canonical accordingly, after removal of failed payments from under the batch
		batchCanonical.setTotalTxns(batchCanonical.getPayments().size());
		
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