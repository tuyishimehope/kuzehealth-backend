package rw.ac.auca.kuzahealth.core.growth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * WHO Child Growth Standards reference tables (LMS parameters) and the z-score
 * calculation that uses them.
 *
 * <p>The tables are not bundled. Put the official WHO tables as CSV files in the
 * directory named by {@code app.growth.reference-path}; without them z-scores are
 * simply not reported. Each file has a header row followed by {@code x,L,M,S} rows,
 * where x is the age in days (or in months when the header starts with "month"),
 * or the length in cm for weight-for-length.
 */
@Component
public class GrowthReference {

    private static final Logger logger = LoggerFactory.getLogger(GrowthReference.class);
    private static final double DAYS_PER_MONTH = 30.4375;

    public enum Sex {
        BOYS, GIRLS;

        /** @return empty when the recorded gender is missing or not recognisable */
        public static Optional<Sex> fromGender(String gender) {
            if (gender == null) {
                return Optional.empty();
            }
            return switch (gender.trim().toLowerCase()) {
                case "m", "male", "boy", "gabo" -> Optional.of(BOYS);
                case "f", "female", "girl", "gore" -> Optional.of(GIRLS);
                default -> Optional.empty();
            };
        }
    }

    private record Lms(double l, double m, double s) {
    }

    private final Map<GrowthIndicator, Map<Sex, TreeMap<Double, Lms>>> tables = new EnumMap<>(GrowthIndicator.class);

    public GrowthReference(ResourceLoader resourceLoader,
            @Value("${app.growth.reference-path:classpath:growth/who/}") String referencePath) {
        String base = referencePath.endsWith("/") ? referencePath : referencePath + "/";
        for (GrowthIndicator indicator : GrowthIndicator.values()) {
            for (Sex sex : Sex.values()) {
                String location = base + indicator.getFileStem() + "_" + sex.name().toLowerCase() + ".csv";
                Resource resource = resourceLoader.getResource(location);
                if (!resource.exists()) {
                    continue;
                }
                try {
                    tables.computeIfAbsent(indicator, key -> new EnumMap<>(Sex.class)).put(sex, read(resource));
                } catch (IOException | RuntimeException e) {
                    logger.error("Could not read growth reference table {}", location, e);
                }
            }
        }
        if (tables.isEmpty()) {
            logger.info("No WHO growth reference tables found under {}; z-scores will not be reported", base);
        }
    }

    public boolean isAvailable() {
        return !tables.isEmpty();
    }

    /**
     * @param x age in days, or length in cm for weight-for-length
     * @return the z-score, or empty when there is no table for it or x is outside the table
     */
    public Optional<Double> zScore(GrowthIndicator indicator, Sex sex, double x, double value) {
        TreeMap<Double, Lms> table = tables.getOrDefault(indicator, Map.of()).get(sex);
        if (table == null || table.isEmpty() || x < table.firstKey() || x > table.lastKey()) {
            return Optional.empty();
        }
        Lms lms = interpolate(table, x);
        double z = rawZ(lms, value);
        if (indicator.isWeightBased() && Math.abs(z) > 3) {
            z = adjustBeyondThreeSd(lms, value, z);
        }
        return Optional.of(Math.round(z * 100) / 100.0);
    }

    private static double rawZ(Lms lms, double value) {
        return Math.abs(lms.l) < 1e-9
                ? Math.log(value / lms.m) / lms.s
                : (Math.pow(value / lms.m, lms.l) - 1) / (lms.l * lms.s);
    }

    /** WHO's correction for skewed, weight-based indicators outside the range of -3 to +3 SD. */
    private static double adjustBeyondThreeSd(Lms lms, double value, double z) {
        double sd3pos = valueAt(lms, 3);
        double sd2pos = valueAt(lms, 2);
        double sd3neg = valueAt(lms, -3);
        double sd2neg = valueAt(lms, -2);
        return z > 3
                ? 3 + (value - sd3pos) / (sd3pos - sd2pos)
                : -3 + (value - sd3neg) / (sd2neg - sd3neg);
    }

    private static double valueAt(Lms lms, double z) {
        return Math.abs(lms.l) < 1e-9
                ? lms.m * Math.exp(lms.s * z)
                : lms.m * Math.pow(1 + lms.l * lms.s * z, 1 / lms.l);
    }

    private static Lms interpolate(TreeMap<Double, Lms> table, double x) {
        Map.Entry<Double, Lms> low = table.floorEntry(x);
        Map.Entry<Double, Lms> high = table.ceilingEntry(x);
        if (low.getKey().equals(high.getKey())) {
            return low.getValue();
        }
        double t = (x - low.getKey()) / (high.getKey() - low.getKey());
        return new Lms(
                low.getValue().l + t * (high.getValue().l - low.getValue().l),
                low.getValue().m + t * (high.getValue().m - low.getValue().m),
                low.getValue().s + t * (high.getValue().s - low.getValue().s));
    }

    private static TreeMap<Double, Lms> read(Resource resource) throws IOException {
        TreeMap<Double, Lms> table = new TreeMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            boolean months = header != null && header.trim().toLowerCase().matches("^\"?(age_?)?months?.*");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] cells = line.split("[,;\t]");
                if (cells.length < 4 || cells[0].isBlank()) {
                    continue;
                }
                double x = Double.parseDouble(cells[0].trim());
                table.put(months ? x * DAYS_PER_MONTH : x, new Lms(Double.parseDouble(cells[1].trim()),
                        Double.parseDouble(cells[2].trim()), Double.parseDouble(cells[3].trim())));
            }
        }
        return table;
    }
}
