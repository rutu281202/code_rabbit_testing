package com.contexio.dam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
	
	@GetMapping("/upload-assets-page")
    public String uploadasset() {
        return "dashboard/upload_assets_details";
    }
	
	

}
