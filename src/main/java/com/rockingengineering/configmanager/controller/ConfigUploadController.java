/**
 * 
 */
package com.rockingengineering.configmanager.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rockingengineering.configmanager.dto.ConfigEnvironment;
import com.rockingengineering.configmanager.paramstore.ParameterStorePropertyManager;

/**
 * @author naveen
 *
 * @date 14-Jun-2019
 */
@RestController
@RequestMapping("/upload")
public class ConfigUploadController {

	@Autowired
	private ParameterStorePropertyManager parameterStorePropertyManager;

	@PostMapping("file")
	public String uploadConfigFile(
			@RequestParam(name = "configFile", required = true) @NotNull MultipartFile multipartFile,
			@RequestParam(name = "configEnv", required = true) @NotNull ConfigEnvironment configEnvironment) {

		return parameterStorePropertyManager.uploadParameters(multipartFile, configEnvironment);

	}
}