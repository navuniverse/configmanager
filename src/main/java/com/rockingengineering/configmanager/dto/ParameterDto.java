/**
 * 
 */
package com.rockingengineering.configmanager.dto;

import java.util.Collection;

import com.amazonaws.services.simplesystemsmanagement.model.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author naveen
 *
 * @date 14-Jun-2019
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDto {

	private String key;

	private String value;

	private boolean secure;

	private String description;

	private Collection<Tag> tags;
}