/**
 * 
 */
package com.rockingengineering.configmanager.config;

import java.util.EnumMap;
import java.util.Map;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.rockingengineering.configmanager.dto.ConfigEnvironment;

import lombok.experimental.UtilityClass;

/**
 * @author naveen
 *
 * @date 25-Jun-2019
 */
@UtilityClass
public class AWSCredentialProvider {

	private static Map<ConfigEnvironment, AWSSimpleSystemsManagement> credentialMap = new EnumMap<>(ConfigEnvironment.class);

	public static void addCredential(ConfigEnvironment environment, AWSSimpleSystemsManagement simpleSystemsManagement) {
		credentialMap.put(environment, simpleSystemsManagement);
	}

	public static AWSSimpleSystemsManagement getCredential(ConfigEnvironment configEnvironment) {
		return credentialMap.get(configEnvironment);
	}
}