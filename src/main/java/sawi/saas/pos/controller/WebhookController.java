package sawi.saas.pos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sawi.saas.pos.dto.ApiResponse;
import sawi.saas.pos.dto.PaymentRequest;
import sawi.saas.pos.dto.PaymentResponse;
import sawi.saas.pos.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> handleMidtransWebhook(@RequestBody Map<String, Object> payload) {
        String orderId = payload.get("order_id").toString();
        String transactionStatus = payload.get("transaction_status").toString();

        if(orderId != null || transactionStatus != null) {
            PaymentResponse paymentResponse = paymentService.updatePaymentStatus(orderId, transactionStatus);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payment process successfully", paymentResponse));
        }

        throw new IllegalArgumentException("Something went wrong");
    }
}
