package sawi.saas.pos.dto;

import lombok.Getter;
import lombok.Setter;
import sawi.saas.pos.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PaymentResponse {
    private String id;
    private String transactionId;
    private String paymentStatus;
    private String paymentMethod;
    private String paymentAmount;
    private String redirectUrl;
    private OrderResponse order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
