package com.cts.aws.poc.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring configuration for sample application.
 */
@Configuration
@ComponentScan(basePackages = { "com.cts.aws.poc" })
@EntityScan("com.cts.aws.poc.dao")
@EnableJpaRepositories("com.cts.aws.poc.dao")
@PropertySource("classpath:application.properties")
@EnableContextResourceLoader
@EnableAutoConfiguration(exclude = { ContextStackAutoConfiguration.class })
//@EnableRdsInstance(dbInstanceIdentifier = "sampledb", password = "${RDS_PASSWORD}")
public class ApplicationConfig {

	@Bean
	@Autowired
	public JdbcTemplate buildJdbcTemplate(DataSource dataSource) {
		
		return new JdbcTemplate(dataSource);
	}

    /**
     * Required to inject properties using the 'Value' annotation.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}