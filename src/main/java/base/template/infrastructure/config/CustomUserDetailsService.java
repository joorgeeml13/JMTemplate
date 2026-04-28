package base.template.infrastructure.config;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import base.template.domain.model.Account;
import base.template.domain.ports.AccountRepositoryPort;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepositoryPort accountRepository;

    public CustomUserDetailsService(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
       Account account = accountRepository.findById(UUID.fromString(username))
            .orElseThrow(() -> new RuntimeException("error.account.notfound"));

        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new User(account.getEmail(), account.getPassword(), authorities);
    }
    

    public UserDetails loadUserById(UUID id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("error.account.notfound"));

        var authorities = account.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new User(account.getEmail(), account.getPassword(), authorities);
    }
}
