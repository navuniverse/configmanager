/**
 * 
 */
package com.rockingengineering.configmanager.paramstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParameterResult;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterType;
import com.rockingengineering.configmanager.config.AWSCredentialProvider;
import com.rockingengineering.configmanager.dto.ConfigEnvironment;
import com.rockingengineering.configmanager.dto.ParameterDto;

/**
 * @author naveen
 *
 * @date 25-Jun-2019
 */
@Service
public class SearchPropertyService {

	public ParameterDto getPropertyByName(String name, ConfigEnvironment configEnvironment) {

		AWSSimpleSystemsManagement awsSimpleSystemsManagement = AWSCredentialProvider.getCredential(configEnvironment);

		GetParameterRequest parameterRequest = new GetParameterRequest();
		parameterRequest.withName(name).setWithDecryption(Boolean.valueOf(true));

		GetParameterResult parameterResult = awsSimpleSystemsManagement.getParameter(parameterRequest);

		if (Objects.nonNull(parameterResult) && Objects.nonNull(parameterResult.getParameter())) {
			return getParameterDtoFromResponse(parameterResult.getParameter());
		}

		return null;
	}

	private ParameterDto getParameterDtoFromResponse(Parameter parameter) {

		return ParameterDto.builder()
				.key(parameter.getName())
				.value(parameter.getValue())
				.type(parameter.getType())
				.secure(ParameterType.SecureString.name().equals(parameter.getType()))
				.version(parameter.getVersion())
				.lastModifiedDate(parameter.getLastModifiedDate())
				.build();
	}

	public List<ParameterDto> getPropertiesForPrefix(String prefix, ConfigEnvironment configEnvironment) {

		AWSSimpleSystemsManagement awsSimpleSystemsManagement = AWSCredentialProvider.getCredential(configEnvironment);

		GetParametersByPathRequest parameterRequest =
				new GetParametersByPathRequest()
						.withPath(prefix)
						.withRecursive(true)
						.withWithDecryption(true);

		List<ParameterDto> parameterDtos = new ArrayList<>();

		return getParameters(parameterRequest, awsSimpleSystemsManagement, parameterDtos);
	}

	private List<ParameterDto> getParameters(
			GetParametersByPathRequest paramsRequest, AWSSimpleSystemsManagement awsSimpleSystemsManagement, List<ParameterDto> parameterDtos) {

		try {
			GetParametersByPathResult paramsResult = awsSimpleSystemsManagement.getParametersByPath(paramsRequest);

			for (Parameter parameter : paramsResult.getParameters()) {
				System.out.println("Loading Property: " + parameter.getName());

				parameterDtos.add(getParameterDtoFromResponse(parameter));
			}

			if (paramsResult.getNextToken() != null) {
				getParameters(paramsRequest.withNextToken(paramsResult.getNextToken()), awsSimpleSystemsManagement, parameterDtos);
			}

		} catch (Exception e) {
			System.err.println("Error fetching proeprties for prefix: " + e);
		}

		return parameterDtos;
	}

}