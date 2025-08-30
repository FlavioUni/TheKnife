package theknife.eccezioni;

public class InputAnnullatoException extends RuntimeException {
	public InputAnnullatoException() {
        super("Input annullato dall'utente.");
	}
}
// Crea un'istanza dell'eccezione con il messaggio "Input annullato dall'utente".
