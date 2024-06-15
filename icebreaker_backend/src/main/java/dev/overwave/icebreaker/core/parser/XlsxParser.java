package dev.overwave.icebreaker.core.parser;


import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.RawVelocity;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationRoute;
import dev.overwave.icebreaker.core.util.GeometryUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.Spliterators.spliteratorUnknownSize;

@UtilityClass
public class XlsxParser {
    private static final Period VELOCITY_INTERVAL_OFFSET = Period.ofDays(365 * 4 + 1);

    @SneakyThrows
    public void createFileWithGanttDiagram(List<ShipSchedule> segmentsByShipName,
                                           LocalDate firstDate, LocalDate finishDate) {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Gantt diagram");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 10000);

        Row header = sheet.createRow(0);
        Cell headerShipName = header.createCell(0, CellType.STRING);
        headerShipName.setCellValue("Название судна");
        Cell headerSegment = header.createCell(1, CellType.STRING);
        headerSegment.setCellValue("Сегмент маршрута");
        // заполняем 0ю колонку названиями кораблей и 1ю колонку инфой о сегментах
        int rowNum = 0;
        Map<Integer, ScheduleSegment> segmentsByRowIdx = new HashMap<>();
        // названия кораблей и сегменты начинаются с 1й строки
        for (ShipSchedule shipSchedule : segmentsByShipName) {
            Row shipNameRow = sheet.createRow(++rowNum);
            Cell shipNameCell = shipNameRow.createCell(0, CellType.STRING);
            shipNameCell.setCellValue(shipSchedule.shipName());
            Cell firstSegmentCell = shipNameRow.createCell(1, CellType.STRING);
            List<ScheduleSegment> segments = shipSchedule.segments();
            firstSegmentCell.setCellValue(segments.getFirst().startPointName() + " - "
                    + segments.getFirst().finishPointName());
            segmentsByRowIdx.put(rowNum, segments.getFirst());
            for (int i = 1; i < segments.size(); i++) {
                Row segmentRow = sheet.createRow(++rowNum);
                Cell nextSegmentCell = segmentRow.createCell(1, CellType.STRING);
                nextSegmentCell.setCellValue(segments.get(i).startPointName() + " - "
                        + segments.get(i).finishPointName());
                segmentsByRowIdx.put(rowNum, segments.get(i));
            }
        }

        // стиль для строки с датами
        DataFormat format = workbook.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat(DateParser.PATTERN));
        dateStyle.setAlignment(HorizontalAlignment.CENTER);

        // стиль для окрашивания ячеек
        CellStyle colored = workbook.createCellStyle();
        colored.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        colored.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int dateCellIdx = 2;
        //заполняем остальные колонки с датами и закрашиваем ячейки
        for (LocalDate date = firstDate; !date.isAfter(finishDate); date = date.plusDays(1)) {
            sheet.setColumnWidth(dateCellIdx, 4000);
            Cell dateCell = header.createCell(dateCellIdx, CellType.STRING);
            dateCell.setCellStyle(dateStyle);
            dateCell.setCellValue(DateParser.localDateToString(date));
            for (int rowIdx = 1; rowIdx <= rowNum; rowIdx++) {
                ScheduleSegment segment = segmentsByRowIdx.get(rowIdx);
                Row segmentRow = sheet.getRow(rowIdx);
                Cell segmentCell = segmentRow.createCell(dateCellIdx, CellType.BLANK);
                if (segment.contains(date)) {
                    segmentCell.setCellStyle(colored);
                }
            }
            dateCellIdx++;
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "scheduleGantt.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
    }

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
            if (lon < 0) {
                lon += 360;
            }
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
            NavigationRoute route = new NavigationRoute(startPoint, endPoint, GeometryUtils.getDistance(
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
        // проходимся по всем листам документа, в которых лежат значения интегральной тяжести льда и сортируем по датам
        List<XSSFSheet> sheetsSorted =
                StreamSupport.stream(spliteratorUnknownSize(workbook.iterator(), Spliterator.ORDERED), false)
                        .skip(2)
                        .sorted(Comparator.comparing(sheet -> DateParser.stringDateToInstant(sheet.getSheetName())))
                        .map(s -> (XSSFSheet) s)
                        .toList();
        for (int i = 0; i < sheetsSorted.size(); i++) {
            // рассчитываем длительность между двумя датами (листами)
            XSSFSheet currentSheet = sheetsSorted.get(i);
            Duration duration = Duration.ofDays(7);
            Instant instant = DateParser.stringDateToInstant(currentSheet.getSheetName());
            if (i + 1 < sheetsSorted.size()) {
                Instant nextInstant = DateParser.stringDateToInstant(sheetsSorted.get(i + 1).getSheetName());
                duration = Duration.between(instant, nextInstant);
            }
            // складываем ContinuousVelocity в правильном порядке
            float integralVelocity = (float) currentSheet.getRow(rowNum).getCell(cellNum).getNumericCellValue();
            velocities.add(new ContinuousVelocity(integralVelocity,
                    new Interval(instant.plus(VELOCITY_INTERVAL_OFFSET), duration)));
        }
        return velocities;
    }

}
