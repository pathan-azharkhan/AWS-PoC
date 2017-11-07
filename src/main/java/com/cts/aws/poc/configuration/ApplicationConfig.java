package com.cts.aws.poc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Spring configuration for sample application.
 */
@Configuration
@ComponentScan(basePackages = { "com.cts.aws.poc" })
@PropertySource("classpath:application.properties")
@EnableContextResourceLoader
//@EnableRdsInstance(dbInstanceIdentifier = "sampledb", password = "${RDS_PASSWORD}")
public class ApplicationConfig {
	
	@Bean
	public DataSource buildDataSource() {
		
		String url = "jdbc:mysql://sampledb.cfzqvfpahiok.us-east-2.rds.amazonaws.com:3306/ebdb";
		String username = "sample";
		String password = "password";
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource(url, username, password);
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		return dataSource;
	}

	@Bean
	@Autowired
	public JdbcTemplate buildJdbcTemplate(/*DataSource dataSource*/) {
		
		return new JdbcTemplate(buildDataSource());
	}
	
	/*@Bean
	@Autowired
	public static ResourceLoaderBeanPostProcessor resourceLoaderBeanPostProcessor(AmazonS3Client amazonS3EncryptionClient) {
		
		return new ResourceLoaderBeanPostProcessor(amazonS3EncryptionClient);
	}*/

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