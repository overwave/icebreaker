package dev.overwave.icebreaker.core.parser;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class DateParser {
    private static final String PATTERN = "dd-MMM-yyyy";

    public static Instant stringDateToInstant(String stringDate) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(PATTERN)
                .toFormatter(Locale.ENGLISH);
        LocalDate localDate = LocalDate.parse(stringDate, dateTimeFormatter);
        LocalDateTime localDateTime = localDate.atTime(0, 0, 0);
        ZoneId zoneId = ZoneId.of("UTC");
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.toInstant();
    }
}
