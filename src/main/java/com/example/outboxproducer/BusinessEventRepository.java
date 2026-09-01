package com.example.outboxproducer;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;

@Repository
public interface BusinessEventRepository extends JpaRepository<BusinessEvent, Long> {
}