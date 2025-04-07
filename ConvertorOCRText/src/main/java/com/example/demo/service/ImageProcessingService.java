package com.example.demo.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;

@Service
public class ImageProcessingService {

	@Async  //async annotated method runs in separate thread, ensure the slow operation does'nt block other parts
	public CompletableFuture<String> extractText(MultipartFile uploadedImage) throws Exception {  //images uploaded to spring application always as Multifilepart
		File localImageFile = convertMultipartToFile(uploadedImage);// OCR doesn't understand MultipartFile, so we convert it to java file
		Tesseract ocrTool = new Tesseract();
		ocrTool.setDatapath("E://tessdata/");
		String extractedText = ocrTool.doOCR(localImageFile);
		String textFileName = saveTextToFile(extractedText, uploadedImage);

		 return CompletableFuture.completedFuture(textFileName);

	}

	private File convertMultipartToFile(MultipartFile uploadedImage) throws FileNotFoundException, IOException {
						         //creating file object with same name as uploaded filename
		File localFile = new File(uploadedImage.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(localFile)) {
			fos.write(uploadedImage.getBytes());//retrieves the binary data of the image and writes into localfile
		}
		return localFile;
	}
	
	private String saveTextToFile(String textContent, MultipartFile uploadedImage) throws IOException {
		String originalFileName = uploadedImage.getOriginalFilename();
		 String baseName = originalFileName != null ? originalFileName.substring(0, originalFileName.lastIndexOf(".")) : "extracted";

	        // Define the output file name and path
	        String outputFileName = baseName + ".txt";
	        File outputTextFile = new File("E:/workspace/ConvertorOCRText/output/" + outputFileName);

	        // Ensure the output directory exists
	        outputTextFile.getParentFile().mkdirs();

	        // Write the extracted text to the file
	        try (FileOutputStream fos = new FileOutputStream(outputTextFile)) {
	            fos.write(textContent.getBytes());
	            System.out.println("Extracted text file saved successfully: " + outputTextFile.getAbsolutePath());
	        }

	        // Return the name of the output file
	        return outputFileName;
	    }

}
