package com.example.school.service;

import com.example.school.entity.Mkab;
import com.example.school.repository.MkabRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProcessExcel {

    @Autowired
    MkabRepository mkabRepository;

    private final String source = "D:\\temp\\Справки ШП в ЕМИАС_xlsx";

    public void runs() throws IOException {

        List<Path> files = Files.walk(Paths.get(source)).filter(Files::isRegularFile).collect(Collectors.toList());
//        System.out.println(files.size());
        for (int i = 0; i < files.size(); i++) {
            log.debug("Обрабатывается - " + files.get(i).getFileName().toString() + " # " + (i + 1));
            run(files.get(i));
        }
    }

    public void run(Path path) {

        try {
            FileInputStream fis = new FileInputStream(path.toString());
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);

            int j = 0;
            int count = sheet.getLastRowNum();
            for (int i = 1; i <= count; i++) {
                Row currentRow = sheet.getRow(i);
/*
                if (currentRow.getCell(9) != null && currentRow.getCell(9).getCellType() == CellType.STRING) {
                    String cellText = currentRow.getCell(9).getStringCellValue();
                    if (cellText != null && !cellText.isEmpty() && !cellText.isBlank() && snilsMatcher(cellText)) {
                        continue;
                    }
                }
*/
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy h:mm");
                LocalDate date = LocalDate.parse(currentRow.getCell(5).getStringCellValue(), formatter);
                //log.debug("#" + i + " --> " + date);

                Mkab mkab = mkabRepository.findBy(date,
                        currentRow.getCell(2).getStringCellValue(),
                        currentRow.getCell(3).getStringCellValue(),
                        currentRow.getCell(4).getStringCellValue());//"АЛЕВТИНА", "ВИТАЛЬЕВНА");

                Cell cell = currentRow.createCell(15, CellType.STRING);
                if (mkab == null) {
                    cell.setBlank();
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String fio = date.format(formatter) + " " +
                            currentRow.getCell(2).getStringCellValue() + " " +
                            currentRow.getCell(3).getStringCellValue() + " " +
                            currentRow.getCell(4).getStringCellValue();
                    log.debug("# " + i + " " + fio);
                    continue;
                } else {
//                    cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                    if (mkab.getN_pol().trim().length() == 16) {
                        cell.setCellValue(mkab.getN_pol());
                        log.debug("Added police# " + mkab.getN_pol());
                    } else {
                        if (mkab.getS_pol() != null && mkab.getS_pol().trim().length() == 6) {
                            cell.setCellValue(mkab.getS_pol().trim() + mkab.getN_pol().trim());
                            log.debug("Added combo police# " + mkab.getN_pol());
                        }
                    }
                    continue;
                }

//                log.debug(currentRow.getCell(2).getStringCellValue() + "--" + "police# " + mkab.getN_pol() + " snils# " + mkab.getSs());

//                Cell cell;
                // update снилс
/*
                if (snilsMatcher(mkab.getSs())) {
                    if (currentRow.getCell(9) != null) {
                        currentRow.getCell(9).setCellValue(mkab.getSs());
                        //log.debug("CreateUpdated snils " + mkab.getSs());
                    } else {
                        cell = currentRow.createCell(9, CellType.STRING);
                        cell.setCellValue(mkab.getSs());
                        //log.debug("Updated snils " + mkab.getSs());
                    }
                } else {
                    // add police
                    cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                    if (mkab.getN_pol().trim().length() == 16) {
                        cell.setCellValue(mkab.getN_pol());
                        //log.debug("Added police# " + mkab.getN_pol());
                    } else {
                        //add s_police
                        if (mkab.getS_pol() != null && !mkab.getS_pol().trim().isEmpty()) {
                            cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                            if (mkab.getS_pol().trim().length() == 6) {
                                cell.setCellValue(mkab.getS_pol().trim() + mkab.getN_pol().trim());
                                //log.debug("Added S+N police# " + mkab.getN_pol());
                            }
                        }
                    }
                }
*/

            }
            // finish
            fis.close();//Close the InputStream
            OutputStream outputStream = new FileOutputStream(path.toString());//Open FileOutputStream to write updates
            book.write(outputStream); //write changes
            outputStream.close();  //close the stream
            book.close();
            log.debug("----> " + j);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean snilsMatcher(String s) {
        //        mymatcher("178-589-563 55");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate date = LocalDate.parse("2001-07-24", formatter);
//        Mkab person = mkabRepository.findBy(date, "АЗАРОВА", "АЛЕВТИНА", "ВИТАЛЬЕВНА");
//        System.out.println(person);
        String regex = "^\\d{3}-\\d{3}-\\d{3} \\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
//        System.out.println(s + " : " + matcher.matches());
        if (s.equals("000-000-000 00")) {
            //log.debug("#" + " --> " + "000-000-000 00");
            return false;
        }
        return matcher.matches();
    }

}
