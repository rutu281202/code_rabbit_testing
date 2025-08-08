package com.contexio.dam.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.contexio.dam.dto.AssetsDTO;

@Service
public interface AssetsService {
	
	AssetsDTO submitAsset(AssetsDTO asset);
	
	List<Map<String, String>> findAssetPlatforms();
	
	

	List<Map<String, String>> fetchAssetTypes();

	List<Map<String, String>> fetchAssetSubtypes(String assetTypeId);

	String uploadAssets(MultipartFile[] files, Map<String, String> formData) throws Exception;
	
	

}
