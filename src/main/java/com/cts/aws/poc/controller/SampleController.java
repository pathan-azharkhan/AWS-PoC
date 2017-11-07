/**
 * 
 */
package com.cts.aws.poc.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Azharkhan
 *
 */
@Controller
public class SampleController {
	
	@Value("${RDS_DB_NAME:N/A}")
	private String dbName;
	
	@Value("${RDS_USERNAME:N/A}")
	private String dbUserName;
	
	@Value("${RDS_PASSWORD:N/A}")
	private String dbHostname;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value = "/rds", method = RequestMethod.GET)
	public @ResponseBody String getRDSDetails() {
		
		return new StringBuilder().append(dbName).append("|").append(dbUserName).append("|").append(dbHostname).toString();
	}
	
	@RequestMapping(value = "/db", method = RequestMethod.GET)
	public @ResponseBody String getDatabases() {
		
		List<String> list = jdbcTemplate.query("show databases", new RowMapper<String>() {

			public String mapRow(ResultSet arg0, int arg1) throws SQLException {
				
				return arg0.getString(1);
			}
		});
		
		return CollectionUtils.isEmpty(list) ? "No data" : list.get(0);
	}
	
	@GetMapping("/rds/db")
    public ModelAndView uploadScreen() {
    	
        return new ModelAndView("db");
    }
}