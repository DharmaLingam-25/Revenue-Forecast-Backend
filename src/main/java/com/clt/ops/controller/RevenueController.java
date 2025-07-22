package com.clt.ops.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.clt.ops.service.RevenueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@CrossOrigin(origins = "http://localhost:3000") 
@RestController
@Slf4j
@RequiredArgsConstructor
public class RevenueController {
	
	
	private final RevenueService revenueService;
	
	@PostMapping("/upload")
	public void fileUpload(
	        @RequestHeader(name = "type") String type,@RequestParam("file") MultipartFile file) {
		log.info("Type :::::::::::::{}",type);
		log.info("File Name:::::::::::::{}",file.getOriginalFilename());
	    revenueService.uploadFile(type, file);
	   
	}
	


}
