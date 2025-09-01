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
	
	/**
	 * COSTRUTTORE parametrico della classe Utente
	 * 
	 * @param nome Nome dell'utente
	 * @param cognome Cognome dell'utente
	 * @param username Username dell'utente
	 * @param password Password dell'utente
	 * @param domicilio Domicilio dell'utente
	 * @param data Data di nascita dell'utente
	 * @param ruolo Ruolo dell'utente che può essere {@link theknife.utente.Ruolo#CLIENTE} o {@link theknife.utente.Ruolo#RISTORATORE}
	 * 		CLIENTE può gestire i suoi preferiti, mentre RISTORATORE gestisce i suoi ristoranti
	 * */
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
	
	// GETTER
	public String getNome () {return nome;}	
	public String getCognome () {return cognome;}	
	public String getUsername () {return username;}	
	public String getPassword () {return password;}	
	public String getDomicilio () {return domicilio;}	
	public LocalDate getData () {return data;}	
	public Ruolo getRuolo () {return ruolo;}	
	public List<Ristorante> getRistorantiPreferiti () {return ristorantiPreferiti;}	
	public List<Ristorante> getRistorantiGestiti () {return ristorantiGestiti;}
	
	// SETTER
	public void setNome (String x) {this.nome = x;}	
	public void setCognome (String x) {this.cognome = x;}

	/** 
	 * Aggiunge il ristorante alla lista coerente in base al ruolo (preferiti per CLIENTE, gestiti per RISTORATORE),
	 * evitando i duplicati
	 * 
	 * @param ristorante Ristorante da aggiungere a preferiti o gestiti
	 * @return true se il ristorante viene correttamente aggiunto, altrimenti false se è già presente o se il ruolo non è valido
	 * */
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

	/** 
	 * Rimuove il ristorante dalla lista coerente in base al ruolo (preferiti per CLIENTE, gestiti per RISTORATORE)
	 * 
	 * @param ristorante Ristorante da rimuovere da preferiti o gestiti
	 * @return true se il ristorante viene correttamente rimosso, altrimenti false se non è presente o se il ruolo non è valido
	 * */
	public boolean rimuoviAssoc(Ristorante ristorante) {
	    if (ruolo == Ruolo.CLIENTE) {
	        return ristorantiPreferiti.remove(ristorante);
	    } else if (ruolo == Ruolo.RISTORATORE) {
	        return ristorantiGestiti.remove(ristorante);
	    }
	    return false;
	}

	/** 
	 * Stampa a video {@code System.out} l’elenco delle associazioni coerente con il ruolo (preferiti per CLIENTE, gestiti per RISTORATORE)
	 * In caso di ruolo non valido, stampa a video un messaggio {@code System.err}
	 * */
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
	
	/**
	 * Controllo di permesso per verificare se l'utente RISTORATORE gestisce il ristorante
	 * 
	 * @param ristorante Ristorante di cui bisogna verificare la gestione
	 * @return true se l'utente ha ruolo RISTORATORE e se è presente nella sua lista ristorantiGestiti
	 * */
    public boolean gestisce (Ristorante ristorante) {
        if (ruolo == Ruolo.RISTORATORE) {
            return ristorantiGestiti.contains(ristorante);
        }
        return false;
    }
	
 // --- supporto per persistenza preferiti/gestiti --- 
    /**
     * String grezza che rappresenta l'associazione tra utente e ristoranti in un formato compatto per la persistenza su CSV
     * E' usata solo per l'import/export, nel resto dell’app non usarla: usa le liste vere (preferiti/gestiti), non questa stringa
     * */
    private String assocKeysRaw = "";
    
    /**
     * Restituisce la stringa compatta con le associazioni utente e ristoranti (esclusivamente destinata all'import/export su CSV)
     * 
     * @return assocKeyRaw Stringa nel formato {@code "Nome|Location;Nome|Location;..."}
     * */
    public String getAssocKeysRaw() { return assocKeysRaw; }
    
    /**
     * Imposta la stringa compatta con le associazioni utente e ristoranti (da usare esclusivamente all'import da CSV)
     * Se {@code s} è {@code null}, viene impostata come stringa vuota
     * 
     * @param s Stringa nel formato {@code "Nome|Location;Nome|Location;..."}
     * */
    public void setAssocKeysRaw(String s) { this.assocKeysRaw = (s == null ? "" : s); }
	
    /**
     * Restituisce una rappresentazione leggibile dell'utente che include: nome, cognome, username, ruolo, domicilio
     * e data di nascita (vuota se assente)
     * 
     * @return Stringa formattata con i principali campi dell'utente
     * */
    @Override
    public String toString() {
        return String.format(
            "Utente[%s %s, username=%s, ruolo=%s, domicilio=%s, nascita=%s]",
            nome, cognome, username, ruolo, domicilio,
            theknife.logica.GestoreDate.formatOrEmpty(data)
        );
    }

    /**
     * Definisce quando due utenti sono considerati la stessa persona
     * Criterio: due Utenti sono uguali se hanno lo stesso username (ignorando maiuscole/minuscole) e non sono considerati gli altri campi
     * Lo username è l'identificativo unico dell'utente
     * 
     * @param obj Oggetto con cui confrontare l'Utente
     * @return true se obj è un Utente con lo stesso username, altrimenti false
     * */
	@Override
	public boolean equals (Object obj) {
	    if (this == obj) return true;
	    if (!(obj instanceof Utente other)) return false;
	    return this.username.equalsIgnoreCase(other.username);
	}
	
	/**
	 * Restituisce un codice hash coerente con equals(Object)
	 * L'hash dipende esclusivamente dallo username (ignorando maiscole/minuscole), in quanto euquals è definita esclusivamente su quello
	 * 
	 * @return il codice di hash dello username in minuscolo o 0 se lo username è {@code null}*/
	@Override
	public int hashCode() {
	    return (username == null)
	            ? 0
	            : username.toLowerCase(java.util.Locale.ROOT).hashCode();
	}
}
