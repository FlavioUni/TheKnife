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

/**
 * La classe Utente rappresenta un utente del sistema.
 * Un utente è caratterizzato da: dati anagrafici, password, ruolo (CLIENTE o RISTORATORE) e la lista dei ristoranti preferiti o gestiti.*/

public class Utente
{
	// campi
	private String nome, cognome, username, password, domicilio;
	private LocalDate data;
	private Ruolo ruolo;
	private List<Ristorante> ristorantiPreferiti;
	private List<Ristorante> ristorantiGestiti;
	
	/**COSTRUTTORE parametrico della classe Utente
	 * @param nome Nome dell'utente
	 * @param cognome Cognome dell'utente
	 * @param username Username dell'utente
	 * @param password Password dell'utente
	 * @param domicilio Domicilio dell'utente
	 * @param data Data di nascita dell'utente
	 * @param ruolo Ruolo dell'utente che può essere {@link theknife.utente.Ruolo#CLIENTE} o {@link theknife.utente.Ruolo#RISTORATORE}
	 * 		CLIENTE può gestire i suoi preferiti, mentre RISTORATORE gestisce i suoi ristoranti*/
	public Utente (String nome, String cognome, String username, String password, String domicilio, LocalDate data, Ruolo ruolo)
	{
		this.nome = nome;
		this.cognome = cognome;
		this.username = username;
		this.password = password;
		this.domicilio = domicilio;
		this.data = data;
		this.ruolo = ruolo;
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
	
	public void setNome (String x) {this.nome = x;}	
	public void setCognome (String x) {this.cognome = x;}
	
	
	// --- API UNIFICATA ---

	/** Aggiunge l'associazione coerente col ruolo:
	 *  - CLIENTE  -> in ristorantiPreferiti
	 *  - RISTORATORE -> in ristorantiGestiti
	 */
	public boolean aggiungiAssoc(Ristorante ristorante) {
	    if (ruolo == Ruolo.CLIENTE) {
	        if (!ristorantiPreferiti.contains(ristorante)) {
	            ristorantiPreferiti.add(ristorante);
	            return true;
	        }
	        return false;
	    } else if (ruolo == Ruolo.RISTORATORE) {
	        if (!ristorantiGestiti.contains(ristorante)) {
	            ristorantiGestiti.add(ristorante);
	            return true;
	        }
	        return false;
	    }
	    return false;
	}

	/** Rimuove l'associazione coerente col ruolo. */
	public boolean rimuoviAssoc(Ristorante ristorante) {
	    if (ruolo == Ruolo.CLIENTE) {
	        return ristorantiPreferiti.remove(ristorante);
	    } else if (ruolo == Ruolo.RISTORATORE) {
	        return ristorantiGestiti.remove(ristorante);
	    }
	    return false;
	}

	/** Stampa a video l’elenco coerente col ruolo. */
	public void visualizzaAssoc() {
	    if (ruolo == Ruolo.CLIENTE) {
	        if (ristorantiPreferiti.isEmpty()) {
	            System.out.println("Lista dei preferiti vuota.");
	        } else {
	            System.out.println("Ristoranti preferiti di " + username + ":");
	            for (Ristorante r : ristorantiPreferiti)
	                System.out.println("- " + r.getNome() + " (" + r.getLocation() + ")");
	        }
	    } else if (ruolo == Ruolo.RISTORATORE) {
	        if (ristorantiGestiti.isEmpty()) {
	            System.out.println("Nessun ristorante in gestione.");
	        } else {
	            System.out.println("Ristoranti gestiti da " + username + ":");
	            for (Ristorante r : ristorantiGestiti)
	                System.out.println("- " + r.getNome() + " (" + r.getLocation() + ")");
	        }
	    } else {
	        System.err.println("Ruolo non supportato.");
	    }
	}
	
	// --- Vecchi metodi -> delega all’API unificata (retro-compatibilità) ---
	public boolean aggiungiPreferito(Ristorante ristorante) { return aggiungiAssoc(ristorante); }
	public boolean rimuoviPreferito(Ristorante ristorante)   { return rimuoviAssoc(ristorante); }
	public void visualizzaPreferiti()                        { visualizzaAssoc(); }

	public boolean aggiungiRistoranteGestito(Ristorante ristorante) { return aggiungiAssoc(ristorante); }
	public boolean rimuoviRistoranteGestito(Ristorante ristorante)  { return rimuoviAssoc(ristorante); }
	public void visualizzaRistorantiGestiti()                       { visualizzaAssoc(); }
	
	//Metodo per la classe RecensioneService. Ritorno true/false se il ristorante è gestito dall'utente
    public boolean gestisce (Ristorante ristorante) {
        if (ruolo == Ruolo.RISTORATORE) {
            return ristorantiGestiti.contains(ristorante);
        }
        return false;
    }
	
 // --- supporto per persistenza preferiti/gestiti --- 
    private String assocKeysRaw = "";

    public String getAssocKeysRaw() { return assocKeysRaw; }
    public void setAssocKeysRaw(String s) { this.assocKeysRaw = (s == null ? "" : s); }
	
    @Override
    public String toString() {
        return String.format(
            "Utente[%s %s, username=%s, ruolo=%s, domicilio=%s, nascita=%s]",
            nome, cognome, username, ruolo, domicilio,
            theknife.logica.GestoreDate.formatOrEmpty(data)
        );
    }

	@Override
	public boolean equals (Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof Utente other)) return false;
	    return this.username.equalsIgnoreCase(other.username);
	}

	@Override
	public int hashCode () {
	    return username.toLowerCase().hashCode();
	}
}
