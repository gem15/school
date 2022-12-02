package com.example.school.service;

import com.example.school.entity.Mkab;
import com.example.school.repository.MkabRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessExcelChld {

    @Autowired
    MkabRepository mkabRepository;

    private final String source = "D:\\temp\\Выгрузка_МО_1.xlsx";

    public void runs() throws IOException {

        run(Paths.get(source));
    }

    public void run(Path path) {
        IOUtils.setByteArrayMaxOverride(300_000_000);
        try {
            FileInputStream fis = new FileInputStream(path.toString());
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);

            int count = sheet.getLastRowNum(); //10;//
            for (int i = 1; i <= count; i++) {
                Row currentRow = sheet.getRow(i);
                LocalDate date = LocalDate.ofInstant(currentRow.getCell(10).getDateCellValue().toInstant(), ZoneId.systemDefault());
                log.debug("# " + i );
                String ot = null;
                if (currentRow.getCell(9) != null )
                    ot=currentRow.getCell(9).getStringCellValue();

                Mkab mkab = mkabRepository.findBy(date,
                    currentRow.getCell(7).getStringCellValue(),
                    currentRow.getCell(8).getStringCellValue(),
                    ot);//"АЛЕВТИНА", "ВИТАЛЬЕВНА");
                if (mkab == null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fio = date.format(formatter) + " " +
                            currentRow.getCell(7).getStringCellValue() + " " +
                            currentRow.getCell(8).getStringCellValue() + " " +
                            ot;
                    log.debug( fio);
                } else {
                    currentRow.createCell(13, CellType.STRING).setCellValue(mkab.getSs());
                    currentRow.createCell(14, CellType.STRING).setCellValue(mkab.getS_pol());
                    currentRow.createCell(15, CellType.STRING).setCellValue(mkab.getN_pol());
                }
            }
            // finish
            fis.close();//Close the InputStream
            OutputStream outputStream = new FileOutputStream(path.toString());//Open FileOutputStream to write updates
            book.write(outputStream); //write changes
            outputStream.close();  //close the stream
            book.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean snilsMatcher(String s) {
        String regex = "^\\d{3}-\\d{3}-\\d{3} \\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        if (s.equals("000-000-000 00")) {
            return false;
        }
        return matcher.matches();
    }

}
