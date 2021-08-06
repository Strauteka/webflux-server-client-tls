package edu.strauteka.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
public class Ping {
    private Integer times = 10;
    private Integer delayMs = 0;
    private Long requestTimeMs = System.currentTimeMillis();
}
