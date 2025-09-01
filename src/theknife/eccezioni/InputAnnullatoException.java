/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
 */
package theknife.eccezioni;

/**
 * Eccezione non controllata che segnala l'intenzione dell'utente di annullare un'operazione di input.
 * <p>
 * Estende {@link RuntimeException} in quanto rappresenta una condizione anomala ma prevista, non un errore di programmazione.
 * La sua gestione permette di uscire da un menu o da una richiesta di input senza propagare errori generici.
 * </p>
 * 
 * @author Lorenzo Gasparini
 * @see RuntimeException
 */
public class InputAnnullatoException extends RuntimeException {
	
	/**
	 * Costruisce una nuova {@code InputAnnullatoException} con il messaggio di default: "Input annullato dall'utente.".
	 */
	public InputAnnullatoException() {
        super("Input annullato dall'utente.");
	}
}
// Crea un'istanza dell'eccezione con il messaggio "Input annullato dall'utente".
