/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.kms.model.UnsupportedOperationException;
import com.cts.aws.poc.constants.PaymentStatus;
import com.cts.aws.poc.constants.SystemConstants;
import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.dao.PaymentDetailsRepository;
import com.cts.aws.poc.dao.PaymentErrors;
import com.cts.aws.poc.dao.PaymentErrorsRepository;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.services.PaymentDetailsPersistenceService;

/**
 * @author Azharkhan
 *
 */
@Service
public class PaymentDetailsJPAService implements PaymentDetailsPersistenceService {
	
	@Autowired
	private PaymentDetailsRepository paymentRepository;
	
	@Autowired
	private PaymentErrorsRepository paymentErrorsRepository;

	@Override
	public List<PaymentDetails> persistNewBatch(PaymentBatch batch) {
		
		String batchId = batch.getBatchId();
		
		List<PaymentDetails> payments = Collections.synchronizedList(new ArrayList<>(batch.getPayments().size()));
		
		batch.getPayments().parallelStream().forEach(pmntInstr -> {
			
			PaymentDetails payment = new PaymentDetails();
			
			payment.setPaymentId(pmntInstr.getInstrctnId());
			payment.setBatchId(batchId);
			payment.setTxnAmount(pmntInstr.getTxnAmnt());
			payment.setTxnCurrency(pmntInstr.getCurrency());
			payment.setStatus(PaymentStatus.NEW.name());
			payment.setCreatedBy(SystemConstants.SYSTEM);
			payment.setCreatedDate(new Date());
			
			payments.add(payment);
		});
		
		return (List<PaymentDetails>) paymentRepository.save(payments);
	}
	
	@Override
	@Transactional
	public void updatePaymentsOnValidationFailure(List<FailedPayment> failedPayments) {
		
		List<PaymentDetails> paymentsToBeUpdated = Collections.synchronizedList(new ArrayList<>(failedPayments.size()));
		List<PaymentErrors> errorsToBeCreated = Collections.synchronizedList(new ArrayList<>(failedPayments.size()));
		
		failedPayments.parallelStream().forEach(failedPayment -> {
			
			PaymentDetails payment = failedPayment.getPayment();
			
			payment.setStatus(PaymentStatus.REJECTED.name());
			payment.setUpdatedBy(SystemConstants.SYSTEM);
			payment.setUpdatedDate(new Date());
			
			paymentsToBeUpdated.add(payment);
			
			PaymentErrors paymentError = new PaymentErrors();
			paymentError.setId(UUID.randomUUID().toString());
			paymentError.setPaymentId(payment.getPaymentId());
			paymentError.setErrorCode(failedPayment.getFailureType().name());
			paymentError.setErrorDescription(failedPayment.getFailureReason());
			
			errorsToBeCreated.add(paymentError);
		});
		
		paymentRepository.save(paymentsToBeUpdated);
		paymentErrorsRepository.save(errorsToBeCreated);
	}
	
	@Override
	public Map<String, Map<String, Integer>> getDashboardData(Date selectedDate) {
		throw new UnsupportedOperationException("This operation is currently not supported by JPA service");
	}

	@Override
	public void updatePaymentsOnFileDispatch(List<PaymentDetails> payments, PaymentStatus paymentStatus) {
		
		payments.forEach(payment -> {
			
			payment.setStatus(paymentStatus.name());
			payment.setUpdatedBy(SystemConstants.SYSTEM);
			payment.setUpdatedDate(new Date());
		});
		
		paymentRepository.save(payments);
	}
}