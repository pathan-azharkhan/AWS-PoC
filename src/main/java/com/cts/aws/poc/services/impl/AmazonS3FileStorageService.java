/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
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
	private AmazonS3 s3Client;
	
	private TransferManager transferManager = TransferManagerBuilder.defaultTransferManager();
	
	@Override
	public void store(MultipartFile file) {
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		
		try {
			byte[] md5 = DigestUtils.md5(file.getBytes());
			
			String md5Base64 = new String(Base64Utils.encode(md5));
			objectMetadata.setContentMD5(md5Base64);
			
			transferManager.upload(new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), objectMetadata));
			
		} catch (AmazonClientException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> listBuckets() {
		
		return s3Client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
	}

	@Override
	public File retrieve(String fileName) {
		
		File downloadedFile = new File(fileName);
		
		s3Client.getObject(new GetObjectRequest(bucketName, fileName), downloadedFile);
		
		return downloadedFile;
	}
}