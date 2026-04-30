package base.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BaseTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseTemplateApplication.class, args);
	}

}
