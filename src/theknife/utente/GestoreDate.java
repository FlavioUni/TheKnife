/*Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.utente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class GestoreDate {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private GestoreDate() {}

    /** Parsea una data obbligatoria (mai vuota). Lancia IllegalArgumentException se il formato è sbagliato. */
    public static LocalDate parse(String s) {
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Data mancante. Usa il formato gg/MM/aaaa.");
        try {
            return LocalDate.parse(s, FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato della data non valido. Usa gg/MM/aaaa.");
        }
    }

    /** Parsea una data opzionale: ritorna null se stringa vuota/null; altrimenti valida il formato. */
    public static LocalDate parseNullable(String s) {
        if (s == null || s.isBlank()) return null;
        return parse(s);
    }

    /** Formatta una data opzionale: ritorna "" se null. */
    public static String formatOrEmpty(LocalDate d) {
        return d == null ? "" : d.format(FORMATO);
    }
}
