package com.contexio.dam.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

public class ProductController {
    @PostMapping("/fetchProductData")
    @ResponseBody
    public Map<String, Object> fetchProductData(@RequestBody Map<String, Object> requestMap) {
        int draw = (int) requestMap.getOrDefault("draw", 1);
        int start = (int) requestMap.getOrDefault("start", 0);
        int length = (int) requestMap.getOrDefault("length", 10);
        String searchValue = (String) requestMap.getOrDefault("searchValue", "");
        String parentCategoryId = (String) requestMap.get("parentCategoryId");
        List<String> productCategoryId = (List<String>) requestMap.getOrDefault("productCategoryId", new ArrayList<>());
        
        //List<ProductDataDTO> filteredData = damService.getProductData(parentCategoryId, productCategoryId, searchValue, start, length);
       // long totalRecords = damService.countTotalProductData(parentCategoryId, productCategoryId);
        //long filteredRecords = damService.countFilteredProductData(parentCategoryId, productCategoryId, searchValue);

        Map<String, Object> response = new HashMap<>();
        response.put("draw", draw);
        Object totalRecords;
        // response.put("recordsTotal", totalRecords);
        // response.put("recordsFiltered", filteredRecords);
        // response.put("data", filteredData);

        return response;
    }
    
}
