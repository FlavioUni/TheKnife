/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GestoreDate {
	private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/mm/yyyy");
	
	public static LocalDate parse(String dataStringa) throws IllegalArgumentException {
		try {
			return LocalDate.parse(dataStringa, FORMATO);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Formato della data non valido. Usa gg/mm/aaaa.");
		}
	}
	public static String format(LocalDate data) {
		return data.format(FORMATO);
	}
}
