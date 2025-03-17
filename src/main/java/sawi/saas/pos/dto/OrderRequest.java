package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String storeId;
    private List<OrderItemRequest> items;
}
