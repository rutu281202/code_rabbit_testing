package com.contexio.dam.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.contexio.dam.dto.AssetsDTO;

@Repository
public interface AssetsRepository {
	
	AssetsDTO saveAsset(AssetsDTO asset);

	List<Map<String, String>> fetchAssetPlatforms();

	List<Map<String, String>> fetchAssetTypes();

	List<Map<String, String>> fetchAssetSubtypes(String assetTypeId);

	void saveAssetData(Map<String, String> formData);

}
