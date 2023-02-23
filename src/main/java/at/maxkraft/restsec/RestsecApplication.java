package at.maxkraft.restsec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class RestsecApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestsecApplication.class, args);
	}
}
