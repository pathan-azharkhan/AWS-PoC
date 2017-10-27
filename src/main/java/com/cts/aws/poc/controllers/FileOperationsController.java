/**
 * 
 */
package com.cts.aws.poc.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@RestController
public class FileOperationsController {
	
//	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/file")
	public String receivePaymentFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		
//		fileStorageService.store(file);
		
		redirectAttributes.addFlashAttribute("message", "Successfully uploaded " + file.getOriginalFilename() + "!");
		
		return "redirect:/file";
	}
}