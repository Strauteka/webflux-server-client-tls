package edu.strauteka.example.controller;

import edu.strauteka.example.dto.Ping;
import edu.strauteka.example.entity.Pong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping(value = "ping", produces = MediaType.APPLICATION_JSON_VALUE)
public class PingController {
    //spring autowire Ping class request params

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_NDJSON_VALUE})
    Flux<Pong> ping(Ping ping) {
        log.info("Ping {}", ping);
        return Flux.generate(() -> 1L,
                (state, sink) -> {
                    sink.next(state);
                    return state + 1L;
                })
                .take(ping.getTimes())
                .delayElements(Duration.ofMillis(ping.getDelayMs()))
                .map(n -> new Pong("Pong " + n, System.currentTimeMillis() - ping.getRequestTimeMs()));
    }
}
