/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe di utilità con metodi statici usati nell’app.
 * Attualmente serve solo per hashare le password con SHA-256.
 * Non è istanziabile.
 * 
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 * @author Gasparini Lorenzo
 */
public final class Util {

    // Costruttore privato per evitare istanze
    private Util() {}

    /**
     * Calcola l’hash SHA-256 di una password e lo restituisce in esadecimale (minuscolo).
     * Se la password è null, viene trattata come stringa vuota.
     *
     * @param password La password in chiaro
     * @return L’hash SHA-256 come stringa di 64 caratteri esadecimali
     * @throws RuntimeException Se l’algoritmo SHA-256 non è disponibile (molto raro)
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest((password == null ? "" : password).getBytes());
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritmo SHA-256 non disponibile", e);
        }
    }
}