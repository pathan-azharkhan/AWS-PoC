/**
 * 
 */
package com.cts.aws.poc.services;

import java.io.File;
import java.util.List;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Azharkhan
 *
 */
public interface FileStorageService {

	void store(MultipartFile file);
	
	DeferredResult<Boolean> store(String fileContents);
	
	File retrieve(String fileName);
	
	List<String> listBuckets();
}