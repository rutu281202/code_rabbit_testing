package com.contexio.platformXPlus.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.contexio.platformXPlus.dto.BrandDTO;
import com.contexio.platformXPlus.dto.ChannelDTO;
import com.contexio.platformXPlus.dto.ProductDataDTO;
import com.contexio.platformXPlus.service.DAMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@Controller
public class DAMController {

    @Autowired
	private DAMService damService;

    @GetMapping("/damHomePage")
    public String damHomePage(){
        return "product/product_details";
    }
    @GetMapping("/fetchBrand")
    @ResponseBody
    public List<BrandDTO> fetchBrand(){
        List<BrandDTO> brands = damService.getBrands();
         for (BrandDTO brand : brands) {
        System.out.println("Client ID: " + brand.getId() + ", SubBrand ID: " + brand.getSubBrandId());
        }
        return brands;
    }

    @GetMapping("/fetchParentCategory")
    @ResponseBody
    public List<BrandDTO> fetchParentCategory(@RequestParam("brandId") String brandId){
        List<BrandDTO> parentCategories = damService.getParentCategory(brandId);
        return parentCategories;
    }

    @GetMapping("/fetchProductCategory")
    @ResponseBody
    public List<BrandDTO> fetchProductCategory(@RequestParam("brandId") String brandId,@RequestParam("parentCategoryId") String parentCategoryId){
        List<BrandDTO> productCategories = damService.getProductCategory(brandId,parentCategoryId);
        return productCategories;
    }
    //Updated by Rutuja
    @PostMapping("/fetchProductData")
    @ResponseBody
    public Map<String, Object> fetchProductData(@RequestBody Map<String, Object> requestMap) {
        int draw = (int) requestMap.getOrDefault("draw", 1);
        int start = (int) requestMap.getOrDefault("start", 0);
        int length = (int) requestMap.getOrDefault("length", 10);
        String searchValue = (String) requestMap.getOrDefault("searchValue", "");
        String parentCategoryId = (String) requestMap.get("parentCategoryId");
        List<String> productCategoryId = (List<String>) requestMap.getOrDefault("productCategoryId", new ArrayList<>());
        
        List<ProductDataDTO> filteredData = damService.getProductData(parentCategoryId, productCategoryId, searchValue, start, length);
        long totalRecords = damService.countTotalProductData(parentCategoryId, productCategoryId);
        long filteredRecords = damService.countFilteredProductData(parentCategoryId, productCategoryId, searchValue);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", totalRecords);
        response.put("recordsFiltered", filteredRecords);
        response.put("data", filteredData);

        return response;
    }

    // @GetMapping("/fetchProductData")
    // @ResponseBody
    // public List<ProductDataDTO> fetchProductData(@RequestParam("parentCategoryId") String parentCategoryId,@RequestParam("productCategoryId") List<String> productCategoryId, @RequestParam(name = "export", required = false, defaultValue = "false") boolean export)
    // {
    //     List<ProductDataDTO> productDataList = damService.getProductData(parentCategoryId,productCategoryId);
    //     return productDataList;
    // }
    
    //Added by Rutuja on 03/07/2025
    @GetMapping("/addAssetDetails")
    public String addAssetDetails(){
        return "product/add_asset_details";
    }

    @GetMapping("/fetchChannel")
    @ResponseBody
    public List<BrandDTO> fetchChannel(@RequestParam("subBrandId") String subBrandId) {
        List<BrandDTO> channelNameId = damService.fetchChannel(subBrandId);
        System.out.println("Check channel data:- " + channelNameId);
        return channelNameId;
    }

    @PostMapping(value = "/saveAssetDetails")
    public ResponseEntity<?> saveAssetDetails(
            @RequestParam("jsonData") String jsonData,
            @RequestParam(value = "productImages", required = false) MultipartFile[] productImages,
            @RequestParam(value = "productVideos", required = false) MultipartFile[] productVideos,
            @RequestParam(value = "infographics", required = false) MultipartFile[] infographics,
            @RequestParam(value = "brochures", required = false) MultipartFile[] brochures,
            @RequestParam(value = "marketingBanners", required = false) MultipartFile[] marketingBanners,
            @RequestParam(value = "tvCommercials", required = false) MultipartFile[] tvCommercials,
            @RequestParam(value = "storeCommercials", required = false) MultipartFile[] storeCommercials,
            @RequestParam(value = "aplusImages", required = false) MultipartFile[] aplusImages,
            @RequestParam(value = "storeDisplayFeatures", required = false) MultipartFile[] storeDisplayFeatures,
            @RequestParam(value = "installationGuides", required = false) MultipartFile[] installationGuides) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonDataMap = objectMapper.readValue(jsonData, Map.class);
            Map<String, MultipartFile[]> fileInputs = new LinkedHashMap<>();
            fileInputs.put("productImages", productImages);
            fileInputs.put("productVideos", productVideos);
            fileInputs.put("infographics", infographics);
            fileInputs.put("brochures", brochures);
            fileInputs.put("marketingBanners", marketingBanners);
            fileInputs.put("tvCommercials", tvCommercials);
            fileInputs.put("storeCommercials", storeCommercials);
            fileInputs.put("aplusImages", aplusImages);
            fileInputs.put("storeDisplayFeatures", storeDisplayFeatures);
            fileInputs.put("installationGuides", installationGuides);
            Map<String, Object> result = damService.saveDraftData(jsonDataMap, fileInputs);
            return ResponseEntity.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error in saveAssetDetails: " + e.getMessage());
            }
    }

    @GetMapping("/fetchAssetDetails")
    @ResponseBody
    public ResponseEntity<?> fetchAssetDetails(
        // @RequestParam("mappingId") String mappingId,
        @RequestParam("parentCategory") String parentCategory,
        @RequestParam("childCategory") String childCategory,
        @RequestParam("channelId") String channelId,
        @RequestParam("productId") String productId) {
         List<ChannelDTO> channelDataList = damService.fetchChannelData(parentCategory, childCategory, channelId, productId);
        if (channelDataList == null || channelDataList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No data found for the given details.");
        }
            return ResponseEntity.ok(channelDataList);
    }

    // @PostMapping("/removeAsset")
    // @ResponseBody
    // public ResponseEntity<?> removeAsset(@RequestBody Map<String, String> payload) {
    //     try {
    //         String section = payload.get("section");
    //         String channelId = payload.get("channelId");
    //         String productId = payload.get("productId");
    //         String filePathJson;
    //         // Dynamically construct filePathJson based on section
    //         if ("specification".equals(section)) {
    //             String key = payload.get("key");
    //             String value = payload.get("value");
    //             if (key == null || value == null || channelId == null || productId == null) {
    //                 return ResponseEntity.badRequest().body("Missing required parameters for specification");
    //             }
    //             filePathJson = new ObjectMapper().writeValueAsString(Map.of("key", key, "value", value));
    //         } else {
    //             filePathJson = payload.get("filePath");
    //             if (filePathJson == null || channelId == null || productId == null) {
    //                 return ResponseEntity.badRequest().body("Missing required parameters");
    //             }
    //         }
    //         boolean removed = damService.removeAssetMetadata(filePathJson, section, channelId, productId);
    //         if (removed) {
    //             return ResponseEntity.ok(Map.of("message", "Asset removed successfully"));
    //         } else {
    //             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Asset not found"));
    //         }
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error removing asset"));
    //     }
    // }

    @PostMapping("/removeAsset")
        @ResponseBody
        public ResponseEntity<?> removeAsset(@RequestBody Map<String, String> data) {
            return damService.removeAsset(data);
        }
}