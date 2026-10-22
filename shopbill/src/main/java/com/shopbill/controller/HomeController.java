package com.shopbill.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	
	@GetMapping("/api/health")
	public String healthCheck() {
		return "shop bill apiis running successfully";
	}
}
