package dev.overwave.icebreaker.core.parser;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@UtilityClass
public class DateParser {
    private static final String PATTERN = "dd-MMM-yyyy";

    public Instant stringDateToInstant(String stringDate) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(PATTERN)
                .toFormatter(Locale.ENGLISH);
        LocalDate localDate = LocalDate.parse(stringDate, dateTimeFormatter);
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }
}
