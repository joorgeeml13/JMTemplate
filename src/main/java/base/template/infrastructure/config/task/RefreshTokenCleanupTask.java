package base.template.infrastructure.config.task;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import base.template.application.ports.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenCleanupTask {
    private final RefreshTokenRepositoryPort refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredTokens() {
        log.info("Iniciando limpieza de refresh tokens expirados...");
        
        try {
            refreshTokenRepository.deleteExpiredTokens(Instant.now());
            log.info("Limpieza completada con éxito. Base de datos estética de nuevo 🧼");
        } catch (Exception e) {
            log.error("Error limpiando tokens: {}", e.getMessage());
        }
    }
}
