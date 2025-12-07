package ru.effective_mobile.email_service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.effective_mobile.email_service.dto.UserRegisteredEvent;

@Service
@Slf4j
public class EmailNotificationService {

    // Временно принимаем String, а не DTO
    @KafkaListener(topics = "user-registration-topic", groupId = "email-debug-group")
    public void listen(String message) {
        log.info("!!! СЫРОЕ СООБЩЕНИЕ ПОЛУЧЕНО: {}", message);
    }

//    @KafkaListener(topics = "user-registration-topic", groupId = "email-service-group")
//    public void listen(UserRegisteredEvent event) {
//        // Здесь вся бизнес-логика email сервиса
//        log.info("Получено событие регистрации для email: {}", event.getEmail());
//
//        // Имитация отправки письма
//        System.out.println("------------------------------------------------");
//        System.out.println("EMAIL SERVICE: Письмо отправлено на " + event.getEmail());
//        System.out.println("EMAIL SERVICE: Ваш код подтверждения: " + event.getVerificationCode());
//        System.out.println("------------------------------------------------");
//    }
}
