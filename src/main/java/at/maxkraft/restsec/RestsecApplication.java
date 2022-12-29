package at.maxkraft.restsec;

import at.maxkraft.restsec.entity.RsaKeyProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@AllArgsConstructor
public class RestsecApplication {

	@Bean
	public SecurityContextHolder methodInvokingFactoryBean() {

		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);

		return new SecurityContextHolder();
	}

	public static void main(String[] args) {
		SpringApplication.run(RestsecApplication.class, args);
	}

}
