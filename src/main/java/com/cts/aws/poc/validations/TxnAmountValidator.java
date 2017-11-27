/**
 * 
 */
package com.cts.aws.poc.validations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cts.aws.poc.constants.FailureType;
import com.cts.aws.poc.dao.PaymentDetails;
import com.cts.aws.poc.exceptions.ValidationException;
import com.cts.aws.poc.models.FailedPayment;

/**
 * @author Azharkhan
 *
 */
@Component
public class TxnAmountValidator implements BusinessValidator<List<PaymentDetails>> {
	
	private static final Logger LOGGER = LogManager.getLogger(TxnAmountValidator.class);

	@Override
	public boolean validate(List<PaymentDetails> input) throws ValidationException {

		List<FailedPayment> failedPayments = Collections.synchronizedList(new ArrayList<>());
		
		LOGGER.info("Validating the transaction amount and currency of payments...");
		
		input.parallelStream().forEach(payment -> {
			
			if (payment.getTxnAmount() < 0) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_TXN_AMNT, "Invalid transaction amount", payment));
			}
			if (StringUtils.isBlank(payment.getTxnCurrency())) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_TXN_CCY, "Invalid transaction currency", payment));
			}
		});
		
		if (CollectionUtils.isEmpty(failedPayments))
			return true;
		else
			throw new ValidationException("One or more payments in batch have failed", failedPayments);
	}
}