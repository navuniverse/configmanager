/**
 * 
 */
package com.rockingengineering.configmanager.controller;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rockingengineering.configmanager.dto.ConfigEnvironment;
import com.rockingengineering.configmanager.dto.ParameterDto;
import com.rockingengineering.configmanager.paramstore.SearchPropertyService;

/**
 * @author naveen
 *
 * @date 25-Jun-2019
 */
@RestController
@RequestMapping("/search")
public class ConfigSearchController {

	@Autowired
	private SearchPropertyService searchPropertyService;

	@GetMapping("property")
	public ResponseEntity<ParameterDto> getPropertyByName(
			@RequestParam(name = "name", required = true) @NotBlank(message = "Property Name is Mandatory") String name,
			@RequestParam(name = "configEnv", required = true) @NotNull ConfigEnvironment configEnvironment) {

		System.out.println("Requesting Value for Property: " + name + " from Environment: " + configEnvironment);

		ParameterDto parameterDto = searchPropertyService.getPropertyByName(name, configEnvironment);

		System.out.println("PropertyName: " + name + ", RetrievedValue: " + parameterDto);

		if (Objects.nonNull(parameterDto)) {
			return new ResponseEntity<>(parameterDto, HttpStatus.OK);
		}

		return ResponseEntity.notFound().build();

	}

	@GetMapping("property/prefix")
	public ResponseEntity<List<ParameterDto>> getPropertyByPrefix(
			@RequestParam(name = "prefix", required = true) @NotBlank(message = "Prefix is Mandatory") String prefix,
			@RequestParam(name = "configEnv", required = true) @NotNull ConfigEnvironment configEnvironment) {

		System.out.println("Requesting Properties for Prefix: " + prefix + " from Environment: " + configEnvironment);

		List<ParameterDto> parameterDtos = searchPropertyService.getPropertiesForPrefix(prefix, configEnvironment);

		System.out.println("Found " + parameterDtos.size() + " Properties for Prefix: " + prefix);

		if (!CollectionUtils.isEmpty(parameterDtos)) {
			return new ResponseEntity<>(parameterDtos, HttpStatus.OK);
		}

		return ResponseEntity.notFound().build();
	}
}