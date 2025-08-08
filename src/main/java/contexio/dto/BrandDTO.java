package com.contexio.dam.dto;


public class BrandDTO {
    private String id;
    private String name;
    private String subBrandId;
    private String mappingBrandSubbrandId;
    
    public BrandDTO(String id, String name, String subBrandId, String mappingBrandSubbrandId){
        this.id = id;
        this.name = name;  
        this.subBrandId = subBrandId; 
        this.mappingBrandSubbrandId = mappingBrandSubbrandId;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSubBrandId() { 
        return subBrandId; 
    }
    public void setSubBrandId(String subBrandId) { 
        this.subBrandId = subBrandId; 
    }
     public String getMappingBrandSubbrandId() {
        return mappingBrandSubbrandId;
    }
    public void setMappingBrandSubbrandId(String mappingBrandSubbrandId) {
        this.mappingBrandSubbrandId = mappingBrandSubbrandId;
    }
    @Override
    public String toString() {
        return "BrandDTO [id=" + id + ", name=" + name + ", subBrandId=" + subBrandId + "]";
    }
}

