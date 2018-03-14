package com.ido.qna;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("index")
@Slf4j
public class QnaApplication {

	@PostMapping("data")
	public String data(Person person){
		log.info(person.toString());
		return "test";
	}

	public static void main(String[] args) {
		SpringApplication.run(QnaApplication.class, args);
	}

	@Data
	public static class Person{
		int age;
		String name;


	}
}
