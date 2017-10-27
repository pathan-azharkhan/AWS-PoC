/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3FileStorageService implements FileStorageService {
	
	@Autowired
	private ResourceLoader resourceLoader;

	public void store(MultipartFile file) {

		WritableResource resource = (WritableResource) resourceLoader.getResource("s3://bucket/test.txt");
		
		try {
			OutputStream outputStream = resource.getOutputStream();
			
			outputStream.write(file.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File retrieve(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
}