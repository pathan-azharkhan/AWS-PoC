/**
 * 
 */
package com.cts.aws.poc.services.impl;

import org.springframework.stereotype.Component;

import iso.std.iso._20022.tech.xsd.pain_001_001.Document;

import com.cts.aws.poc.models.PaymentBatch;
import com.cts.aws.poc.services.FileFormatTransformer;

/**
 * @author Azharkhan
 *
 */
@Component
public class InboundFileFormatTransformer implements FileFormatTransformer<Document, PaymentBatch> {

	@Override
	public PaymentBatch transform(Document input) {
		
		return null;
	}
}