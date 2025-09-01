/*Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;
import theknife.utente.Ruolo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DataContext {
	
	// ======= GESTORI CSV =======
	private final GestoreUtenti gestoreUtenti = new GestoreUtenti();
	private final GestoreRistoranti gestoreRistoranti = new GestoreRistoranti();
	private final GestoreRecensioni gestoreRecensioni = new GestoreRecensioni();
	
	// ======= LISTE IN RAM =======
	private List<Utente> utenti;
	private List<Ristorante> ristoranti;
	private List<Recensione> recensioni;
	
	// ======= INDICI RAPIDI =======
	private Map<String, Utente> utentiPerUsername;
	private Map<String, Ristorante> ristorantePerKey;
	private Map<String, List<Recensione>> recensioniPerRistoKey;

	// ------- normalizza stringhe -------
	private String norm(String s) {
		if (s == null) return "";
		String t = s.trim().replaceAll("\\s+", " ");
		return t.toLowerCase();
	}
	
	// ------- key ristorante (nome|location) -------
	private String ristoKey(String nome, String location) {
		return norm(nome) + "|" + norm(location);
	}
	
	// ======= LOAD TUTTO =======
	public void loadAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
	    // 1) leggi i file
	    gestoreUtenti.caricaDaCSV(utentiCsv);
	    gestoreRistoranti.caricaDaCSV(ristorantiCsv);
	    gestoreRecensioni.caricaDaCSV(recensioniCsv);

	    // 2) prendi le liste vive dai gestori
	    utenti = gestoreUtenti.getElementi();
	    ristoranti = gestoreRistoranti.getElementi();
	    recensioni = gestoreRecensioni.getElementi();

	    // 3) evita liste null
	    if (utenti == null) utenti = new ArrayList<>();
	    if (ristoranti == null) ristoranti = new ArrayList<>();
	    if (recensioni == null) recensioni = new ArrayList<>();

	    // 4) prepara indici e collega dati
	    buildIndici();
	    recensioniRistorante();
	    linkAssocUtenti();   // importa preferiti/gestiti da assocKeysRaw -> liste vere
	}
	
	private void buildIndici() {
		utentiPerUsername = new HashMap<>();
		ristorantePerKey = new HashMap<>();
		recensioniPerRistoKey = new HashMap<>();
		
		for (Utente u : utenti) {
		    String user = (u.getUsername() == null) ? "" : u.getUsername().trim();
		    if (user.isEmpty()) {
		        System.err.println("Utente senza username.");
		        continue;
		    }
		    utentiPerUsername.put(user.toLowerCase(), u);
		}
		
		for (Ristorante r : ristoranti) {
			String key = ristoKey(r.getNome(), r.getLocation());
			if (ristorantePerKey.containsKey(key)) {
				System.err.println("Ristorante duplicato: " + r.getNome() + "|" + r.getLocation());
				continue;
			}
			ristorantePerKey.put(key, r);
		}
		
		for (Recensione rec : recensioni) {
			String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());
			List<Recensione> lista = recensioniPerRistoKey.get(key);
			if (lista == null) {
				lista = new ArrayList<>();
				recensioniPerRistoKey.put(key, lista);
			}
			lista.add(rec);
		}
	}
	
	private void recensioniRistorante() {
		int orfane = 0;
		// svuota le liste recensioni in ogni ristorante
		for (Ristorante r : ristoranti) r.getRecensioni().clear();
		
		// ricollega ogni recensione al suo ristorante
		for (Recensione rec : recensioni) {
			String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());
			Ristorante r = ristorantePerKey.get(key);
			if (r != null) {
				r.aggiungiRecensione(rec);
			} else {
				orfane++;
			}
		}
		// if (orfane > 0) System.err.println("Recensioni orfane: " + orfane);
	}
	
	// ======= GETTER =======
	public List<Utente> getUtenti() { return utenti; }
	public List<Ristorante> getRistoranti() { return ristoranti; }
	public List<Recensione> getRecensioni() { return recensioni; }
	
	// ======= RICERCHE =======
	public Utente findUtente(String username) {
	    if (username == null) return null;
	    return utentiPerUsername.get(username.trim().toLowerCase());
	}

	public Ristorante findRistorante(String nome, String location) {
	    return ristorantePerKey.get(ristoKey(nome, location));
	}
	
	// ======= RECENSIONI: ADD/REMOVE =======
	public boolean addRecensione(Recensione rec) {
	    if (rec == null) return false;

	    Ristorante r = findRistorante(rec.getNomeRistorante(), rec.getLocationRistorante());
	    if (r == null) {
	        System.err.println("Ristorante non trovato per recensione: "
	                + rec.getNomeRistorante() + " | " + rec.getLocationRistorante());
	        return false; // blocca orfane
	    }

	    recensioni.add(rec);
	    r.aggiungiRecensione(rec);

	    String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());
	    List<Recensione> lista = recensioniPerRistoKey.get(key);
	    if (lista == null) {
	        lista = new ArrayList<>();
	        recensioniPerRistoKey.put(key, lista);
	    }
	    lista.add(rec);

	    return true;
	}
	
	public boolean removeRecensione(Recensione rec) {
	    if (rec == null) return false;

	    String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());

	    // rimuovi dalla lista globale
	    boolean removed = recensioni.remove(rec);

	    // rimuovi dall'oggetto Ristorante
	    Ristorante r = ristorantePerKey.get(key);
	    if (r != null) r.getRecensioni().remove(rec);

	    // rimuovi dall'indice "recensioniPerRistoKey"
	    List<Recensione> lista = recensioniPerRistoKey.get(key);
	    if (lista != null) {
	        lista.remove(rec);
	        if (lista.isEmpty()) recensioniPerRistoKey.remove(key);
	    }
	    return removed;
	}
	
	// ======= UTENTI/RISTORANTI: ADD =======
	public boolean addUtente(Utente u) {
		if (u == null) return false;

		String user = (u.getUsername() == null) ? "" : u.getUsername().trim().toLowerCase();
		if (user.isEmpty()) {
			System.err.println("Impossibile aggiungere utente: username vuoto.");
			return false;
		}

		if (utentiPerUsername == null) buildIndici(); 
		if (utentiPerUsername.containsKey(user)) {
			System.err.println("Utente già esistente: " + user);
			return false;
		}

		utenti.add(u);
		utentiPerUsername.put(user, u);
		return true;
	}
	
	public boolean addRistorante(Ristorante r) {
		if (r == null) return false;

		String key = ristoKey(r.getNome(), r.getLocation());
		if (key.equals("|")) {
			System.err.println("Impossibile aggiungere ristorante: nome/location mancanti.");
			return false;
		}

		if (ristorantePerKey == null) buildIndici();
		if (ristorantePerKey.containsKey(key)) {
			System.err.println("Ristorante già presente: " + r.getNome() + " | " + r.getLocation());
			return false;
		}

		ristoranti.add(r);
		ristorantePerKey.put(key, r);

		// collega eventuali recensioni già caricate con stessa chiave
		if (recensioniPerRistoKey != null) {
			List<Recensione> pendenti = recensioniPerRistoKey.get(key);
			if (pendenti != null) {
				for (Recensione rec : pendenti) r.aggiungiRecensione(rec);
			}
		}
		return true;
	}

	// ======= IMPORT ASSOCIAZIONI (CSV -> liste) =======
	private void linkAssocUtenti() {
	    for (Utente u : utenti) {
	        String raw = u.getAssocKeysRaw();
	        if (raw == null || raw.trim().isEmpty()) continue;

	        String[] tokens = raw.split(";");
	        for (String tok : tokens) {
	            tok = tok.trim();
	            if (tok.isEmpty()) continue;

	            String[] parts = tok.split("\\|", 2); // split una sola volta
	            String nome = parts[0];
	            String loc  = (parts.length > 1) ? parts[1] : "";

	            Ristorante r = findRistorante(nome, loc);
	            if (r == null) {
	                System.err.println("Associazione utente non risolta: " + tok);
	                continue;
	            }

	            // aggiunge alla lista coerente col ruolo (preferiti/gestiti)
	            u.aggiungiAssoc(r);
	        }
	        // azzera SOLO alla fine, dopo aver importato tutte le associazioni
	        u.setAssocKeysRaw("");
	    }
	}

	// ======= EXPORT ASSOCIAZIONI (liste -> CSV) =======
	private String buildAssocKeysRaw(Utente u) {
	    // Prende la lista coerente col ruolo
	    List<Ristorante> src;
	    try {
	        src = (u.getRuolo() == Ruolo.CLIENTE)
	                ? u.getRistorantiPreferiti()
	                : u.getRistorantiGestiti();
	    } catch (Throwable t) {
	        src = new ArrayList<>();
	    }

	    List<String> tokens = new ArrayList<>();
	    if (src != null) {
	        for (Ristorante r : src) {
	            if (r == null) continue;
	            String nome = (r.getNome() == null) ? "" : r.getNome().trim();
	            String loc  = (r.getLocation() == null) ? "" : r.getLocation().trim();
	            if (!nome.isEmpty() || !loc.isEmpty()) {
	                tokens.add(nome + "|" + loc);
	            }
	        }
	    }
	    return String.join(";", tokens);
	}
	
	// ======= SAVE TUTTO =======
	public void saveAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
	    // 1) Prima di salvare, ricostruisci la stringa compatta dalle liste vere
	    for (Utente u : utenti) {
	        u.setAssocKeysRaw(buildAssocKeysRaw(u));
	    }

	    // 2) Salva i CSV
	    gestoreUtenti.salvaSuCSV(utentiCsv);
	    gestoreRistoranti.salvaSuCSV(ristorantiCsv);
	    gestoreRecensioni.salvaSuCSV(recensioniCsv);
	}
}