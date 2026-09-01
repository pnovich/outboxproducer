package com.example.outboxproducer;
import org.springframework.transaction.annotation.Transactional;import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")public class EventController {

    private final BusinessEventRepository businessRepo;
    private final OutboxEventRepository outboxRepo;

    public EventController(BusinessEventRepository businessRepo, OutboxEventRepository outboxRepo) {
        this.businessRepo = businessRepo;
        this.outboxRepo = outboxRepo;
    }

    @PostMapping
    @Transactional
    public String createEvent(@RequestParam String data) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] REST: Отримано запит на створення івенту.");

        // 1. Зберігаємо бізнес-сутність
        BusinessEvent businessEvent = new BusinessEvent(data);
        businessEvent = businessRepo.save(businessEvent);
        System.out.println("[" + threadName + "] REST: Бізнес-івент збережено з ID: " + businessEvent.getId());

        // 2. Зберігаємо службовий рядок в Outbox в тій же транзакції
        OutboxEvent outboxEvent = new OutboxEvent(
                businessEvent.getId(),
                businessEvent.getId().toString(), // Kafka Key
                businessEvent.getPayload()
        );
        outboxRepo.save(outboxEvent);
        System.out.println("[" + threadName + "] REST: Запис в Outbox успішно створено у статусі PENDING.");

        return "Event created successfully with ID: " + businessEvent.getId();
    }

    @GetMapping("/create")
    @Transactional
    public String createEventGet() {
        String data = "data";
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + threadName + "] REST: Отримано запит на створення івенту.");

        // 1. Зберігаємо бізнес-сутність
        BusinessEvent businessEvent = new BusinessEvent(data);
        businessEvent = businessRepo.save(businessEvent);
        System.out.println("[" + threadName + "] REST: Бізнес-івент збережено з ID: " + businessEvent.getId());

        // 2. Зберігаємо службовий рядок в Outbox в тій же транзакції
        OutboxEvent outboxEvent = new OutboxEvent(
                businessEvent.getId(),
                businessEvent.getId().toString(), // Kafka Key
                businessEvent.getPayload()
        );
        outboxRepo.save(outboxEvent);
        System.out.println("[" + threadName + "] REST: Запис в Outbox успішно створено у статусі PENDING.");

        return "Event created successfully with ID: " + businessEvent.getId();
    }
}