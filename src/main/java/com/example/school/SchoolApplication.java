package com.example.school;

import com.example.school.service.ProcessExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SchoolApplication  implements CommandLineRunner {

	@Autowired
	ProcessExcel excel;

	public static void main(String[] args) {
		SpringApplication.run(SchoolApplication.class, args);
	}

	@Override
	public void run(String... args)  {
		try {
			excel.runs();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		excel.run();
	}
}
