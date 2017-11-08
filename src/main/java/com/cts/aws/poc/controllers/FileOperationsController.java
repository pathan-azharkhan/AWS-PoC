/**
 * 
 */
package com.cts.aws.poc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@Controller
public class FileOperationsController {
	
	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/upload")
	public String receivePaymentFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		
		fileStorageService.store(file);
		
		redirectAttributes.addFlashAttribute("message", "Successfully uploaded " + file.getOriginalFilename() + "!");
		
		return "redirect:/file";
	}
	
	@GetMapping("/buckets")
	public List<String> listBuckets() {
		
		return fileStorageService.listBuckets();
	}
	
	@GetMapping("/file")
    public ModelAndView uploadScreen() {
    	
        return new ModelAndView("file");
    }
}