package com.contexio.dam.serviceimpl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.contexio.dam.dto.AssetsDTO;
import com.contexio.dam.repository.AssetsRepository;
import com.contexio.dam.service.AssetsService;

@Service
public class AssetServiceImpl implements AssetsService {
	
	private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    @Autowired
    private AssetsRepository assetRepository;
    
    @Override
    public AssetsDTO submitAsset(AssetsDTO asset) {
        logger.info("Submitting asset: {}", asset.getAssetName());
        return assetRepository.saveAsset(asset);
    }

	@Override
	public List<Map<String, String>> findAssetPlatforms() {
		return assetRepository.fetchAssetPlatforms();
	}
	
	@Override
	public List<Map<String, String>> fetchAssetTypes() {
        return assetRepository.fetchAssetTypes();
    }
	
	@Override
	public List<Map<String, String>> fetchAssetSubtypes(String assetTypeId) {
        return assetRepository.fetchAssetSubtypes(assetTypeId);
    }
	
	@Override
    public String uploadAssets(MultipartFile[] files, Map<String, String> formData) throws Exception {
        // Validate file types
        for (MultipartFile file : files) {
            if (!isValidFileType(file)) {
                throw new Exception("Invalid file type: " + file.getOriginalFilename());
            }
        }

     // Save files and generate thumbnail
        String uploadPath = "uploads/";  // Directory to save files
        String thumbnailPath = "uploads/thumbnails/"; // Directory for thumbnails
        
        for (MultipartFile file : files) {
            // Save original file
            Path filePath = Path.of(uploadPath + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
         // Calculate file size in KB or MB
            long fileSizeInBytes = file.getSize();
            String fileSizeFormatted = formatFileSize(fileSizeInBytes);
            
            // Generate thumbnail (if image)
            String thumbPath = null;
            if (file.getContentType() != null && file.getContentType().startsWith("image")) {
                thumbPath = thumbnailPath + "thumb_" + file.getOriginalFilename();
                generateThumbnail(filePath.toString(), thumbPath);  // You need to implement this method
                
                
                File thumbFile = new File(thumbPath);
                if (!thumbFile.exists()) {
                    logger.error("Thumbnail generation failed: " + thumbPath);
                    thumbPath = ""; // Set empty if thumbnail is not created
                }
            }

         // Store file metadata in formData
            formData.put("filePath", filePath.toString());
            formData.put("fileSize", fileSizeFormatted); // ✅ Store formatted file size
            formData.put("fileType", file.getContentType()); // ✅ Store file type
            formData.put("thumbnailPath", thumbPath);
        }

        // Save metadata to database
        assetRepository.saveAssetData(formData);
        
        logger.info("Final Form Data: " + formData);

        return "Files uploaded successfully!";
    }
	
	private String formatFileSize(long sizeInBytes) {
	    double sizeInKB = sizeInBytes / 1024.0;
	    if (sizeInKB < 1024) {
	        return String.format("%.2f KB", sizeInKB);
	    } else {
	        return String.format("%.2f MB", sizeInKB / 1024.0);
	
	    }
	}
	
	private void generateThumbnail(String imagePath, String thumbPath) throws IOException {
	    File originalFile = new File(imagePath);
	    
	    if (!originalFile.exists()) {
	        logger.error("Original file does not exist: " + imagePath);
	        return; // Exit if file is missing
	    }

	    BufferedImage originalImage = ImageIO.read(originalFile);
	    if (originalImage == null) {
	        logger.error("Failed to read image: " + imagePath);
	        return;
	    }

	    BufferedImage thumbnail = Scalr.resize(originalImage, 150); // Resize to 150px width
	    File thumbFile = new File(thumbPath);

	    // 🔹 Ensure thumbnail directory exists
	    File thumbDir = thumbFile.getParentFile();
	    if (!thumbDir.exists()) {
	        thumbDir.mkdirs();
	    }

	    ImageIO.write(thumbnail, "jpg", thumbFile);
	    
	    logger.info("Thumbnail successfully generated at: " + thumbPath);
	}



    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("image/jpeg") ||
                                       contentType.equals("image/png") ||
                                       contentType.equals("application/pdf") ||
                                       contentType.equals("video/mp4"));
    }
	
	

}
