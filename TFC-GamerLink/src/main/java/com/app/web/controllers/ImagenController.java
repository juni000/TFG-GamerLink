package com.app.web.controllers;

import java.net.MalformedURLException;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.web.service.UploadFileService;

@Controller
@RequestMapping("/imagen")
public class ImagenController {
	private final UploadFileService uploadFileService;
	 public ImagenController(UploadFileService uploadFileService) {
		this.uploadFileService = uploadFileService;
	}
	 
	 @RequestMapping("/uploads/{filename:.+}")
	 public ResponseEntity<Resource> getImage(@PathVariable String filename) {
		 Resource resource = null;
		try {
			resource = uploadFileService.load(filename);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
					 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					 .body(resource);
	 }
	 @GetMapping("/new")
	 public String newImage(Model model) {
		 model.addAttribute("title", "New Image");
		 return "imagen/new";
	 }
	 
}
