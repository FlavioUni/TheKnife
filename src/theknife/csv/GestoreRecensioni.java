/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.csv;

import java.util.ArrayList;
import theknife.recensione.Recensione;

public class GestoreRecensioni extends GestoreCSV<Recensione> {
	
	//campi
	private final String username;
	private final String nomeRistorante;
	
	private int voto;
	private String commento;
	private String risposta;
	@Override
	public void caricaDaCSV(String filePath) {
	    // TODO: Implementa lettura da CSV
	}

	@Override
	public void salvaSuCSV(String filePath) {
	    // TODO: Implementa scrittura su CSV
	}
	
}