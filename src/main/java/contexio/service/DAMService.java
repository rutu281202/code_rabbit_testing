package com.contexio.platformXPlus.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.contexio.platformXPlus.dto.BrandDTO;
import com.contexio.platformXPlus.dto.ChannelDTO;
import com.contexio.platformXPlus.dto.ProductDataDTO;

@Service
public interface DAMService {

    public List<BrandDTO> getBrands();

    public List<BrandDTO> getParentCategory(String brandId);

    public List<BrandDTO> getProductCategory(String brandId,String parent_category_id);

    public List<ProductDataDTO> getProductData(String parentCategoryId, List<String> productCategoryId, String searchValue, int start, int length);

    public long countTotalProductData(String parentCategoryId, List<String> productCategoryId);

    public long countFilteredProductData(String parentCategoryId, List<String> productCategoryId, String searchValue);
    // public List<ProductDataDTO> getProductData(String parentCategoryId, List<String> productCategoryId);

    // Added by Rutuja on 03/07/2025 to implement channel fetching functionality
    public List<BrandDTO> fetchChannel(String subBrandId);

    public Map<String, Object> saveDraftData(Map<String, Object> data, Map<String, MultipartFile[]> fileMap);
    
    public List<ChannelDTO> fetchChannelData(String parentCategory, String childCategory, String channelId, String productId);

    // public boolean removeAssetMetadata(String filePath, String section, String channelId, String productId);

    public ResponseEntity<?> removeAsset(Map<String, String> data);
}