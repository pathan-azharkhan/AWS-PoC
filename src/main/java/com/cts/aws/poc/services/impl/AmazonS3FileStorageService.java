/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3FileStorageService implements FileStorageService {
	
	@Value("${amazon.s3.bucket.name}")
	private String bucketName;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	private AmazonS3 s3Client;

	/*public void store(MultipartFile file) {
		
		WritableResource resource = (WritableResource) resourceLoader.getResource("s3://" + bucketName + "/" + file.getOriginalFilename());
		
		try {
			OutputStream outputStream = resource.getOutputStream();
			
			outputStream.write(file.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Successfully uploaded file to S3 bucket: " + bucketName);
	}*/
	
	@Override
	public void store(MultipartFile file) {
		
		File newFile = new File("/usr/share/tomcat8/work/Catalina/localhost/ROOT/newFile.txt");
		try {
			newFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		s3Client.putObject(bucketName, "key", newFile);
	}
	
	@Override
	public List<String> listBuckets() {
		
		return s3Client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
	}

	public File retrieve(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}
}