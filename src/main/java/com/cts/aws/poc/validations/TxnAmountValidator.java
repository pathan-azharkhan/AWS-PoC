/**
 * 
 */
package com.cts.aws.poc.validations;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
public class TxnAmountValidator implements BusinessValidator<PaymentBatch> {

	@Override
	public boolean validate(PaymentBatch input) throws ValidationException {

		List<FailedPayment> failedPayments = new ArrayList<>();
		
		input.getPayments().parallelStream().forEach(payment -> {
			
			if (payment.getTxnAmnt() < 0) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_TXN_AMNT, "Invalid transaction amount", payment));
			}
			if (StringUtils.isBlank(payment.getCurrency())) {
				
				failedPayments.add(new FailedPayment(FailureType.INVALID_TXN_CCY, "Invalid transaction currency", payment));
			}
		});
		
		if (CollectionUtils.isEmpty(failedPayments))
			return true;
		else
			throw new ValidationException("One or more payments in batch have failed", failedPayments);
	}
}