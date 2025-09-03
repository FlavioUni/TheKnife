/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.eccezioni;

/**
 * Eccezione non controllata che segnala l'intenzione dell'utente di annullare un'operazione di input.
 * Estende RuntimeException perché rappresenta un caso previsto e gestibile nei flussi del menu.
 * Viene usata per interrompere la lettura input e tornare al menu precedente senza errori generici.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class InputAnnullatoException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;
	
    /**
     * Costruisce una nuova eccezione con il messaggio di default:
     * "Input annullato dall'utente."
     */
    public InputAnnullatoException() {
        super("Input annullato dall'utente.");
    }
}