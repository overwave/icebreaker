package dev.overwave.icebreaker.core.parser;


import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class XlsxParser {

    @SneakyThrows
    public List<List<RawVelocity>> parseIntegralVelocityTable(String filename) {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFWorkbook workbook = new XSSFWorkbook(pkg);

        XSSFSheet lonSheet = workbook.getSheet("lon");
        List<List<RawVelocity>> matrix = new ArrayList<>();

        //проходимся по всем строкам в первых 2х листах с долготой и широтой
        for (int rowNum = lonSheet.getFirstRowNum(); rowNum <= lonSheet.getLastRowNum(); rowNum++) {
            //получаем все данные по интегральной тяжести льда в строке таблицы
            List<RawVelocity> velocitiesInRow = getAllVelocitiesInSheetRow(workbook, rowNum);
            matrix.add(velocitiesInRow);
        }
        return matrix;

    }

    @SneakyThrows
    public List<NavigationPoint> parseNavigationPointsTable(String filename) {
        OPCPackage pkg = OPCPackage.open(filename);
        return doParseNavigationPointsTable(pkg);
    }

    @SneakyThrows
    public List<NavigationPoint> parseNavigationPointsTable(File file) {
        OPCPackage pkg = OPCPackage.open(file);
        return doParseNavigationPointsTable(pkg);
    }

    @SneakyThrows
    private static List<NavigationPoint> doParseNavigationPointsTable(OPCPackage pkg) {
        XSSFWorkbook workbook = new XSSFWorkbook(pkg);
        XSSFSheet pointsSheet = workbook.getSheet("points");
        List<NavigationPoint> points = new ArrayList<>();

        for (int rowNum = 1; rowNum <= pointsSheet.getLastRowNum(); rowNum++) {
            XSSFRow row = pointsSheet.getRow(rowNum);
            float lat = (float) row.getCell(1).getNumericCellValue();
            float lon = (float) row.getCell(2).getNumericCellValue();
            String name = row.getCell(3).getStringCellValue();
            NavigationPoint point = new NavigationPoint(name, lat, lon);
            points.add(point);
        }
        return points;
    }

    private List<RawVelocity> getAllVelocitiesInSheetRow(XSSFWorkbook workbook, int rowNum) {
        XSSFSheet lonSheet = workbook.getSheet("lon");
        XSSFSheet latSheet = workbook.getSheet("lat");
        XSSFRow lonRow = lonSheet.getRow(rowNum);
        XSSFRow latRow = latSheet.getRow(rowNum);

        List<RawVelocity> velocitiesInRow = new ArrayList<>();
        //проходимся по каждой ячейке в строке, чтобы узнать координаты
        for (int cellNum = lonRow.getFirstCellNum(); cellNum < lonRow.getLastCellNum(); cellNum++) {
            XSSFCell lonCell = lonRow.getCell(cellNum);
            XSSFCell latCell = latRow.getCell(cellNum);
            float lon = (float) lonCell.getNumericCellValue();
            float lat = (float) latCell.getNumericCellValue();
            //получаем все значения интегральной тяжести льда для текущих координат
            List<ContinuousVelocity> pointVelocities = getAllPointVelocities(workbook, rowNum, cellNum);
            velocitiesInRow.add(new RawVelocity(new Point(lat, lon), pointVelocities));
        }
        return velocitiesInRow;
    }

    private List<ContinuousVelocity> getAllPointVelocities(XSSFWorkbook workbook, int rowNum, int cellNum) {
        List<ContinuousVelocity> velocities = new ArrayList<>();
        //проходимся по всем листам документа, в которых лежат значения интегральной тяжести льда
        for (int sheetNum = 2; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            XSSFSheet velocitySheet = workbook.getSheetAt(sheetNum);
            String sheetDate = velocitySheet.getSheetName();
            //рассчитываем длительность между двумя датами (листами)
            Instant instant = DateParser.stringDateToInstant(sheetDate);
            Duration duration = Duration.ofDays(7);
            if (sheetNum + 1 < workbook.getNumberOfSheets()) {
                XSSFSheet nextSheet = workbook.getSheetAt(sheetNum + 1);
                Instant nextInstant = DateParser.stringDateToInstant(nextSheet.getSheetName());
                duration = Duration.between(instant, nextInstant);
            }
            float integralVelocity = (float) velocitySheet.getRow(rowNum).getCell(cellNum).getNumericCellValue();
            velocities.add(new ContinuousVelocity(integralVelocity, new Interval(instant, duration)));
        }
        return velocities;
    }

}
