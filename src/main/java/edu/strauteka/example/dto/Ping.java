package edu.strauteka.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Ping {
    private Integer times = 10;
    private Integer delayMs = 0;
    private Long requestTimeMs = System.currentTimeMillis();
}
