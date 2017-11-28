/**
 * 
 */
package com.cts.aws.poc.configuration;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.event.S3EventNotification.S3Entity;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.cts.aws.poc.services.FlowOrchestrator;

/**
 * @author Azharkhan
 *
 */
@Component
public class SqsListenerConfigurer {
	
	private static final Logger LOGGER = LogManager.getLogger(SqsListenerConfigurer.class);
	
	private static final String QUEUE_NAME = "S3NotificationQueue";
	
	@Autowired
	private FlowOrchestrator flowOrchestrator;

	@SqsListener(QUEUE_NAME)
	public void onS3EventNotification(String message) {
		
		LOGGER.info("Message received from SQS~{}. Contents: {}", QUEUE_NAME, message);
		
		try {
			S3EventNotification eventNotification = S3EventNotification.parseJson(message);
			List<S3EventNotificationRecord> records = eventNotification.getRecords();
			
			if (CollectionUtils.isNotEmpty(records)) {
				
				LOGGER.info("Publishing {} file(s) to Orchestrator", records.size());
				
				records.parallelStream().forEach(record -> {
	
					S3Entity s3Entity = record.getS3();
					String fileName = s3Entity.getObject().getKey();
	
					flowOrchestrator.process(fileName);
				});
			}
		} catch (SdkClientException e) {
			LOGGER.error("Unable to parse message received from SQS");
		}
	}
}