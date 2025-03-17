package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private String id;
    private String storeId;
    private String storeName;
    private String userId;
    private String userName;
    private BigDecimal totalPrice;
    private String status;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
