package theknife.logica;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DataContext {
	
	//campi
	private final GestoreUtenti gestoreUtenti = new GestoreUtenti();
	private final GestoreRistoranti gestoreRistoranti = new GestoreRistoranti();
	private final GestoreRecensioni gestoreRecensioni = new GestoreRecensioni();
	
	private List<Utente> utenti;
	private List<Ristorante> ristoranti;
	private List<Recensione> recensioni;
	
	// indici/dizionari per ricerche rapide
	private Map<String, Utente> utentiPerUsername;
	private Map<String, Ristorante> ristorantePerKey;
	private Map<String, List<Recensione>> recensioniPerRistoKey;

	//normalizza stringhe
	private String norm(String s) {
		if(s == null)
			return "";
		String t = s.trim().replaceAll("\\s+"," ");
		return t.toLowerCase();
	}
	
	//key ristorante (nome | location normalizzati)
	private String ristoKey(String nome, String location) {
		return norm(nome) + "|" + norm(location);
	}
	
	public void loadAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
	    // leggi i file
	    gestoreUtenti.caricaDaCSV(utentiCsv);
	    gestoreRistoranti.caricaDaCSV(ristorantiCsv);
	    gestoreRecensioni.caricaDaCSV(recensioniCsv);

	    // prende le liste vive dai gestori
	    utenti = gestoreUtenti.getElementi();
	    ristoranti = gestoreRistoranti.getElementi();
	    recensioni = gestoreRecensioni.getElementi();

	    // evita liste null
	    if (utenti == null) utenti = new ArrayList<>();
	    if (ristoranti == null) ristoranti = new ArrayList<>();
	    if (recensioni == null) recensioni = new ArrayList<>();

	    // prepara indici e collega
	    buildIndici();
	    recensioniRistorante();
	    linkAssocUtenti();
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
		for (Ristorante r : ristoranti) {
		    r.getRecensioni().clear();  // svuota le liste recensioni di tutti i ristoranti
		}
		for(Recensione rec : recensioni) {
			String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());
			Ristorante r = ristorantePerKey.get(key); //cerco nell'hashmap il ristorante r tramite la key
			if (r != null) 
				r.aggiungiRecensione(rec);
			else
				orfane++;
		}
		//if (orfane > 0)
			//System.err.println("Recensioni orfane: " + orfane);
	}
	
	// metodi getter
	public List<Utente> getUtenti() {return utenti;}
	public List<Ristorante> getRistoranti() {return ristoranti;}
	public List<Recensione> getRecensioni() {return recensioni;}
	
	// ricerche rapide
	public Utente findUtente(String username) {
	    if (username == null) return null;
	    return utentiPerUsername.get(username.trim().toLowerCase());
	}

	public Ristorante findRistorante(String nome, String location) {
	    return ristorantePerKey.get(ristoKey(nome, location));
	}
	
	// aggiunta recensione
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
	
	// rimozione recensione (allinea RAM + indici)
	public boolean removeRecensione(Recensione rec) {
	    if (rec == null) return false;

	    String key = ristoKey(rec.getNomeRistorante(), rec.getLocationRistorante());

	    // rimuovi dalla lista globale
	    boolean removed = recensioni.remove(rec);

	    // rimuovi dall'oggetto Ristorante
	    Ristorante r = ristorantePerKey.get(key);
	    if (r != null) {
	        r.getRecensioni().remove(rec);
	    }

	    // rimuovi dall'indice "recensioniPerRistoKey"
	    List<Recensione> lista = recensioniPerRistoKey.get(key);
	    if (lista != null) {
	        lista.remove(rec);
	        if (lista.isEmpty()) {
	            recensioniPerRistoKey.remove(key); // opzionale
	        }
	    }
	    return removed;
	}
	
	// aggiunta utente
	public boolean addUtente(Utente u) {
		if (u == null) return false;

		String user = (u.getUsername() == null) ? "" : u.getUsername().trim().toLowerCase();
		if (user.isEmpty()) {
			System.err.println("Impossibile aggiungere utente: username vuoto.");
			return false;
		}

		if (utentiPerUsername == null) 
			buildIndici(); 
		if (utentiPerUsername.containsKey(user)) {
			System.err.println("Utente già esistente: " + user);
			return false;
		}

		utenti.add(u);
		utentiPerUsername.put(user, u);
		return true;
	}
	
	// aggiunta ristorante 

public boolean addRistorante(Ristorante r) {
	if (r == null) return false;

	String key = ristoKey(r.getNome(), r.getLocation());
	if (key.equals("|")) {
		System.err.println("Impossibile aggiungere ristorante: nome/location mancanti.");
		return false;
	}

	if (ristorantePerKey == null) 
		buildIndici();
	if (ristorantePerKey.containsKey(key)) {
		System.err.println("Ristorante già presente: " + r.getNome() + " | " + r.getLocation());
		return false;
	}

	ristoranti.add(r);
	ristorantePerKey.put(key, r);

	if (recensioniPerRistoKey != null) {
		List<Recensione> pendenti = recensioniPerRistoKey.get(key);
		if (pendenti != null) {
			for (Recensione rec : pendenti) r.aggiungiRecensione(rec);
		}
	}
	return true;
}

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

            u.aggiungiAssoc(r);
        }
    }
}
	
	public void saveAll(String utentiCsv, String ristorantiCsv, String recensioniCsv) {
	    gestoreUtenti.salvaSuCSV(utentiCsv);
	    gestoreRistoranti.salvaSuCSV(ristorantiCsv);
	    gestoreRecensioni.salvaSuCSV(recensioniCsv);
	}
}
