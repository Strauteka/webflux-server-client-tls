package edu.strauteka.example.scheduler;

import edu.strauteka.example.entity.Pong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class PingPongScheduler {

    private final WebClient webClient;

    public PingPongScheduler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Scheduled(fixedRate = 10000)
    private void pingPong() {
        log.info("calling PingPong!");
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ping")
                        .queryParam("times", 2)
                        .queryParam("delayMs", 10)
                        .queryParam("responseTimeMs", System.currentTimeMillis())

                        .build())
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(Pong.class)
                .doOnNext(n -> log.info("On Next: {}", n))
                .subscribe(
                        success -> log.info("Success: {}", success),
                        throwable -> log.warn("Error: {}", throwable.toString()));
    }
}
