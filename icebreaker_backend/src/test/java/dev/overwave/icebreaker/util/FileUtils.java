package dev.overwave.icebreaker.util;

import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Objects;

@UtilityClass
public class FileUtils {
    public InputStream fromClassPath(String resourceName) {
        return Objects.requireNonNull(FileUtils.class.getResourceAsStream(resourceName));
    }
}
