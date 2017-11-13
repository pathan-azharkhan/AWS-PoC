/**
 * 
 */
package com.cts.aws.poc.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.models.PaymentBatch;

/**
 * @author Azharkhan
 *
 */
public interface PaymentDetailsPersistenceService {
	
	List<PaymentDetails> persistNewBatch(PaymentBatch batch);

	void updatePaymentsOnValidationFailure(List<FailedPayment> failedPayments);
	
	Map<String, Map<String, Integer>> getDashboardData(Date selectedDate);
}