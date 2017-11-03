/**
 * 
 */
package com.cts.aws.poc.validations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.cts.aws.poc.constants.FailureType;
import com.cts.aws.poc.exceptions.ValidationException;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.models.PaymentBatch;

/**
 * @author Azharkhan
 *
 */
@Component
public class BusinessDateValidator implements BusinessValidator<PaymentBatch> {

	@Override
	public boolean validate(PaymentBatch input) throws ValidationException {
		
		LocalDate date = LocalDate.now();
		
		List<FailedPayment> failedPayments = new ArrayList<>();
		
		input.getPayments().parallelStream().forEach(payment -> {
			
			if (payment.getValueDate() == null || !date.equals(payment.getValueDate())) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_BUSINESS_DATE, "Value date is not current date", payment));
			}
		});
		
		if (CollectionUtils.isEmpty(failedPayments))
			return true;
		else
			throw new ValidationException("One or more payments in batch have failed", failedPayments);
	}
}