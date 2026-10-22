package com.shopbill.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopbill.dto.BillRequest;

@RestController()
@RequestMapping("/api/billing")
public class BillingController {
	
	@PostMapping("/calculate")
	public Map<String, Object> calculateBill(@RequestBody List<BillRequest> items){
		double total = 0;
		
		for(BillRequest item : items) {
			total += item.price() * item.quantity();
		}
		
		return Map.of(
				"Items", items,
				"Total Amount", Math.round(total * 100.0) / 100.0
				);
		
	}
}
