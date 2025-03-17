package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    String productId;
    Integer quantity;
}
