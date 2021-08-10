package edu.strauteka.example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class ExampleApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(ExampleApplication.class, args);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Starting exit...");
			ctx.close();
		}));
	}
}
