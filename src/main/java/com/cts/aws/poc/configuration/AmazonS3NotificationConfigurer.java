/**
 * 
 */
package com.cts.aws.poc.configuration;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.auth.policy.conditions.ConditionFactory;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.QueueConfiguration;
import com.amazonaws.services.s3.model.S3Event;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3NotificationConfigurer implements InitializingBean {
	
	private static final String NOTFCTN_CONFIG_NAME = "qConfiguration";
	
	@Value("${amazon.s3.inbound.bucket.name}")
	private String bucketName;
	
	@Value("${amazon.s3.inbound.bucket.arn}")
	private String bucketARN;
	
	@Value("${amazon.sqs.notfctn.queue.arn}")
	private String queueARN;
	
	@Value("${amazon.sqs.notfctn.queue.url}")
	private String queueURL;
	
	@Autowired
	private AmazonS3 amazonS3Client;
	
	public void afterPropertiesSet() throws Exception {
		
		// Action=GetQueueUrl&Version=2012-11-05&QueueName=S3NotificationQueue
		
		BucketNotificationConfiguration existingConfiguration = amazonS3Client.getBucketNotificationConfiguration(bucketName);
		
		if (existingConfiguration == null || existingConfiguration.getConfigurationByName(NOTFCTN_CONFIG_NAME) == null) {
		
			// AmazonSQS Permissions
			AmazonSQS sqsClient = AmazonSQSClientBuilder.defaultClient();
			
			Policy policy = new Policy().withStatements(new Statement(Effect.Allow)
																.withPrincipals(Principal.AllUsers)
																.withActions(SQSActions.SendMessage)
																.withConditions(ConditionFactory.newSourceArnCondition(bucketARN)));
			
			Map<String, String> queueAttributes = new HashMap<>();
			queueAttributes.put(QueueAttributeName.Policy.toString(), policy.toJson());
			
			sqsClient.setQueueAttributes(new SetQueueAttributesRequest(queueURL, queueAttributes));
			
			// Amazon S3 Event Notification Configuration
			QueueConfiguration qConfiguration = new QueueConfiguration(queueARN, EnumSet.of(S3Event.ObjectCreated));
			BucketNotificationConfiguration bucketNotificationConfiguration = new BucketNotificationConfiguration(NOTFCTN_CONFIG_NAME, qConfiguration);
	
			amazonS3Client.setBucketNotificationConfiguration(bucketName, bucketNotificationConfiguration);
			
			System.out.println("Configured S3 Notifications for bucket: " + bucketName);
		}
	}
}