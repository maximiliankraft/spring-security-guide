package at.maxkraft.restsec;

import at.maxkraft.restsec.entity.RsaKeyProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@AllArgsConstructor
public class RestsecApplication {



	public static void main(String[] args) {
		SpringApplication.run(RestsecApplication.class, args);
	}

}
