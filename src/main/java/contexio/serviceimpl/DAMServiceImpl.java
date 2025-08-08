package com.contexio.platformXPlus.serviceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.contexio.platformXPlus.dto.BrandDTO;
import com.contexio.platformXPlus.dto.ChannelDTO;
import com.contexio.platformXPlus.dto.ProductDataDTO;
import com.contexio.platformXPlus.repository.DAMRepository;
import com.contexio.platformXPlus.service.DAMService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DAMServiceImpl implements DAMService {
    
    @Autowired
	public DAMRepository damRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public List<BrandDTO> getBrands(){
        System.out.println("getBrands serviceImpl");
        List<BrandDTO> brands = damRepository.getBrands();
        return brands;
    }

    @Override
     public List<BrandDTO> getParentCategory(String brandId){
        List<BrandDTO> parentCategory = damRepository.fetchParentCategoryId(brandId);
        return parentCategory;
    }

    @Override
     public List<BrandDTO> getProductCategory(String brandId, String parent_category_id){
         List<BrandDTO> productCategory =damRepository.fetchProductCategoryId(brandId, parent_category_id);
        return productCategory;
    }
    
    @Override
    public List<ProductDataDTO> getProductData(String parentCategoryId, List<String> productCategoryId, String searchValue, int start, int length) {
    List<ProductDataDTO> productDataList = damRepository.fetchProductData(parentCategoryId, productCategoryId, searchValue, start, length);
    String baseAwsUrl = "https://contexiocatalogueimages.s3.ca-central-1.amazonaws.com/PIM/Images";

    for (ProductDataDTO product : productDataList) {
        Map<String, String> properties = product.getProperties();
        if (properties != null && properties.containsKey("PRODUCT IMAGES")) {
            String imagesStr = properties.get("PRODUCT IMAGES");
            System.out.println("Product Images String: " + imagesStr);

            String batchName = product.getBatchName();
            String mappingId = product.getMappingBrandSubBrandId();

            if (batchName != null && !batchName.isEmpty() &&
                mappingId != null && !mappingId.isEmpty() &&
                imagesStr != null && !imagesStr.isEmpty()) 
            {
                String[] imageNames = imagesStr.split(",");

                String selectedImage = null;
                for (String imgName : imageNames) {
                    imgName = imgName.trim();
                    String nameWithoutExt = imgName.contains(".") 
                        ? imgName.substring(0, imgName.lastIndexOf('.')) 
                        : imgName;

                    if (nameWithoutExt.endsWith("_1")) {
                        selectedImage = imgName;
                        System.out.println("Picked _1 image: " + selectedImage);
                        break;  
                    }
                }
                if (selectedImage != null) {
                    String imageUrl = baseAwsUrl + "/" + mappingId + "/" + batchName + "/" + selectedImage;
                    System.out.println("Generated Image URL: " + imageUrl);
                    product.setImageUrl(imageUrl);
                } else {
                    System.out.println("No image ending with _1 found in PRODUCT IMAGES: " + imagesStr);
                }
            } else {
                System.out.println("Cannot build image URL — some parts are missing.");
            }
        } else {
            System.out.println("Product Image Name: Not Available");
        }
    }

    return productDataList;
}


    @Override
    public long countTotalProductData(String parentCategoryId, List<String> productCategoryId) {
        return damRepository.countTotalProductData(parentCategoryId, productCategoryId);
    }

    @Override
    public long countFilteredProductData(String parentCategoryId, List<String> productCategoryId, String searchValue) {
        return damRepository.countFilteredProductData(parentCategoryId, productCategoryId, searchValue);
    }






    // @Override
    // public List<ProductDataDTO> getProductData(String parentCategoryId, List<String> productCategoryId) {
    // List<ProductDataDTO> productDataList = damRepository.fetchProductData(parentCategoryId, productCategoryId);

    // String baseAwsUrl = "https://contexiocatalogueimages.s3.ca-central-1.amazonaws.com/PIM/Images";

    //     for (ProductDataDTO product : productDataList) {

    //         Map<String, String> properties = product.getProperties();
    //         if (properties != null && properties.containsKey("PRODUCT IMAGES")) {
    //             String imageName = properties.get("PRODUCT IMAGES");

    //             System.out.println("Product Image Name: " + imageName);
    //             String batchName = product.getBatchName();
    //             String mappingId = product.getMappingBrandSubBrandId();
    //             if (batchName != null && !batchName.isEmpty()
    //                     && mappingId != null && !mappingId.isEmpty()
    //                     && imageName != null && !imageName.isEmpty()) {

    //                 String imageUrl = baseAwsUrl + "/" + mappingId + "/" + batchName + "/" + imageName;

    //                 System.out.println("Generated Image URL: " + imageUrl);
    //                 product.setImageUrl(imageUrl);
    //             } else {
    //                 System.out.println("Cannot build image URL — some parts are missing.");
    //             }
    //         } else {
    //             System.out.println("Product Image Name: Not Available");
    //         }
    //     }
    //     return productDataList;
    // }
    
    // Added by Rutuja on 03/07/2025 to implement channel fetching functionality
    @Override
    public List<BrandDTO> fetchChannel(String subBrandId) {
        List<BrandDTO> channel = damRepository.fetchChannel(subBrandId);
        return channel;
    }

    @Override
        public Map<String, Object> saveDraftData(Map<String, Object> data, Map<String, MultipartFile[]> fileMap) {
            String parentCategory = (String) data.get("parentCategory");
            String childCategory = (String) data.get("childCategory");
            String channelId = (String) data.get("channelId");
            String productId = (String) data.get("productId");
            String mappingBrandSubbrandId = (String) data.get("brandSubbrandMappingId");

            System.out.println("In serviceImpl - mappingBrandSubbrandId: " + mappingBrandSubbrandId);
            System.out.println("Received parentCategory: " + parentCategory);
            System.out.println("Received childCategory: " + childCategory);
            System.out.println("Received channelId: " + channelId);
            System.out.println("Received productId: " + productId);

            if (Stream.of(parentCategory, childCategory, channelId, productId, mappingBrandSubbrandId)
                    .anyMatch(Objects::isNull)) {
                throw new IllegalArgumentException("Required field missing.");
            }

            if (fileMap == null || fileMap.isEmpty()) {
                System.out.println("No files received for upload.");
            } else {
                System.out.println("Files received for upload: " + fileMap.keySet());
            }

            // Process each entry with MultipartFile array
            if (fileMap != null && !fileMap.isEmpty()) {
                for (Map.Entry<String, MultipartFile[]> entry : fileMap.entrySet()) {
                    String stepKey = entry.getKey();
                    MultipartFile[] files = entry.getValue();

                    if (files == null || files.length == 0) {
                        System.out.println("No files found under key: " + stepKey);
                        continue;
                    }

                    List<Map<String, Object>> uploadedFiles = new ArrayList<>();

                    for (MultipartFile file : files) {
                        try {
                            if (file == null || file.isEmpty()) {
                                System.out.println("Skipped empty or null file under key: " + stepKey);
                                continue;
                            }

                            String key = "DAM/" + mappingBrandSubbrandId + "/" + parentCategory + "/" + childCategory + "/" +
                                    productId + "/" + channelId + "/" + stepKey + "/" + file.getOriginalFilename();

                            ObjectMetadata metadata = new ObjectMetadata();
                            metadata.setContentLength(file.getSize());
                            metadata.setContentType(file.getContentType());

                            PutObjectRequest putRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead);
                            amazonS3.putObject(putRequest);

                            String fileUrl = amazonS3.getUrl(bucketName, key).toString();
                            System.out.println("Uploaded file: " + file.getOriginalFilename() + " to: " + fileUrl);

                            Map<String, Object> contentObj = new HashMap<>();
                            contentObj.put("filePath", fileUrl);

                            // Merge metadata from JSON by matching filename
                            if (data.containsKey(stepKey) && data.get(stepKey) instanceof List) {
                                List<Map<String, Object>> metadataList = (List<Map<String, Object>>) data.get(stepKey);

                                for (Map<String, Object> meta : metadataList) {
                                    Object filePathObj = meta.get("filePath");
                                    if (filePathObj != null && filePathObj.toString().contains(file.getOriginalFilename())) {
                                        for (Map.Entry<String, Object> entryMeta : meta.entrySet()) {
                                            if (!"filePath".equals(entryMeta.getKey())) {
                                                contentObj.put(entryMeta.getKey(), entryMeta.getValue());
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            uploadedFiles.add(contentObj);

                        } catch (Exception e) {
                            System.out.println("Failed to upload file: " + file.getOriginalFilename());
                            e.printStackTrace();
                        }
                    }
                    if (!uploadedFiles.isEmpty()) {
                        data.put(stepKey, uploadedFiles);
                    }
                }
            }
            // Remove top-level keys before saving
            Map<String, Object> draftData = new HashMap<>(data);
            draftData.keySet().removeAll(Arrays.asList("parentCategory", "childCategory", "channelId", "productId", "brandSubbrandMappingId"));

            System.out.println("Saving draft data (without top-level keys) to DB...");
            damRepository.saveDraftData(parentCategory, childCategory, channelId, productId, draftData);
            System.out.println("Draft data saved successfully.");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Draft + Files saved successfully");
            return response;
        }

    @Override
        public List<ChannelDTO> fetchChannelData(String parentCategory, String childCategory, String channelId, String productId) {
            return damRepository.fetchChannelData(parentCategory, childCategory, channelId, productId);
    }

    // @Override
    //     public boolean removeAssetMetadata(String filePath, String section, String channelId, String productId) {
    //         return damRepository.deleteAssetMetadata(filePath, section, channelId, productId);
    // }

    @Override
    public ResponseEntity<?> removeAsset(Map<String, String> data) {
    try {
        String section = data.get("section");
        String channelId = data.get("channelId");
        String productId = data.get("productId");
        if (section == null || channelId == null || productId == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing section/channelId/productId"));
        }
        ObjectMapper mapper = new ObjectMapper();
        String filePathJson;
            switch (section) {
                case "specification": {
                    String key = data.get("key");
                    String value = data.get("value");
                    if (key == null || value == null) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing key/value for specification"));
                    }
                    filePathJson = mapper.writeValueAsString(Map.of("key", key, "value", value));
                    break;
                }
                case "expertReviews": {
                    String expertName = data.get("expertName");
                    String reviewText = data.get("reviewText");
                    if (expertName == null || reviewText == null) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing expertName/reviewText"));
                    }
                    filePathJson = mapper.writeValueAsString(Map.of("expertName", expertName, "reviewText", reviewText));
                    break;
                }
                case "faq": {
                    String question = data.get("question");
                    String answer = data.get("answer");
                    if (question == null || answer == null) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing question/answer"));
                    }
                    filePathJson = mapper.writeValueAsString(Map.of("question", question, "answer", answer));
                    break;
                }
                default: {
                    filePathJson = data.get("filePath");
                    if (filePathJson == null) {
                        return ResponseEntity.badRequest().body(Map.of("message", "Missing filePath"));
                    }
                }
            }
            boolean removed = damRepository.deleteAssetMetadata(filePathJson, section, channelId, productId);
            if (removed) {
                return ResponseEntity.ok(Map.of("message", "Asset removed successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Asset not found"));
            }
        } 
        catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal error occurred"));
            }
    }

}
