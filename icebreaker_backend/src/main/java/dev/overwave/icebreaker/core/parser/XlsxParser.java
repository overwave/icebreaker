package dev.overwave.icebreaker.core.parser;


import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationRoute;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

@UtilityClass
public class XlsxParser {

    @SneakyThrows
    public List<List<RawVelocity>> parseIntegralVelocityTable(String filename) {
        OPCPackage pkg = OPCPackage.open(requireNonNull(XlsxParser.class.getResourceAsStream(filename)));
        XSSFWorkbook workbook = new XSSFWorkbook(pkg);

        return doParceIntegralVelocityTable(workbook);
    }

    @SneakyThrows
    public List<List<RawVelocity>> parseIntegralVelocityTable(InputStream inputStream) {
        XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(inputStream));

        return doParceIntegralVelocityTable(workbook);
    }

    private static List<List<RawVelocity>> doParceIntegralVelocityTable(XSSFWorkbook workbook) {
        XSSFSheet lonSheet = workbook.getSheet("lon");
        List<List<RawVelocity>> matrix = new ArrayList<>();

        // проходимся по всем строкам в первых 2х листах с долготой и широтой
        for (int rowNum = lonSheet.getFirstRowNum(); rowNum <= lonSheet.getLastRowNum(); rowNum++) {
            // получаем все данные по интегральной тяжести льда в строке таблицы
            List<RawVelocity> velocitiesInRow = getAllVelocitiesInSheetRow(workbook, rowNum);
            matrix.add(velocitiesInRow);
        }
        return matrix;
    }

    @SneakyThrows
    public List<NavigationPoint> parseNavigationPointsTable(InputStream inputStream) {
        XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(inputStream));
        List<NavigationPoint> points = getNavigationPoints(workbook.getSheet("points"));
        addNavigationEdges(points, workbook.getSheet("edges"));
        return points;
    }

    private static List<NavigationPoint> getNavigationPoints(XSSFSheet pointsSheet) {
        List<NavigationPoint> points = new ArrayList<>();
        //  пропускаем шапку
        for (int rowNum = 1; rowNum <= pointsSheet.getLastRowNum(); rowNum++) {
            XSSFRow row = pointsSheet.getRow(rowNum);
            int externalId = (int) row.getCell(0).getNumericCellValue();
            float lat = (float) row.getCell(1).getNumericCellValue();
            float lon = (float) row.getCell(2).getNumericCellValue();
            String name = row.getCell(3).getStringCellValue();
            points.add(new NavigationPoint(externalId, name, lat, lon, new ArrayList<>(), new ArrayList<>()));
        }
        return points;
    }

    private void addNavigationEdges(List<NavigationPoint> points, XSSFSheet edgesSheet) {
        //  пропускаем шапку
        for (int rowNum = 1; rowNum <= edgesSheet.getLastRowNum(); rowNum++) {
            XSSFRow row = edgesSheet.getRow(rowNum);
            NavigationPoint startPoint = getPointById(points, row.getCell(1));
            NavigationPoint endPoint = getPointById(points, row.getCell(2));
            NavigationRoute route = new NavigationRoute(startPoint, endPoint, GraphFactory.getDistance(
                    new Point(startPoint.getLat(), startPoint.getLon()),
                    new Point(endPoint.getLat(), endPoint.getLon())
            ));
            startPoint.getRoutes1().add(route);
            endPoint.getRoutes2().add(route);
        }
    }

    private static NavigationPoint getPointById(List<NavigationPoint> points, XSSFCell cell) {
        int pointId = (int) cell.getNumericCellValue();
        return points.stream().filter(point -> point.getExternalId() == pointId).findFirst().orElseThrow();
    }

    private List<RawVelocity> getAllVelocitiesInSheetRow(XSSFWorkbook workbook, int rowNum) {
        XSSFSheet lonSheet = workbook.getSheet("lon");
        XSSFSheet latSheet = workbook.getSheet("lat");
        XSSFRow lonRow = lonSheet.getRow(rowNum);
        XSSFRow latRow = latSheet.getRow(rowNum);

        List<RawVelocity> velocitiesInRow = new ArrayList<>();
        // проходимся по каждой ячейке в строке, чтобы узнать координаты
        for (int cellNum = lonRow.getFirstCellNum(); cellNum < lonRow.getLastCellNum(); cellNum++) {
            XSSFCell lonCell = lonRow.getCell(cellNum);
            XSSFCell latCell = latRow.getCell(cellNum);
            float lon = (float) lonCell.getNumericCellValue();
            float lat = (float) latCell.getNumericCellValue();
            // получаем все значения интегральной тяжести льда для текущих координат
            List<ContinuousVelocity> pointVelocities = getAllPointVelocities(workbook, rowNum, cellNum);
            velocitiesInRow.add(new RawVelocity(new Point(lat, lon), pointVelocities));
        }
        return velocitiesInRow;
    }

    private List<ContinuousVelocity> getAllPointVelocities(XSSFWorkbook workbook, int rowNum, int cellNum) {
        List<ContinuousVelocity> velocities = new ArrayList<>();
        // проходимся по всем листам документа, в которых лежат значения интегральной тяжести льда
        for (int sheetNum = 2; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            XSSFSheet velocitySheet = workbook.getSheetAt(sheetNum);
            String sheetDate = velocitySheet.getSheetName();
            // рассчитываем длительность между двумя датами (листами)
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
