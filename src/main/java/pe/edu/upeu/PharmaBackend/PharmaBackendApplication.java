package pe.edu.upeu.PharmaBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PharmaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PharmaBackendApplication.class, args);
	}

}
