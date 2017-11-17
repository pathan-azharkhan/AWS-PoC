/**
 * 
 */
package com.cts.aws.poc.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cts.aws.poc.services.FileStorageService;
import com.cts.aws.poc.services.FlowOrchestrator;
import com.cts.aws.poc.services.PaymentDetailsPersistenceService;
import com.cts.aws.poc.utils.DateUtils;

/**
 * @author Azharkhan
 *
 */
@Controller
public class UIController {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private FlowOrchestrator flowOrchestrator;
	
	@Autowired
	@Qualifier("paymentDetailsJdbcService")
	private PaymentDetailsPersistenceService paymentService;
	
	@PostMapping("/upload")
	public String receivePaymentFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		
		fileStorageService.store(file);
		
		redirectAttributes.addFlashAttribute("message", "Successfully uploaded " + file.getOriginalFilename() + "!");
		
		return "redirect:/file";
	}
	
	@GetMapping("/buckets")
	public @ResponseBody List<String> listBuckets() {
		
		return fileStorageService.listBuckets();
	}
	
	@GetMapping("/file")
    public String uploadScreen() {
    	
        return "file";
    }
	
	@GetMapping("/test")
	public @ResponseBody String testFlowWithFile() {
		
		flowOrchestrator.process("pain.001.001.03.xml");
		return "Ok";
	}
	
	@GetMapping("/dashboard-data")
	public @ResponseBody Map<String, Map<String, Integer>> getDashboardData(@RequestParam("selectedDate") String selectedDate) {
		
		try {
			return paymentService.getDashboardData(DateUtils.MYSQL_DATE_FORMAT.parse(selectedDate));
		} catch (ParseException e) {
			
			e.printStackTrace();
			return null;
		}
	}
}