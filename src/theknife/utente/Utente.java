/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.utente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import theknife.ristorante.Ristorante;

public class Utente
{
	// campi
	private String nome, cognome, username, password, domicilio;
	private LocalDate data;
	private Ruolo ruolo;
	private List<Ristorante> ristorantiPreferiti;
	private List<Ristorante> ristorantiGestiti;
	
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
		this.ristorantiGestiti = new ArrayList<>();
	}
	
	// metodi getter e setter
	public String getNome () {return nome;}
	
	public String getCognome () {return cognome;}
	
	public String getUsername () {return username;}
	
	public String getPassword () {return password;}
	
	public String getDomicilio () {return domicilio;}
	
	public LocalDate getData () {return data;}
	
	public Ruolo getRuolo () {return ruolo;}
	
	public List<Ristorante> getRistorantiPreferiti () {return ristorantiPreferiti;}
	
	public List<Ristorante> getRistorantiGestiti () {return ristorantiGestiti;}
	// - //
	public void setNome (String x) {this.nome = x;}
	
	public void setCognome (String x) {this.cognome = x;}
	// - //
	public boolean aggiungiPreferito (Ristorante ristorante) {
		if (ruolo == Ruolo.CLIENTE && !ristorantiPreferiti.contains(ristorante)) {
			ristorantiPreferiti.add(ristorante);
			return true;
		}
		return false;
	}
	public boolean rimuoviPreferito (Ristorante ristorante) {
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
				for (Ristorante r : ristorantiPreferiti)
					System.out.println("- " + r.getNome() + " (" + r.getCitta() + ")");
			}
		}
		else {
			System.out.println("Errore: lista dei preferiti disponibile solo per i clienti.");
		}
	}
	public boolean aggiungiRistoranteGestito (Ristorante ristorante) {
		if (ruolo == Ruolo.RISTORATORE && !ristorantiGestiti.contains(ristorante)) {
			ristorantiGestiti.add(ristorante);
			return true;
		}
		return false;
	}
	public boolean rimuoviRistoranteGestito (Ristorante ristorante) {
		if (ruolo == Ruolo.RISTORATORE)
			return ristorantiGestiti.remove(ristorante);
		return false;
	}
	public void visualizzaRistorantiGestiti () {
		if (ruolo == Ruolo.RISTORATORE) {
			if (ristorantiGestiti.isEmpty())
				System.out.println("Nessun ristorante in gestione.");
			else {
				System.out.println("Ristoranti gestiti da " + username + ":");
				for (Ristorante r : ristorantiPreferiti)
					System.out.println("- " + r.getNome() + " (" + r.getCitta() + ")");
			}
		}
		else {
			System.out.println("Errore: solo i ristoratori possono gestire ristoranti.");
		}
	}
}
