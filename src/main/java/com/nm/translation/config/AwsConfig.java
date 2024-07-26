package com.nm.translation.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

/**
 * Configuration Class used for building AWS Clients with valid credentials.
 * Only change this class if adding a new AWS Client type or messing with current Client configurations.
 */
@Slf4j
@Configuration
public class AwsConfig {
	/**
	 * SQS Client Bean which configures and builds a Client with the necessary credentials
	 */
	@Bean
	public SqsClient sqsClient() {
		try {
			log.info("#####: Creating SQS Connection");
			return SqsClient.builder()
					.credentialsProvider(DefaultCredentialsProvider.create())
					.region(Region.US_EAST_1)
					.build();

		} catch (Exception ex) {
			log.error("Exception: {}", ex);
		}

		return null;
	}

	/**
	 * S3 Client Bean which configures and builds a Client with the necessary credentials
	 */
	@Bean
	public S3Client s3Client() {
		try {
			log.info("#####: Creating S3 Connection");
			return S3Client.builder()
					.credentialsProvider(DefaultCredentialsProvider.create())
					.region(Region.US_EAST_1)
					.build();
		} catch (Exception ex) {
			log.error("Exception: {}", ex);
		}

		return null;
	}
}
