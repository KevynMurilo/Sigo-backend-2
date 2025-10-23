package br.gov.formosa.sigo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)

public class Sigo2Application {

	public static void main(String[] args) {
		SpringApplication.run(Sigo2Application.class, args);
	}

}
