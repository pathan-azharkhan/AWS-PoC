/**
 * 
 */
package com.cts.aws.poc.validations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cts.aws.poc.constants.FailureType;
import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.exceptions.ValidationException;
import com.cts.aws.poc.models.FailedPayment;
import com.cts.aws.poc.utils.DateUtils;

/**
 * @author Azharkhan
 *
 */
@Component
public class BusinessDateValidator implements BusinessValidator<List<PaymentDetails>> {
	
	private static final Logger LOGGER = LogManager.getLogger(BusinessDateValidator.class);

	@Override
	public boolean validate(List<PaymentDetails> input) throws ValidationException {
		
		LocalDate date = LocalDate.now();
		
		List<FailedPayment> failedPayments = Collections.synchronizedList(new ArrayList<>());
		
		LOGGER.info("Validating the Business dates of payments...");
		
		input.parallelStream().forEach(payment -> {
			
			if (payment.getValueDate() == null || !DateUtils.getDateFromLocalDate(date).equals(payment.getValueDate())) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_BUSINESS_DATE, "Value date is not current date", payment));
			}
		});
		
		if (CollectionUtils.isEmpty(failedPayments))
			return true;
		else
			throw new ValidationException("One or more payments in batch have failed", failedPayments);
	}
}