/**
 * 
 */
package com.cts.aws.poc.services;

import java.io.File;

import com.cts.aws.poc.constants.PayloadStatus;
import com.cts.aws.poc.dao.Payload;

/**
 * @author Azharkhan
 *
 */
public interface PayloadPersistenceService {

	Payload persistInboundPayload(File inboundFile);
	
	Payload persistOutboundPayload(String fileName);
	
	Payload updatePayloadStatus(Payload payload, PayloadStatus payloadStatus);
}