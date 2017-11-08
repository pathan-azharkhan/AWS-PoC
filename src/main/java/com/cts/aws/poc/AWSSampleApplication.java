package com.cts.aws.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextResourceLoaderAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.jdbc.AmazonRdsDatabaseAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {ContextStackAutoConfiguration.class, ContextResourceLoaderAutoConfiguration.class, MessagingAutoConfiguration.class, AmazonRdsDatabaseAutoConfiguration.class})
public class AWSSampleApplication {

    public static void main(String[] args) {
		SpringApplication.run(AWSSampleApplication.class, args);
	}
}