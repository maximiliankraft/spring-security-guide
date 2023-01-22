package at.spengergasse.springsecurity.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

@Getter
@Setter
public class CustomSecurityHolderStrategy implements SecurityContextHolderStrategy {

    SecurityContext context;

    @Override
    public void clearContext() {
        this.context = null;
    }

    @Override
    public SecurityContext createEmptyContext() {
        return null;
    }
}

