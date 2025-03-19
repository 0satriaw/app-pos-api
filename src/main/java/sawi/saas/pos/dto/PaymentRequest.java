package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private String order_id;
    private String gross_amount;
}
