package com.example.school.service;

import com.example.school.entity.Mkab;
import com.example.school.repository.MkabRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ProcessExcel {

    @Autowired
    MkabRepository mkabRepository;

    public void run() {
//        mymatcher("178-589-563 55");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate date = LocalDate.parse("2001-07-24", formatter);
//        Mkab person = mkabRepository.findBy(date, "АЗАРОВА", "АЛЕВТИНА", "ВИТАЛЬЕВНА");
//        System.out.println(person);

        DataFormatter dataFormatter = new DataFormatter();
        String excelFilePath;
        try {
            excelFilePath = "Справки ШП в ЕМИАС ГАУЗ МО Воскресенская областная больница.xlsx";
//            Biff8EncryptionKey.setCurrentUserPassword("3178");
//            POIFSFileSystem fs = new POIFSFileSystem(new File(excelFilePath), false);
//            opcPackage = OPCPackage.open(excelFilePath, PackageAccess.READ_WRITE);
            FileInputStream fis = new FileInputStream(new File(excelFilePath));
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);

            // Add additional column for results
            int count = sheet.getLastRowNum();
            for (int i = 1; i <= count; i++) {
                Row currentRow = sheet.getRow(i);
                if (currentRow.getCell(9) != null && currentRow.getCell(9).getCellType() == CellType.STRING) {
                    String cellText = currentRow.getCell(9).getStringCellValue();
                    if (cellText != null && !cellText.isEmpty() && !cellText.isBlank() && snilsMatcher(cellText)) {
                        continue;
                    }
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy h:mm");
                LocalDate date = LocalDate.parse(currentRow.getCell(5).getStringCellValue(), formatter);
                log.info("#" + i + " --> " + date);
                ;
                Mkab mkab = mkabRepository.findBy(date,
                        currentRow.getCell(2).getStringCellValue(),
                        currentRow.getCell(3).getStringCellValue(),
                        currentRow.getCell(4).getStringCellValue());//"АЛЕВТИНА", "ВИТАЛЬЕВНА");
                log.info("police# " + mkab.getN_pol() + " snils# " + mkab.getSs() + " s_pol# " + mkab.getS_pol());
                //#7770 --> 2011-11-19  police#  snils#    -   -     s_pol# null

                Cell cell;
                // update снилс
                if (snilsMatcher(mkab.getSs())) {
                    if (currentRow.getCell(9) != null) {
                        currentRow.getCell(9).setCellValue(mkab.getSs());
                        log.info("Updated snils " + mkab.getSs());
                    } else {
                        cell = currentRow.createCell(9,CellType.STRING);
                        cell.setCellValue(mkab.getSs());
                    }
                }

                // add police
                cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                cell.setCellValue(mkab.getN_pol());
                log.info("Added police# " + mkab.getN_pol());

                //add s_police
                if (mkab.getS_pol() != null && !mkab.getS_pol().trim().isEmpty()) {
                    cell = currentRow.createCell(currentRow.getLastCellNum(), CellType.STRING);
                    cell.setCellValue(mkab.getS_pol());
                }
/*
                if(currentRow.getRowNum() == 0)
                    cell.setCellValue("NEW-COLUMN");
*/
            }
            // finish
            fis.close();//Close the InputStream
            OutputStream outputStream = new FileOutputStream(excelFilePath);//Open FileOutputStream to write updates
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
//        System.out.println(s + " : " + matcher.matches());
        return matcher.matches();
    }
}
