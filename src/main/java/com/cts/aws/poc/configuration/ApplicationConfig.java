package com.cts.aws.poc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.jdbc.config.annotation.EnableRdsInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring configuration for sample application.
 */
@Configuration
@ComponentScan(basePackageClasses = { ApplicationConfig.class })
@PropertySource("classpath:application.properties")
@EnableRdsInstance(dbInstanceIdentifier = "sampledb", password = "${RDS_PASSWORD}")
public class ApplicationConfig {

	@Bean
	@Autowired
	public JdbcTemplate buildJdbcTemplate(DataSource dataSource) {
		
		return new JdbcTemplate(dataSource);
	}

    /*@Bean
    public HelloWorldController helloWorld() {
        return new HelloWorldController(this.siteName);
    }*/

    /**
     * Required to inject properties using the 'Value' annotation.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}