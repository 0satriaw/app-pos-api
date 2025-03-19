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
import sawi.saas.pos.entity.Payment;
import sawi.saas.pos.entity.PaymentStatus;
import sawi.saas.pos.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentService.createPayment(UUID.fromString(paymentRequest.getOrder_id()), paymentRequest.getGross_amount());

        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created successfully", paymentResponse));
    }
}
