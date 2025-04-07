package com.example.demo.controller;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.ImageProcessingService;

@RestController
@RequestMapping("ocr/image")
public class ImageController {

	@Autowired
	private ImageProcessingService imageProcessingService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadImage(@RequestParam("uploadedImage") MultipartFile uploadedImage) {
		try {
			String extractedText = imageProcessingService.extractText(uploadedImage).get(); // get call resolves the completableFuture, waits for async task to complete and returns result
			return ResponseEntity.ok("Text extracted and saved: " + extractedText); // Simple success message
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error processing image: " + e.getMessage());
																										
		}
	}

	@GetMapping("/download")
	public ResponseEntity<FileSystemResource> downloadText(@RequestParam("fileName") String fileName) {
		try {
			File textFile = new File("E:/workspace/ConvertorOCRText/output/" + fileName);
			if (!textFile.exists()) {
				throw new Exception("File not found");
			}
			return ResponseEntity.ok()
					.header("Content-Disposition", "attachment ; fileName=\"" + textFile.getName() + "\"")//instructs the browser the file can be downloaded
					.body(new FileSystemResource(textFile));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}

	}
}
