package com.example.outboxproducer;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class OutboxproducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OutboxproducerApplication.class, args);
	}

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(
				JdbcTemplateLockProvider.Configuration.builder()
						.withJdbcTemplate(new JdbcTemplate(dataSource))
						.usingDbTime() // Використовує час сервера БД, щоб уникнути десинхронізації годинників мікросервісів
						.build()
		);
	}

}
