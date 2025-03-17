package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
