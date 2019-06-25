/**
 * 
 */
package com.rockingengineering.configmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.rockingengineering.configmanager.dto.ConfigEnvironment;

/**
 * @author naveen
 *
 * @date 25-Jun-2019
 */
@Configuration
public class AmazonSSMConfig {

	@Value("${aws.ssm.access.key}")
	private String devAccessKey;

	@Value("${aws.ssm.secret.key}")
	private String devSecretKey;

	@Value("${aws.ssm.prod.access.key}")
	private String prodAccessKey;

	@Value("${aws.ssm.prod.secret.key}")
	private String prodSecretKey;

	@Value("${aws.ssm.region}")
	private String ssmRegion;

	@Bean
	public AWSSimpleSystemsManagement devCrednetials() {
		return createSystemManager(devAccessKey, devSecretKey, ConfigEnvironment.DEV);
	}

	@Bean
	public AWSSimpleSystemsManagement prodCrednetials() {
		return createSystemManager(prodAccessKey, prodSecretKey, ConfigEnvironment.PROD);
	}

	private AWSSimpleSystemsManagement createSystemManager(String accessKey, String secretKey, ConfigEnvironment configEnvironment) {

		AWSSimpleSystemsManagement simpleSystemsManagement = null;

		if (StringUtils.hasText(accessKey) && StringUtils.hasText(secretKey)) {

			AWSCredentialsProvider credentials =
					new AWSStaticCredentialsProvider(
							new BasicAWSCredentials(accessKey, secretKey));

			simpleSystemsManagement =
					AWSSimpleSystemsManagementClientBuilder
							.standard()
							.withCredentials(credentials)
							.withRegion(ssmRegion)
							.build();

			System.out.println("Generated AWS Credentials for " + configEnvironment + " Environment");

			AWSCredentialProvider.addCredential(configEnvironment, simpleSystemsManagement);
		}

		return simpleSystemsManagement;
	}

}