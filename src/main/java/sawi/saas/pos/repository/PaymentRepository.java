package sawi.saas.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sawi.saas.pos.entity.Order;
import sawi.saas.pos.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment>  findByOrderId(UUID orderId);

    List<Payment> findByOrder(Order order);

}
