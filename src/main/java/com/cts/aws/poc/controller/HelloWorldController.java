package com.cts.aws.poc.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Basic Spring MVC controller that handles all GET requests.
 */
@Controller
@RequestMapping("/hello")
public class HelloWorldController {

	/**
     * Retrieved from properties file.
     */
    @Value("${HelloWorld.SiteName}")
    private String siteName;

/*    public HelloWorldController(final String siteName) {
        this.siteName = siteName;
    }*/

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView helloWorld() {
    	
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("siteName", this.siteName);
        
        return mav;
    }
}