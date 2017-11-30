/**
 * 
 */
package com.cts.aws.poc.services.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.cts.aws.poc.exceptions.SystemException;
import com.cts.aws.poc.services.FileStorageService;

/**
 * @author Azharkhan
 *
 */
@Component
public class AmazonS3FileStorageService implements FileStorageService {
	
	private static final Logger LOGGER = LogManager.getLogger(AmazonS3FileStorageService.class);
	
	@Value("${amazon.s3.inbound.bucket.name}")
	private String inboundBucketName;
	
	@Value("${amazon.s3.outbound.bucket.name}")
	private String outboundBucketName;
	
	private TransferManager transferManager;
	
	private File downloadDirectory;
	
	@Autowired
	public AmazonS3FileStorageService(AmazonS3 s3Client, @Value("${java.io.tmpdir}") String stagingDirectory) {
		
		transferManager = TransferManagerBuilder.standard().withS3Client(s3Client).build();
		downloadDirectory = new File(stagingDirectory.concat("/payments/downloads/"));
		
		if (!downloadDirectory.exists())
			downloadDirectory.mkdirs();
		
		LOGGER.debug("Using directory {} for staging downloaded files", downloadDirectory.getPath());
	}
	
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
			
			LOGGER.info("Uploaded file {} to S3 bucket '{}'", file.getOriginalFilename(), inboundBucketName);
			
		} catch (AmazonClientException e) {
			
			LOGGER.error("Failed to upload file {} to S3. Reason: {}", file.getOriginalFilename(), e);
			
		} catch (IOException e) {
			
			LOGGER.error("Failed to read file {}. Reason: {}", file.getOriginalFilename(), e);
		}
	}
	
	@Override
	public DeferredResult<Boolean> store(String fileName, String fileContents) {
		
		DeferredResult<Boolean> deferredResult = new DeferredResult<>();
		
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(MediaType.TEXT_PLAIN_VALUE);
		objectMetadata.setContentLength(fileContents.length());
		
		try {
			byte[] md5 = DigestUtils.md5(fileContents.getBytes());
			
			String md5Base64 = new String(Base64Utils.encode(md5));
			objectMetadata.setContentMD5(md5Base64);
			
			Upload uploadTracker = transferManager.upload(new PutObjectRequest(outboundBucketName, fileName, new ByteArrayInputStream(fileContents.getBytes()), objectMetadata));
			LOGGER.info("Initiated upload of file {} to S3 bucket '{}'", fileName, outboundBucketName);
			
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
			
			LOGGER.error("Failed to upload file {} to S3 bucket '{}'. Reason: {}", fileName, outboundBucketName, e);
			deferredResult.setErrorResult(e);
		}
		return deferredResult;
	}
	
	@Override
	public List<String> listBuckets() {
		
		return transferManager.getAmazonS3Client().listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
	}

	@Override
	public File retrieve(String fileName) {
		
		File downloadedFile = new File(downloadDirectory, fileName);
		
		try {
			transferManager.download(new GetObjectRequest(inboundBucketName, fileName), downloadedFile).waitForCompletion();
		} catch (AmazonClientException | InterruptedException e) {
			
			LOGGER.error("Failed to download file {} from S3 bucket '{}'. Reason: {}", fileName, inboundBucketName, e);
			throw new SystemException("Failed to download file from S3 bucket!", e);
		}
		
		LOGGER.info("Downloaded file {} from S3 bucket '{}'", fileName, inboundBucketName);
		LOGGER.debug("Downloaded file size: {} bytes", downloadedFile.length());
		
		return downloadedFile;
	}
}