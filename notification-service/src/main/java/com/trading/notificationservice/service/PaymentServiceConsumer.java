package com.trading.notificationservice.service;


import com.trading.notificationservice.dto.external.PaymentResponseDto;
import com.trading.notificationservice.entity.Notification;
import com.trading.notificationservice.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceConsumer {

    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "payment.done", groupId = "notification-group")
    @Transactional
    public void consumeSuccessfulPayment(PaymentResponseDto paymentResponseDto) {
        log.info("We are consuming the  payment with paymentId : {}", paymentResponseDto.getPaymentId());
        Notification notification = Notification.builder()
                .totalAmount(paymentResponseDto.getTotalAmount())
                .userId(paymentResponseDto.getUserId())
                .orderId(paymentResponseDto.getOrderId())
                .message("Your Order has been successful")
                .build();
        notificationRepository.save(notification);

    }

}
