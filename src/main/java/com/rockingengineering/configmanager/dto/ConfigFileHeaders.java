/**
 * 
 */
package com.rockingengineering.configmanager.dto;

/**
 * @author naveen
 *
 * @date 14-Jun-2019
 */
public enum ConfigFileHeaders {

	Name,
	Value,
	Secure,
	Description,
	Tag1,
	TagValue1,
	Tag2,
	TagValue2,
	Tag3,
	TagValue3;

	public static String[] getParamFileHeaders() {

		return new String[] {
				Name.name(),
				Value.name(),
				Secure.name(),
				Description.name(),
				Tag1.name(),
				TagValue1.name(),
				Tag2.name(),
				TagValue2.name(),
				Tag3.name(),
				TagValue3.name()
		};
	}

}