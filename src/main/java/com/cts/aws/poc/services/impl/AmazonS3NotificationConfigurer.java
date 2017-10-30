/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.util.EnumSet;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.QueueConfiguration;
import com.amazonaws.services.s3.model.S3Event;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3NotificationConfigurer implements InitializingBean {
	
	@Value("${amazon.s3.bucket.name}")
	private String bucketName;
	
	@Value("${amazon.sqs.notfctn.queue.arn}")
	private String queueARN;
	
	public void afterPropertiesSet() throws Exception {
		
		AmazonS3 amazonS3Client = AmazonS3ClientBuilder.defaultClient();
		
		if (amazonS3Client.getBucketNotificationConfiguration("qConfiguration") == null) {

			QueueConfiguration qConfiguration = new QueueConfiguration(queueARN, EnumSet.of(S3Event.ObjectCreated));
			BucketNotificationConfiguration bucketNotificationConfiguration = new BucketNotificationConfiguration("qConfiguration", qConfiguration);

			amazonS3Client.setBucketNotificationConfiguration(bucketName, bucketNotificationConfiguration);
		}
		
//		AmazonSQS
	}
}