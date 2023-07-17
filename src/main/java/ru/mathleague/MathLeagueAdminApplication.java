package ru.mathleague;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MathLeagueAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathLeagueAdminApplication.class, args);
	}

}
