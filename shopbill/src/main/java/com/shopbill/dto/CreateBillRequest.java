
package com.shopbill.dto;

import java.util.List;

public record CreateBillRequest(
        String customerName,
        String mobileNumber,
        String customerEmail,
        String customerAddress,
        List<BillRequest> items
) {
}
