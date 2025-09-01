/*Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe di utilità {@code final} e con costruttore privato che fornisce metodi statici per la conversione e la formattazione delle date, 
 * utilizzando un formato coerente ("gg/MM/aaaa") in tutta l'applicazione.
 * <p>
 * Non &egrave; possibile istanziare o estendere questa classe; tutti i metodi sono statici.
 * </p>
 * 
 * @author Lorenzo Gasparini
 * @see LocalDate
 * @see DateTimeFormatter
 */
public final class GestoreDate {
	
	/**
	 * Il formatter di data unico utilizzato da tutti i metodi della classe.
	 * Il formato &egrave; "dd/MM/yyyy".
	 */
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Costruttore privato per impedire di istanziare classe.
     */
    private GestoreDate() {}

    /** Converte una stringa in un oggetto {@link LocalDate}.
     * La stringa non può essere null o vuota e deve rispettare esattamente il formato "gg/MM/aaaa".
     * 
     * @param s La stringa da convertire in data.
     * @return L'oggetto {@link LocalDate} corrispondente alla stringa convertita
     * @throws IllegalArgumentException Se la stringa {@code s} è {@code null}, vuota, o non corrisponde al formato "gg/MM/aaaa".
     */
    public static LocalDate parse(String s) {
        if (s == null || s.isBlank())
            throw new IllegalArgumentException("Data mancante. Usa il formato gg/MM/aaaa.");
        try {
            return LocalDate.parse(s, FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato della data non valido. Usa gg/MM/aaaa.");
        }
    }

    /**
     * Converte una stringa in un oggetto {@link LocalDate}, ma in modo opzionale (nullable).
     * Se la stringa di input è {@code null} o vuota, restituisce {@code null}.
     * Se la stringa contiene valore, delega a {@link #parse(String)} per la validazione del formato.
     * 
     * @param s La stringa da convertire in data, oppure {@code null}/vuota.
     * @return {@code null} se {@code s} è {@code null} o vuota; altrimenti, l'oggetto {@link LocalDate} corrispondente alla stringa convertita
     * @throws IllegalArgumentException Se la stringa {@code s} non è vuota ma non corrisponde al formato "gg/MM/aaaa".
     */
    public static LocalDate parseNullable(String s) {
        if (s == null || s.isBlank()) return null;
        return parse(s);
    }

    /**
     * Formatta una data in una stringa, gestendo il caso di valore {@code null}.
     *  Se la data di input è {@code null}, restituisce una stringa vuota ("").
     *  Se la data è valida, la formatta secondo il formato "gg/MM/aaaa".
     * 
     * @param d La data ({@link LocalDate}) da formattare, oppure {@code null}.
     * @return  Una stringa vuota ("") se {@code d} è {@code null}; altrimenti, la stringa formattata nel formato "gg/MM/aaaa".
     */
    public static String formatOrEmpty(LocalDate d) {
        return d == null ? "" : d.format(FORMATO);
    }
}
