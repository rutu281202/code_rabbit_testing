package com.contexio.dam.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.dto.AssetsDTO;
import com.contexio.dam.service.AssetsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class AssetController {
// Added a new feature to test release drafter
	private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

	@Value("${file.upload.path}")
	private String fileUploadPath;

	@Autowired
	private AssetsService assetService;

	/* Added by Yugant */

	@GetMapping("/fetch-asset-platforms")
	public List<Map<String, String>> getAssetPlatforms() {
		return assetService.findAssetPlatforms();
	}

	/* Added by Yugant */

	@GetMapping("/fetch-asset-types")
	public ResponseEntity<List<Map<String, String>>> getAssetTypes() {
		List<Map<String, String>> assetTypes = assetService.fetchAssetTypes();
		return ResponseEntity.ok(assetTypes);
	}

	/* Added by Yugant */
	@GetMapping("/fetch-asset-subtypes")
	public ResponseEntity<List<Map<String, String>>> getAssetSubtypes(
			@RequestParam(name = "assetTypeId") String assetTypeId) {
		if (assetTypeId == null || assetTypeId.trim().isEmpty()) {
			return ResponseEntity.badRequest().body(null);
		}
		List<Map<String, String>> subtypes = assetService.fetchAssetSubtypes(assetTypeId);
		return ResponseEntity.ok(subtypes);
	}

	/* Added by Yugant */
	@PostMapping("/upload-assets")
    public ResponseEntity<Map<String, Object>> uploadAssets(
            @RequestParam("assets") MultipartFile[] files,
            @RequestParam Map<String, String> formData) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Check if files are empty
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "No files uploaded."));
            }

            // Log received form data
            System.out.println("Received form data: " + formData);

            // Define upload directory
            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs(); // Create directory if it doesn't exist
            }

            // Process each file
            List<String> uploadedFileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
                    Files.write(filePath, file.getBytes());
                    uploadedFileNames.add(file.getOriginalFilename());
                }
            }

            // Call assetService for further processing
            String serviceResponse = assetService.uploadAssets(files, formData);

            response.put("message", serviceResponse);
            response.put("uploadedFiles", uploadedFileNames);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

}
