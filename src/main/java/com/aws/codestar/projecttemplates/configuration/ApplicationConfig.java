package com.aws.codestar.projecttemplates.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.aws.codestar.projecttemplates.controller.SampleController;

/**
 * Spring configuration for sample application.
 */
@Configuration
@ComponentScan(basePackages = { "com.aws.codestar.projecttemplates.configuration" }, basePackageClasses = { SampleController.class })
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

    

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