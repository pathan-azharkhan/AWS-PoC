package com.cts.aws.poc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring configuration for sample application.
 */
@Configuration
//@EnableContextResourceLoader
public class ApplicationConfig {

	@Bean
	@Autowired
	public JdbcTemplate buildJdbcTemplate(DataSource dataSource) {
		
		return new JdbcTemplate(dataSource);
	}
	
	/*@Bean
	@Autowired
	public static ResourceLoaderBeanPostProcessor resourceLoaderBeanPostProcessor(AmazonS3Client amazonS3EncryptionClient) {
		
		return new ResourceLoaderBeanPostProcessor(amazonS3EncryptionClient);
	}*/

    /**
     * Required to inject properties using the 'Value' annotation.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}