package com.trading.paymentservice.repository;

import com.trading.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findPaymentByUserId(Long userId);

    Payment findPaymentByOrderId(Long userId);

    boolean existsPaymentByOrderId(Long orderId);
}
