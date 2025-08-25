/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.recensione;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Recensione {
	
	//campi
	private final String autore;
	private final String nomeRistorante;
	private int stelle;
	private String descrizione;
	private LocalDate data;
	private String risposta = "";

	
	//costruttore
	public Recensione(String autore, String nomeRistorante, int stelle, String descrizione) {
		this.autore = autore;
		this.nomeRistorante = nomeRistorante;
		this.stelle = stelle;
		this.descrizione = descrizione;
		this.data = LocalDate.now();
	}
	//getter e setter
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
	
	public String getNomeRistorante() {
		return nomeRistorante;
	}

	public LocalDate getData() {
		return data;
	}
	
	public String getRisposta() {
	    return risposta;
	}

	public void setRisposta(String risposta) {
	    this.risposta = risposta;
	}
	
	//metodi
	@Override
	public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    return "Ristorante: " + nomeRistorante + "\n" +
	           "Autore: " + autore + " *Stelle*: " + stelle + "\n" +
	           descrizione + "\n" +
	           "Data: " + data.format(formatter) + "\n" +
	           "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    
	    Recensione other = (Recensione) obj;
	    
	    return autore.equals(other.autore) &&
	           nomeRistorante.equals(other.nomeRistorante) &&
	           data.equals(other.data) &&
	           descrizione.equals(other.descrizione) &&
	           stelle == other.stelle;
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(autore, nomeRistorante, data, descrizione, stelle);
	}
	
	public boolean isPositiva() {
		return stelle >= 4;
	}
	
	public boolean isRecente() {
		return data.isAfter(LocalDate.now().minusDays(30));
	}
	
	public void modificaRecensione(int newStelle, String newDescrizione) {
		if (newStelle < 1 || newStelle > 5) {
	        throw new IllegalArgumentException("Le stelle devono essere tra 1 e 5.");
	    }
		this.stelle = newStelle;
		this.descrizione = newDescrizione;
		this.data = LocalDate.now();
	}
	
	public String visualizzaRecensione() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    String base = "Autore: " + autore + " *Stelle*: " + stelle + "\n" +
	                  descrizione + "\n" +
	                  "Data: " + data.format(formatter);

	    if (!risposta.isEmpty()) {
	        base += "\nRisposta del ristoratore: " + risposta;
	    }

	    return base;
	}
	
	public void eliminaRisposta() {
	    this.risposta = "";
	}
	
}