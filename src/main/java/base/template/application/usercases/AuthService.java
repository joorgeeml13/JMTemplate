package base.template.application.usercases;

import base.template.application.exceptions.InvalidCredentialsException;
import base.template.domain.model.Account;
import base.template.domain.ports.AccountRepositoryPort;
import base.template.shared.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final AccountRepositoryPort accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AccountRepositoryPort accountRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(String email, String password) {
        if(accountRepository.existsByEmail(email))
            throw new IllegalArgumentException("error.account.email.exists");

       String hashedPassword = passwordEncoder.encode(password);

       Account newAccount = new Account(UUID.randomUUID(), email, hashedPassword, null, null);

        accountRepository.save(newAccount);
    }

    public TokenPair login(String email, String password, String clientType) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("error.auth.credentials.invalid"));

        if (!passwordEncoder.matches(password, account.getPassword())) 
            throw new InvalidCredentialsException("error.auth.credentials.invalid");

        String accessToken = jwtUtil.generateAccessToken(account.getId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(account.getId().toString());

        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refreshToken(String refreshToken) {
        String userId = jwtUtil.extractUserId(refreshToken);

        Account account = accountRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new IllegalArgumentException("error.auth.credentials.invalid"));

        if (!jwtUtil.isTokenValid(refreshToken, account.getId().toString())) {
            throw new IllegalArgumentException("error.auth.refresh.token.invalid");
        }

        String accessToken = jwtUtil.generateAccessToken(account.getId().toString());
        String newRefreshToken = jwtUtil.generateRefreshToken(account.getId().toString());

        return new TokenPair(accessToken, newRefreshToken);
    }

    public record TokenPair(String accessToken, String refreshToken) {}
}
