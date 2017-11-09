/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.util.EnumSet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.QueueConfiguration;
import com.amazonaws.services.s3.model.S3Event;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3NotificationConfigurer implements InitializingBean {
	
	private static final String QUEUE_NAME = "S3NotificationQueue";
	
	@Value("${amazon.s3.bucket.name}")
	private String bucketName;
	
	@Value("${amazon.sqs.notfctn.queue.arn}")
	private String queueARN;
	
	@Autowired
	private AmazonS3 amazonS3Client;
	
	public void afterPropertiesSet() throws Exception {
		
//		if (amazonS3Client.getBucketNotificationConfiguration(bucketName) == null) {

			QueueConfiguration qConfiguration = new QueueConfiguration(queueARN, EnumSet.of(S3Event.ObjectCreated));
			BucketNotificationConfiguration bucketNotificationConfiguration = new BucketNotificationConfiguration("qConfiguration", qConfiguration);

			amazonS3Client.setBucketNotificationConfiguration(bucketName, bucketNotificationConfiguration);
			
			System.out.println("Configured S3 Notifications for bucket: " + bucketName);
//		}
		
//		AmazonSQS
	}
	
	@SqsListener(QUEUE_NAME)
	public void listenForS3Notifications(String message, @Header("ApproximateFirstReceiveTimestamp") String approximateFirstReceiveTimestamp) {
		
		System.out.println("Message received from SQS: " + message);
		System.out.print(" at: " + approximateFirstReceiveTimestamp);
	}
}