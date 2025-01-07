package shu.xai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class BackendDemoWorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendDemoWorkApplication.class, args);
	}

}
