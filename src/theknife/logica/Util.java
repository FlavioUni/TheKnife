/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
 */
package theknife.logica;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * La classe Util fornisce un metodo di utilità generale per l'applicazione TheKnife, ovvero l'hashing delle password.
 * <p>
 * Questa classe è progettata per essere utilizzata in modo statico e non viene istanziata.
 * </p>
 * 
 * @author Lorenzo Gasparini
 */

public class Util {
	
	/**
	 * Calcola l'hash SHA-256 di una stringa che rappresenta una password.
	 * Il risultato è restituito come una stringa esadecimale di 64 caratteri (256 bit).
	 * <p>
	 * Questo metodo viene chiamato durante la registrazione di un nuovo utente e durante il login per confrontare la password fornita con quella memorizzata.
	 * È fondamentale per non memorizzare mai password in chiaro.
	 * </p>
	 * 
	 * @param password La stringa da cui calcolare l'hash.
	 * 	Se la stringa è {@code null}, viene trattata come una stringa vuota.
	 * @return La rappresentazione esadecimale (in minuscolo) dell'hash SHA-256 della password.
	 * @throws RuntimeException Se l'algoritmo "SHA-256" non è disponibile nell'ambiente di esecuzione.
	 */
	protected static String hashPassword (String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : hashBytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Algoritmo SHA-256 non disponibile", e);
		}
	}
}
