/**
 * 
 */
package com.cts.aws.poc.dao.helper;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.aws.poc.constants.PayloadStatus;
import com.cts.aws.poc.constants.PayloadType;
import com.cts.aws.poc.constants.SystemConstants;
import com.cts.aws.poc.dao.Payload;
import com.cts.aws.poc.dao.PayloadRepository;
import com.cts.aws.poc.services.PayloadPersistenceService;

/**
 * @author Azharkhan
 *
 */
@Service
public class PayloadJPAService implements PayloadPersistenceService {
	
	@Autowired
	private PayloadRepository payloadRepository;

	@Override
	public Payload persistInboundPayload(File inboundFile) {
		
		Payload inboundPayload = new Payload();
		
		inboundPayload.setPayloadId(UUID.randomUUID().toString());
		inboundPayload.setPayloadName(inboundFile.getName());
		inboundPayload.setPayloadType(PayloadType.INBOUND.getIdentifier());
		inboundPayload.setStatus(PayloadStatus.NEW.name());
		inboundPayload.setCreatedBy(SystemConstants.SYSTEM);
		inboundPayload.setCreatedDate(new Date());
		
		return payloadRepository.save(inboundPayload);
	}
	
	@Override
	public Payload persistOutboundPayload(String fileName) {
		
		Payload outboundPayload = new Payload();
		
		outboundPayload.setPayloadId(UUID.randomUUID().toString());
		outboundPayload.setPayloadName(fileName);
		outboundPayload.setPayloadType(PayloadType.OUTBOUND.getIdentifier());
		outboundPayload.setStatus(PayloadStatus.CREATED.name());
		outboundPayload.setCreatedBy(SystemConstants.SYSTEM);
		outboundPayload.setCreatedDate(new Date());
		
		return payloadRepository.save(outboundPayload);
	}
	
	@Override
	public Payload updatePayloadStatus(Payload payload, PayloadStatus payloadStatus) {
		
		payload.setStatus(payloadStatus.name());
		payload.setUpdatedBy(SystemConstants.SYSTEM);
		payload.setUpdatedDate(new Date());
		
		return payloadRepository.save(payload);
	}
}