package base.template.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import base.template.domain.model.auth.HashedPassword;
import base.template.domain.model.auth.RawPassword;
import base.template.domain.service.PasswordHasher;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpringBcryptPasswordHasher implements PasswordHasher{
    
    private final PasswordEncoder springEncoder;

    @Override
    public HashedPassword hash(RawPassword rawPassword) {
        String hashed = springEncoder.encode(rawPassword.getValue());
        return new HashedPassword(hashed);
    }

    @Override
    public boolean matches(RawPassword rawPassword, HashedPassword hashedPassword) {
        return springEncoder.matches(rawPassword.getValue(), hashedPassword.getValue());
    }
}
