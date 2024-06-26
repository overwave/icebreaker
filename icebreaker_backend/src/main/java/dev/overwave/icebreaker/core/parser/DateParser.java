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
    public static final String PATTERN = "dd-MMM-yyyy";
    public static final String DATE_WITHOUT_YEAR = "dd-MMM";

    public Instant stringDateToInstant(String stringDate) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(PATTERN)
                .toFormatter(Locale.ENGLISH);
        LocalDate localDate = LocalDate.parse(stringDate, dateTimeFormatter);
        return localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    public String localDateToString(LocalDate date) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(DATE_WITHOUT_YEAR)
                .toFormatter(Locale.of("RU", "ru"));
        return date.format(dateTimeFormatter);
    }

    public LocalDate instantToLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneOffset.UTC);
    }
}
