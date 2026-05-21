package com.infiniteVision.schoolProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication(scanBasePackages = {
		"com.infiniteVision.schoolProject",
		"com.school.auth"
})
@EntityScan(basePackages = "com.school.auth.entity")
public class SchoolProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolProjectApplication.class, args);
	}

}
