package com.shopbill.dto;

public record BillRequest(
		String productName,
		Double price,
		Integer quantity
		) {

}
