package edu.strauteka.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pong {
    private String pong;
    private Long responseTimeMs;
}
