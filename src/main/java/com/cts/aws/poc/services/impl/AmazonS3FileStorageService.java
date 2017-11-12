/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3FileStorageService implements FileStorageService {
	
	@Value("${amazon.s3.inbound.bucket.name}")
	private String inboundBucketName;
	
	@Value("${amazon.s3.outbound.bucket.name}")
	private String outboundBucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	private TransferManager transferManager = TransferManagerBuilder.defaultTransferManager();
	
	@Override
	public void store(MultipartFile file) {
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(file.getOriginalFilename().endsWith(".xml") ? MediaType.APPLICATION_XML_VALUE : file.getContentType());
		objectMetadata.setContentLength(file.getSize());
		
		try {
			byte[] md5 = DigestUtils.md5(file.getBytes());
			
			String md5Base64 = new String(Base64Utils.encode(md5));
			objectMetadata.setContentMD5(md5Base64);
			
			transferManager.upload(new PutObjectRequest(inboundBucketName, file.getOriginalFilename(), file.getInputStream(), objectMetadata));
			
		} catch (AmazonClientException | IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public DeferredResult<Boolean> store(String fileContents) {
		
		DeferredResult<Boolean> deferredResult = new DeferredResult<>();
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.TEXT_PLAIN_VALUE);
		objectMetadata.setContentLength(fileContents.length());
		
		try {
			byte[] md5 = DigestUtils.md5(fileContents.getBytes());
			
			String md5Base64 = new String(Base64Utils.encode(md5));
			objectMetadata.setContentMD5(md5Base64);
			
			String fileName = new StringBuilder(LocalDate.now().toString()).append(".").append(System.currentTimeMillis()).append(".txt").toString();
			
			Upload uploadTracker = transferManager.upload(new PutObjectRequest(outboundBucketName, fileName, new ByteArrayInputStream(fileContents.getBytes()), objectMetadata));
			
			uploadTracker.addProgressListener(new ProgressListener() {
				
				@Override
				public void progressChanged(ProgressEvent progressEvent) {

					if (progressEvent.getEventType() == ProgressEventType.TRANSFER_COMPLETED_EVENT)
						deferredResult.setResult(true);
					else if (progressEvent.getEventType() == ProgressEventType.CLIENT_REQUEST_FAILED_EVENT || progressEvent.getEventType() == ProgressEventType.TRANSFER_FAILED_EVENT)
						deferredResult.setResult(false);
				}
			});
		} catch (AmazonClientException e) {
			deferredResult.setErrorResult(e);
		}
		return deferredResult;
	}
	
	@Override
	public List<String> listBuckets() {
		
		return s3Client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
	}

	@Override
	public File retrieve(String fileName) {
		
		File downloadedFile = new File(fileName);
		
		s3Client.getObject(new GetObjectRequest(inboundBucketName, fileName), downloadedFile);
		
		System.out.println("Downloaded file size: " + downloadedFile.length());
		
		return downloadedFile;
	}
}