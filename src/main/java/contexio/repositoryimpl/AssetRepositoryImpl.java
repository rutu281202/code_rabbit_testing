package com.contexio.dam.repositoryimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contexio.dam.dto.AssetsDTO;
import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.repository.AssetsRepository;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@Repository
public class AssetRepositoryImpl implements AssetsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(AssetRepositoryImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Resource
    Environment environment;
    
    @Autowired
    HttpServletRequest request;

    private String SQL = null;

    @Override
    public AssetsDTO saveAsset(AssetsDTO asset) {
        
        System.out.println("Inside repository - saving asset");
        
        try {
            logger.info("Saving asset: {}", asset.getAssetName());

            String sql = "INSERT INTO assets (asset_platform_id, asset_type_id, asset_sub_type_id, asset_name, " +
                         "file_name, file_path, file_size, file_type, title, description, country, " +
                         "uploaded_by, thumbnail_path, status) " +
                         "VALUES (:assetPlatformId, :assetTypeId, :assetSubTypeId, :assetName, :fileName, " +
                         ":fileSize, :fileType, :title, :description, :country, :uploadedBy,'Draft')";
            
            @SuppressWarnings("unchecked")
            
            List<CustomerDTO> sess_data = (List<CustomerDTO>) request.getSession().getAttribute("USER_SESSION_DATA");
            String uploadedBy = sess_data.get(0).getCustomerId();

            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue("assetPlatformId", asset.getAssetPlatformId())
                    .addValue("assetTypeId", asset.getAssetTypeId())
                    .addValue("assetSubTypeId", asset.getAssetSubTypeId())
                    .addValue("assetName", asset.getAssetName())
                    .addValue("fileName", asset.getFileName())
                    .addValue("filePath", asset.getFilePath())
                    .addValue("fileSize", asset.getFileSize())
                    .addValue("fileType", asset.getFileType())
                    .addValue("comment", asset.getComment())
                    .addValue("title", asset.getTitle())
                    .addValue("description", asset.getDescription())
                    .addValue("country", asset.getCountry())
                    .addValue("uploadedBy", uploadedBy)
                    .addValue("thumbnailPath", asset.getThumbnailPath())
                    .addValue("status", asset.getStatus());
            

            int rowsAffected = jdbcTemplate.update(sql, params);

            logger.info("Asset saved successfully, Rows affected: {}", rowsAffected);
            return asset;
        } 
        catch (Exception e) {
            logger.error("Failed to save asset: {} - Error: {}", asset.getAssetName(), e.getMessage(), e);
            return null;
        }
    }
	
    public List<Map<String, String>> fetchAssetPlatforms(){
        SQL = environment.getRequiredProperty("FETCH_ASSET_PLATFORM");
        List<Map<String, String>> results = jdbcTemplate.query(SQL, new RowMapper<Map<String, String>>() {
            @Override
            public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Map<String, String> platformData = new HashMap<>();
                platformData.put("asset_platform_id", rs.getString("asset_platform_id"));
                platformData.put("asset_platform_display", rs.getString("asset_platform_display"));
                return platformData;
            }
        });

        logger.info("Fetched platforms: {}", results);
        return results;
    }
    
   
    public List<Map<String, String>> fetchAssetTypes() {
        String SQL = "SELECT asset_type_id, asset_type_name FROM asset_type";

        return jdbcTemplate.query(SQL, (rs, rowNum) -> {
            Map<String, String> assetType = new HashMap<>();
            assetType.put("asset_type_id", rs.getString("asset_type_id"));
            assetType.put("asset_type_name", rs.getString("asset_type_name"));
            return assetType;
        });
    }
    
    
    public List<Map<String, String>> fetchAssetSubtypes(String assetTypeId) {
        String SQL = "SELECT asset_sub_type_id, asset_sub_type_name FROM asset_sub_type WHERE asset_type_id = :assetTypeId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("assetTypeId", assetTypeId);

        return jdbcTemplate.query(SQL, params, (rs, rowNum) -> {
            Map<String, String> subtype = new HashMap<>();
            subtype.put("asset_sub_type_id", rs.getString("asset_sub_type_id"));
            subtype.put("asset_sub_type_name", rs.getString("asset_sub_type_name"));
            return subtype;
        });
    }
    
    
    @Override
    public void saveAssetData(Map<String, String> formData) {
    	String sql = "INSERT INTO assets (asset_name, title, description, comment, asset_platform_id, asset_type_id, " +
                "asset_sub_type_id, country, uploaded_by, file_name, file_path, file_size, file_type, thumbnail_path, status, tags) " +
                "VALUES (:assetName, :title, :description, :comment, :assetPlatform, :assetType, " +
                ":assetSubType, :country, :uploadedBy, :fileName, :filePath, :fileSize, :fileType, :thumbnailPath, :status, :tags)";

        System.out.println("Inside Repo"+formData.get("tags-input"));

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("assetName", formData.get("assetName"))
                .addValue("title", formData.get("title"))
                .addValue("description", formData.get("description"))
                .addValue("comment", formData.get("comment"))
                .addValue("assetPlatform", formData.get("assetPlatform"))
                .addValue("assetType", formData.get("assetType"))
                .addValue("assetSubType", formData.get("assetSubType"))
                .addValue("country", formData.get("country"))
                .addValue("uploadedBy", formData.get("uploadedBy"))
                .addValue("fileName", formData.getOrDefault("fileName", null))
                .addValue("filePath", formData.getOrDefault("filePath", null))
                .addValue("fileSize", formData.getOrDefault("fileSize", null))
                .addValue("fileType", formData.getOrDefault("fileType", null))
                .addValue("thumbnailPath", (formData.get("thumbnailPath") != null && !formData.get("thumbnailPath").isEmpty()) ? formData.get("thumbnailPath") : null)
                .addValue("status", "Draft")
        		.addValue("tags", formData.get("tags-input")); 

        jdbcTemplate.update(sql, parameters);
    }
    
}
