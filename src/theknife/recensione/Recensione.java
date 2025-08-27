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
	private final String username;
	private final String nomeRistorante;
	
	private int stelle;
	private String commento;
	private LocalDate data;
	private String risposta;

	
	//costruttore in esecuzione (quando viene creata una nuova recensione)
	public Recensione(String username, String nomeRistorante, int stelle, String commento) {
		this.username = username;
		this.nomeRistorante = nomeRistorante;
		this.stelle = stelle;
		this.commento = commento;
		this.data = LocalDate.now();
		this.risposta = "";
	}
	
	//costruttore per gestoreRecensioni (quando le recensioni vengono caricate dal CSV)
	public Recensione(String username, String nomeRistorante, int stelle, String commento, LocalDate data, String risposta) {
		this.username = username;
		this.nomeRistorante = nomeRistorante;
		this.stelle = stelle;
		this.commento = commento;
		this.data = data;
		this.risposta = risposta;
	}
	//getter e setter
	public int getStelle() {
		return stelle;
	}

	public void setStelle(int stelle) {
		this.stelle = stelle;
	}

	public String getDescrizione() {
		return commento;
	}

	public void setDescrizione(String descrizione) {
		this.commento = descrizione;
	}

	public String getAutore() {
		return username;
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
	           "Autore: " + username + " *Stelle*: " + stelle + "\n" +
	           commento + "\n" +
	           "Data: " + data.format(formatter) + "\n" +
	           "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    
	    Recensione other = (Recensione) obj;
	    
	    return username.equals(other.username) &&
	           nomeRistorante.equals(other.nomeRistorante) &&
	           data.equals(other.data) &&
	           commento.equals(other.commento) &&
	           stelle == other.stelle;
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(username, nomeRistorante, data, commento, stelle);
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
		this.commento = newDescrizione;
		this.data = LocalDate.now();
	}
	
	public String visualizzaRecensione() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    String base = "Autore: " + username + " *Stelle*: " + stelle + "\n" +
	                  commento + "\n" +
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