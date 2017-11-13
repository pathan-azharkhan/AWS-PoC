/**
 * 
 */
package com.cts.aws.poc.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cts.aws.poc.services.FileStorageService;
import com.cts.aws.poc.services.PaymentDetailsPersistenceService;

/**
 * @author Azharkhan
 *
 */
@Controller
public class UIController {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
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
    public ModelAndView uploadScreen() {
    	
        return new ModelAndView("file");
    }
	
	@GetMapping("dashboard-data")
	public @ResponseBody Map<String, Map<String, Integer>> getDashboardData(@RequestParam("selectedDate") Date selectedDate) {
		
		return paymentService.getDashboardData(selectedDate);
	}
}