package com.example.school;

import com.example.school.entity.Response;
import com.example.school.entity.SessionId;
import com.example.school.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class SchoolApplication implements CommandLineRunner {

    @Autowired
    PutDownSnilsWeb  snils;

    public static void main(String[] args) {
        SpringApplication.run(SchoolApplication.class, args).close();
    }

    @Override
    public void run(String... args) {
        String s="10601904302";//106-019-043 02
        s = s.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1-$2-$3 $4");
        //            snils.runs();
        System.out.println("ok");
//        snils.dataEnrichment();
        /*
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		formatter = formatter.withLocale( putAppropriateLocaleHere );  // Locale specifies human language for translating, and cultural norms for lowercase/uppercase and abbreviations and such. Example: Locale.US or Locale.CANADA_FRENCH
        LocalDate date = LocalDate.parse("1960-05-03", formatter);
        Response mkab = webService.getSnils(sessionId.sess_id(),
                "ПЕТРОВА", "ЗОЯ", "МАКСИМОВНА", date).block();
        System.out.println(mkab.data().get(0).PersonSnils_Snils());
*/

//		try {
////			excel.runs();
//			//проставить СНИЛС по ФИО и ДР
//			snils.runs();
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		excel.run();
    }
}
