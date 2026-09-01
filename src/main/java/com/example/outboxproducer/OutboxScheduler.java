package com.example.outboxproducer;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.stereotype.Component;import org.springframework.transaction.annotation.Propagation;import org.springframework.transaction.annotation.Transactional;import java.util.List;

@Component
public class OutboxScheduler {

    private final OutboxEventRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxScheduler(OutboxEventRepository outboxRepo, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepo = outboxRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000) // Кожні 5 секунд
    @SchedulerLock(
            name = "outbox_processing_lock", // Унікальне ім'я локу в таблиці БД
            lockAtMostFor = "4s", // Якщо сервіс упаде, лок автоматично зніметься через 4 секунди
            lockAtLeastFor = "2s" // Навіть якщо метод виконається миттєво, тримати лок мінімум 2 секунди (захист від занадто частого смикання бази іншими серверами)
    )
    public void processOutbox() {
        String threadName = Thread.currentThread().getName();

        // Крок 1: Знаходимо записи PENDING
        List<OutboxEvent> pendingEvents = outboxRepo.findByStatus("PENDING");
        if (pendingEvents.isEmpty()) {
            return;
        }

        System.out.println("[" + threadName + "] SCHEDULER: Знайдено невідправлених івентів: " + pendingEvents.size());

        for (OutboxEvent outbox : pendingEvents) {
            try {
                System.out.println("[" + threadName + "] SCHEDULER: Спроба відправки івенту Outbox_ID: " + outbox.getId());

                // Крок 2: Синхронно шлемо в Кафку і чекаємо на Ack (.join())
                kafkaTemplate.send("test-topic", outbox.getKafkaKey(), outbox.getPayload()).join();
                System.out.println("[" + threadName + "] SCHEDULER: Kafka повернула ACK для Outbox_ID: " + outbox.getId());

                // Крок 3: Оновлюємо статус в окремій транзакції
                updateStatusToProcessed(outbox.getId());
                System.out.println("[" + threadName + "] SCHEDULER: Статус в БД успішно змінено на PROCESSED для Outbox_ID: " + outbox.getId());

            } catch (Exception e) {
                System.err.println("[" + threadName + "] SCHEDULER: Помилка відправки або оновлення для Outbox_ID: " + outbox.getId() + ". Причина: " + e.getMessage());
                // Запис залишається в PENDING, крон спробує ще раз через 5 секунд
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusToProcessed(Long id) {
        outboxRepo.findById(id).ifPresent(outbox -> {
            outbox.setStatus("PROCESSED");
            outboxRepo.save(outbox);
        });
    }
}
