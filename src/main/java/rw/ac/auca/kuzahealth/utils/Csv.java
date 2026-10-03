package rw.ac.auca.kuzahealth.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Minimal CSV writer (RFC 4180 quoting).
 */
public final class Csv {

    /** Plain numbers and phone numbers such as +250780000000 are safe to leave as they are. */
    private static final Pattern NUMERIC = Pattern.compile("[+-]?[0-9][0-9 .,()-]*");

    private final StringBuilder out = new StringBuilder();

    public Csv header(String... columns) {
        return row((Object[]) columns);
    }

    public Csv row(Object... cells) {
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) {
                out.append(',');
            }
            out.append(escape(cells[i]));
        }
        out.append("\r\n");
        return this;
    }

    public Csv rows(List<Object[]> rows) {
        rows.forEach(this::row);
        return this;
    }

    @Override
    public String toString() {
        return out.toString();
    }

    private static String escape(Object cell) {
        if (cell == null) {
            return "";
        }
        String text = cell.toString();
        // Stop spreadsheet programs from running a cell that starts like a formula
        if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0 && !NUMERIC.matcher(text).matches()) {
            text = "'" + text;
        }
        if (text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r")) {
            return '"' + text.replace("\"", "\"\"") + '"';
        }
        return text;
    }
}
