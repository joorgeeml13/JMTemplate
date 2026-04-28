package base.template.infrastructure.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Base Template API",
        version = "1.0",
        description = "API documentation for Base Template application"
    )
)
public class OpenApiConfig {}
