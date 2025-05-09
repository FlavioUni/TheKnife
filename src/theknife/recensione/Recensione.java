/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.recensione;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Recensione {
	
	//campi
	private String autore;
	private int stelle;
	private String descrizione;
	private LocalDate data;
	
	//costruttore
	public Recensione(String autore, int stelle, String descrizione) {
		this.autore = autore;
		this.stelle = stelle;
		this.descrizione = descrizione;
		this.data = LocalDate.now();
	}

	public int getStelle() {
		return stelle;
	}

	public void setStelle(int stelle) {
		this.stelle = stelle;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getAutore() {
		return autore;
	}

	public LocalDate getData() {
		return data;
	}
	
	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/mm/aaaa");
		return "Autore: " + autore +  " *Stelle*: " + stelle + "\n" +
				descrizione + "\n" +
				"Data: " + data.format(formatter);
	}
	
	public boolean isPositiva() {
		return stelle >= 4;
	}
	
	public boolean isRecente() {
		return data.isAfter(LocalDate.now().minusDays(30));
	}
	
}
