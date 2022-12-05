package com.example.school.service;

import com.example.school.entity.Data;
import com.example.school.entity.Mkab;
import com.example.school.entity.Response;
import com.example.school.entity.SessionId;
import com.example.school.repository.MkabRepository;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PutDownSnilsWeb {

    @Autowired
    WebService webService;

    private final String source = "D:\\temp\\rr_301122.xlsx";

    public void runs() throws IOException {
        run(Paths.get(source));
    }

    public void run(Path path) {
        SessionId sessionId = webService.getSessionId().block();
        if (sessionId.error_code() != 0)
            throw new RuntimeException("Не удалось получить токен");

        IOUtils.setByteArrayMaxOverride(300_000_000);
        try {
            FileInputStream fis = new FileInputStream(path.toString());
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);

            int count = sheet.getLastRowNum(); //10;//
            for (int i = 1; i <= count; i++) {
                Row currentRow = sheet.getRow(i);

                log.debug("# " + i);
                if (currentRow.getCell(6).getDateCellValue() == null) continue;
                LocalDate date = LocalDate.ofInstant(currentRow.getCell(6).getDateCellValue().toInstant(), ZoneId.systemDefault());
                String ot = null;
                if (currentRow.getCell(5) != null)
                    ot = currentRow.getCell(5).getStringCellValue();

                Response mkab = webService.getSnils(sessionId.sess_id(),
                        currentRow.getCell(3).getStringCellValue(),
                        currentRow.getCell(4).getStringCellValue(),
                        ot, date).block();

                if (mkab != null) {
                    Optional<Data> first = mkab.data().stream().filter(p ->
                            !(p.PersonSnils_Snils() == null
                                    || p.PersonSnils_Snils().isEmpty())).findFirst();
                    if (first.isEmpty()) {
                        continue;
                    }
                    currentRow.createCell(16, CellType.STRING).setCellValue(
                            mkab.data().get(0).PersonSnils_Snils());
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

    public void dataEnrichment() {
        SessionId sessionId = webService.getSessionId().block();
        if (sessionId.error_code() != 0)
            throw new RuntimeException("Не удалось получить токен");

        Path path = Paths.get(source);
        IOUtils.setByteArrayMaxOverride(300_000_000);
        try {
            FileInputStream fis = new FileInputStream(path.toString());
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);

            int count = sheet.getLastRowNum(); //10;//
            for (int i = 1; i <= count; i++) {
                Row row = sheet.getRow(i);

                if ((row.getCell(15) == null
                        || row.getCell(15).getCellType() == CellType.BLANK
                        || row.getCell(15).getStringCellValue().trim().isEmpty())
                        &&
                        (row.getCell(16) == null
                        || row.getCell(16).getCellType() == CellType.BLANK
                        || row.getCell(16).getStringCellValue().trim().isEmpty())) {
                    continue;
                }
                if (row.getCell(15) == null || row.getCell(15).getStringCellValue().trim().isEmpty()) {
//                    log.debug("# " + i);
                    if (row.getCell(15) == null || row.getCell(15).getCellType() == CellType.BLANK || row.getCell(15).getStringCellValue().trim().isEmpty()) {
                        row.createCell(15, CellType.STRING).setCellValue(
                                row.getCell(16).getStringCellValue()
                                .replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1-$2-$3 $4"));
                        System.out.println(i+" "+row.getCell(15).getStringCellValue());
                    }
                }

            }
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
