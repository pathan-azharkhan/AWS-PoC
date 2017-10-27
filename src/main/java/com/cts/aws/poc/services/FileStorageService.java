/**
 * 
 */
package com.cts.aws.poc.services;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Azharkhan
 *
 */
public interface FileStorageService {

	void store(MultipartFile file);
	
	File retrieve(String fileName);
}