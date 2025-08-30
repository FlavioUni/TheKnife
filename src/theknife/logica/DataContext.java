package theknife.logica;

import theknife.csv.GestoreRecensioni;
import theknife.csv.GestoreRistoranti;
import theknife.csv.GestoreUtenti;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;

import java.util.List;
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
	
	private void buildIndici() {
		utentiPerUsername = new HashMap<>();
	}
	
}
