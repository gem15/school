package com.example.school;

import com.example.school.service.ProcessExcel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class SchoolApplicationTests {

@Autowired
	ProcessExcel  excel;
	@Test
	void mkabTest() {
		excel.snilsMatcher("178-589-563 55");
//		excel.run();
	}
}
