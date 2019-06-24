package com.rockingengineering.configmanager.paramstore;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterType;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.PutParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.Tag;
import com.rockingengineering.configmanager.dto.ConfigEnvironment;
import com.rockingengineering.configmanager.dto.ConfigFileHeaders;
import com.rockingengineering.configmanager.dto.ParameterDto;

@Service
public class ParameterStorePropertyManager {

	@Autowired
	private Environment environment;

	public String uploadParameters(MultipartFile parameterFile, ConfigEnvironment configEnvironment) {

		String message = "Parameters Successfully Uploaded";

		if (parameterFile != null && !parameterFile.isEmpty()) {

			try (Reader reader = new InputStreamReader(parameterFile.getInputStream())) {

				Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader(ConfigFileHeaders.getParamFileHeaders()).withFirstRecordAsHeader().parse(reader);

				if (records != null) {

					System.out.println("Parameter File Successfully Parsed. Creating Parameter DTO Now");

					List<ParameterDto> parameterDtos = createParametersFromCsv(records);

					try {
						if (!parameterDtos.isEmpty()) {
							System.out.println("Uploading " + parameterDtos.size() + " Properties to Parameter Store");
							saveParameters(parameterDtos, configEnvironment);
							message = parameterDtos.size() + " Parameters Uploaded on Parameter Store";
						} else {
							message = "No Valid Config Records Found in Uploaded file";
						}
					} catch (Exception e) {
						message = "Error uploading config on parameter store";
					}
				} else {
					message = "No Properties Found in File";
				}
			} catch (IOException e) {
				System.err.println("Error Reading File: " + e);
			}

		} else {
			message = "Uploaded File is Null or contains No Content";
		}

		return message;
	}

	private List<ParameterDto> createParametersFromCsv(Iterable<CSVRecord> records) {
		List<ParameterDto> parameterDtos = new ArrayList<>();

		for (CSVRecord record : records) {

			try {

				String name = record.get(ConfigFileHeaders.Name);
				String value = record.get(ConfigFileHeaders.Value);
				boolean secure = StringUtils.startsWithIgnoreCase(record.get(ConfigFileHeaders.Secure), "true");
				String description = record.get(ConfigFileHeaders.Description);
				String tag1 = record.get(ConfigFileHeaders.Tag1);
				String tagValue1 = record.get(ConfigFileHeaders.TagValue1);
				String tag2 = record.get(ConfigFileHeaders.Tag2);
				String tagValue2 = record.get(ConfigFileHeaders.TagValue2);
				String tag3 = record.get(ConfigFileHeaders.Tag3);
				String tagValue3 = record.get(ConfigFileHeaders.TagValue3);

				Collection<Tag> tags = new ArrayList<>();

				if (StringUtils.hasText(tag1) && StringUtils.hasText(tagValue1)) {
					tags.add(new Tag().withKey(tag1).withValue(tagValue1));
				}

				if (StringUtils.hasText(tag2) && StringUtils.hasText(tagValue2)) {
					tags.add(new Tag().withKey(tag2).withValue(tagValue2));
				}

				if (StringUtils.hasText(tag3) && StringUtils.hasText(tagValue3)) {
					tags.add(new Tag().withKey(tag3).withValue(tagValue3));
				}

				if (StringUtils.hasText(name) && StringUtils.hasText(value)) {
					parameterDtos.add(ParameterDto.builder().key(name).value(value).secure(secure).description(description).tags(tags).build());
				}

			} catch (Exception e) {
				System.err.println("Error processing Config Parameter CSV entry from file: " + e);
			}

		}

		return parameterDtos;
	}

	private void saveParameters(List<ParameterDto> parameterDtos, ConfigEnvironment configEnvironment) {

		AWSSimpleSystemsManagement awsSimpleSystemsManagement = getAwsSystemManager(configEnvironment);
		String kmsId = environment.getProperty("aws.ssm.kms.key");

		if (ConfigEnvironment.PROD == configEnvironment) {
			kmsId = environment.getProperty("aws.ssm.prod.kms.key");
		}

		for (ParameterDto parameterDto : parameterDtos) {
			try {
				ParameterType parameterType = ParameterType.String;

				if (parameterDto.isSecure()) {
					parameterType = ParameterType.SecureString;
				}

				PutParameterRequest putParameterRequest =
						new PutParameterRequest()
								.withName(parameterDto.getKey())
								.withValue(parameterDto.getValue())
								.withType(parameterType)
								.withDescription(parameterDto.getDescription());

				if (ParameterType.SecureString == parameterType) {
                                       putParameterRequest = putParameterRequest.withKeyId(kmsId);
                                }
				
				if (parameterDto.getTags().isEmpty()) {
					putParameterRequest = putParameterRequest.withOverwrite(true);
				} else {
					putParameterRequest = putParameterRequest.withTags(parameterDto.getTags());
				}

				PutParameterResult putParameterResult = awsSimpleSystemsManagement.putParameter(putParameterRequest);

				if (Objects.nonNull(putParameterResult)
						&& Objects.nonNull(putParameterResult.getSdkHttpMetadata())
						&& Objects.nonNull(putParameterResult.getSdkResponseMetadata())) {

					System.out.println("Successfully Updated Property: " + parameterDto.getKey() + ". Version: " + putParameterResult.getVersion() + ". StatusCode: "
							+ putParameterResult.getSdkHttpMetadata().getHttpStatusCode() + ". ResponseId: " + putParameterResult.getSdkResponseMetadata().getRequestId());

				} else {

					System.err.println("Failed to Update Property: " + parameterDto.getKey() + " on Parameter Store");
				}
			} catch (Exception e) {
				System.err.println("Error uploading parameter: " + e);
			}
		}

	}

	private AWSSimpleSystemsManagement getAwsSystemManager(ConfigEnvironment configEnvironment) {

		String accessKey = environment.getProperty("aws.ssm.access.key");
		String secretKey = environment.getProperty("aws.ssm.secret.key");

		if (ConfigEnvironment.PROD == configEnvironment) {
			accessKey = environment.getProperty("aws.ssm.prod.access.key");
			secretKey = environment.getProperty("aws.ssm.prod.secret.key");
		}

		AWSCredentialsProvider credentials =
				new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(accessKey, secretKey));

		System.out.println("Generated AWS Credentials for Parameters");

		return AWSSimpleSystemsManagementClientBuilder
				.standard()
				.withCredentials(credentials)
				.withRegion(environment.getProperty("aws.ssm.region"))
				.build();
	}
}
