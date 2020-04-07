package io.github.paritoshgpt1.Housie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("io.github.paritoshgpt1.Housie.repository")
@EntityScan("io.github.paritoshgpt1.Housie.model")
public class HousieApplication {

	public static void main(String[] args) {
		SpringApplication.run(HousieApplication.class, args);
	}

}
