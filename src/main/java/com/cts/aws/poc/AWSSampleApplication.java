package com.cts.aws.poc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
@EntityScan("com.cts.aws.poc.dao")
@EnableJpaRepositories("com.cts.aws.poc.dao")
@EnableContextResourceLoader
@EnableAutoConfiguration(exclude = { ContextStackAutoConfiguration.class })
public class AWSSampleApplication {
	
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

	public static void main(String[] args) {
		SpringApplication.run(AWSSampleApplication.class, args);
	}
}