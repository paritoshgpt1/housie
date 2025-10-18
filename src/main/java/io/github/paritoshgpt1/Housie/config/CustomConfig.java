package io.github.paritoshgpt1.Housie.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("custom")
public class CustomConfig {
    String host;
    String scheme;
}
