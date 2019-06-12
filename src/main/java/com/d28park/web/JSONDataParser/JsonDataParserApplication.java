package com.d28park.web.JSONDataParser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;*/

/*@EnableJpaRepositories("com.d28park.persistence.repo")
@EntityScan("com.d28park.persistence.model")*/
@SpringBootApplication
public class JsonDataParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(JsonDataParserApplication.class, args);
	}
}