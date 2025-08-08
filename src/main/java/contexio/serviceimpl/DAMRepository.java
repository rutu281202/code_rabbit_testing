package com.contexio.platformXPlus.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.contexio.platformXPlus.dto.BrandDTO;
import com.contexio.platformXPlus.dto.ChannelDTO;
import com.contexio.platformXPlus.dto.ProductDataDTO;

@Repository
public interface DAMRepository {
    
    public List<BrandDTO> getBrands();

    public List<BrandDTO> fetchParentCategoryId(String brandId);

    public List<BrandDTO> fetchProductCategoryId(String brandId,String parent_category_id);

    public List<ProductDataDTO> fetchProductData(String parentCategoryId, List<String> childCategoryIds, String searchValue, int start, int length);

    public long countTotalProductData(String parentCategoryId, List<String> childCategoryIds);

    public long countFilteredProductData(String parentCategoryId, List<String> childCategoryIds, String searchValue);

    //public List<ProductDataDTO> fetchProductData(String parentCategoryId, List<String> productCategoryId);
   
    // Added by Rutuja on 03/07/2025 to implement channel fetching functionality
    public List<BrandDTO> fetchChannel(String subBrandId);

    public void saveDraftData(String parentCategory, String childCategory, String channelId, String productId, Map<String, Object> draftData);

    public List<ChannelDTO> fetchChannelData(String parentCategory, String childCategory, String channelId, String productId);

    //public boolean deleteAssetMetadata(String filePath, String section, String channelId, String productId);

    public boolean deleteAssetMetadata(String filePathJson, String section, String channelId, String productId);

    

   
    
}
