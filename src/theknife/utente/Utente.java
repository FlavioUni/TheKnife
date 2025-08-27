/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Utente
{
	// campi
	private String nome, cognome, username, password, domicilio;
	private LocalDate data;
	private Ruolo ruolo;
	private List<String> ristorantiPreferiti;
	
	// costruttore
	public Utente (String a1, String a2, String a3, String a4, String a5, LocalDate a6, Ruolo a7)
	{
		this.nome = a1;
		this.cognome = a2;
		this.username = a3;
		this.password = a4;
		this.domicilio = a5;
		this.data = a6;
		this.ruolo = a7;
		this.ristorantiPreferiti = new ArrayList<>();
	}
	
	// metodi getter e setter
	public String getNome () {return nome;}
	
	public String getCognome () {return cognome;}
	
	public String getUsername () {return username;}
	
	public String getPassword () {return password;}
	
	public String getDomicilio () {return domicilio;}
	
	public LocalDate getData () {return data;}
	
	public Ruolo getRuolo () {return ruolo;}
	
	public List<String> getRistorantiPreferiti () {return ristorantiPreferiti;}
	// - //
	public void setNome (String x) {this.nome = x;}
	
	public void setCognome (String x) {this.cognome = x;}
	// - //
	public boolean aggiungiPreferito (String ristorante) {
		if (ruolo == Ruolo.CLIENTE && !ristorantiPreferiti.contains(ristorante)) {
			ristorantiPreferiti.add(ristorante);
			return true;
		}
		return false;
	}
	public boolean rimuoviPreferito (String ristorante) {
		if (ruolo == Ruolo.CLIENTE)
			return ristorantiPreferiti.remove(ristorante);
		return false;
	}
	public void visualizzaPreferiti () {
		if (ruolo == Ruolo.CLIENTE) {
			if (ristorantiPreferiti.isEmpty())
				System.out.println("Lista dei preferiti vuota.");
			else {
				System.out.println("Ristoranti preferiti di " + username + ":");
				for (String r : ristorantiPreferiti)
					System.out.println("- " + r);
			}
		}
		else {
			System.out.println("Errore: lista dei preferiti disponibile solo per i clienti.");
		}
	}
}
