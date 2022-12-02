package com.example.school;

import com.example.school.service.ProcessExcel;
import com.example.school.service.ProcessExcelChld;
import com.example.school.service.PutDownSnils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class SchoolApplication  implements CommandLineRunner {

	@Autowired
	ProcessExcelChld excel;
	@Autowired
	PutDownSnils  snils;

	public static void main(String[] args) {
		SpringApplication.run(SchoolApplication.class, args);
	}

	@Override
	public void run(String... args)  {
		try {
//			excel.runs();
			//проставить СНИЛС по ФИО и ДР
			snils.runs();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		excel.run();
	}
}
