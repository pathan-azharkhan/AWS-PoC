/**
 * 
 */
package com.aws.codestar.projecttemplates.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Azharkhan
 *
 */
@RestController
public class SampleController {
	
	@Value("${RDS_DB_NAME:N/A}")
	private String dbName;
	
	@Value("${RDS_DB_NAME:N/A}")
	private String dbUserName;
	
	@Value("${RDS_DB_NAME:N/A}")
	private String dbHostname;

	@RequestMapping(value = "/rds", method = RequestMethod.GET)
	public String getRDSDetails() {
		
		return new StringBuilder().append(dbName).append("|").append(dbUserName).append("|").append(dbHostname).toString();
	}
}