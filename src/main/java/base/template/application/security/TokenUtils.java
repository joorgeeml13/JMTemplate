package base.template.application.security;

import base.template.domain.model.auth.Account;

public interface TokenUtils {
    AuthTokens issue(Account account);
    String extractId(String token);
    boolean isTokenValid(String token, String userId);

    boolean isValidFormmat(String token);
}
