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
		if (orfane > 0)
			System.err.println("Recensioni orfane: " + orfane);
	}
	
}
