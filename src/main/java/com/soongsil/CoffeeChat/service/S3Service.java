package com.soongsil.CoffeeChat.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {
	private final AmazonS3 amazonS3;
	private final UserService userService;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String saveFile(String directory, String username, MultipartFile multipartFile) throws IOException {
		//Long userId = getUserId(email);
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		//String originalFilename = directory + "/" + username + "/" + timeStamp + "_" + multipartFile.getOriginalFilename();
		String originalFilename = directory + "/" + username;

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);
		return amazonS3.getUrl(bucket, originalFilename).toString();
	}

	public String updateFile(String url, MultipartFile multipartFile) throws IOException {
		if (amazonS3.doesObjectExist(bucket, url)) {
			amazonS3.deleteObject(new DeleteObjectRequest(bucket, url));
		}
		amazonS3.putObject(new PutObjectRequest(bucket, url, multipartFile.getInputStream(), null));
		return amazonS3.getUrl(bucket, url).toString();
	}

	public ResponseEntity<UrlResource> downloadImage(String originalFilename) {
		UrlResource urlResource = new UrlResource(amazonS3.getUrl(bucket, originalFilename));
		String contentDisposition = "attachment; filename=\"" + originalFilename + "\"";
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
			.body(urlResource);
	}

	public void deleteFrameImage(String username, String url) {
		String originalFilename = extractFilenameFromS3Url(url);
		amazonS3.deleteObject(new DeleteObjectRequest(bucket, "frame/" + username + "/" + originalFilename));
	}

	private static String extractFilenameFromS3Url(String s3Url) {
		try {
			URL url = new URL(s3Url);
			String path = url.getPath();
			String[] pathComponents = path.split("/");
			return java.net.URLDecoder.decode(pathComponents[pathComponents.length - 1], StandardCharsets.UTF_8.name());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid S3 URL", e);
		}
	}

	public List<String> findImageUrlsByUserId(String username, String directory) {
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
			.withBucketName(bucket)
			.withPrefix(directory + "/" + username + "/");

		ObjectListing objectListing = amazonS3.listObjects(listObjectsRequest);
		List<String> imageUrls = new ArrayList<>();
		while (objectListing.isTruncated()) {
			imageUrls.addAll(objectListing.getObjectSummaries().stream()
				.map(summary -> amazonS3.getUrl(bucket, summary.getKey()).toString())
				.toList());
			listObjectsRequest.setMarker(objectListing.getNextMarker());
			objectListing = amazonS3.listObjects(listObjectsRequest);
		}
		return imageUrls;
	}

	public String saveBase64Image(String directory, Long username, String base64Image) {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String fileName = directory + "/" + username + "/" + timeStamp + ".jpg";

		try {
			byte[] decodedBytes = Base64.decodeBase64(base64Image);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(decodedBytes.length);
			metadata.setContentType("image/png");

			amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));
			return amazonS3.getUrl(bucket, fileName).toString();
		} catch (AmazonServiceException e) {
			throw new RuntimeException("S3 upload error", e);
		}
	}
}
