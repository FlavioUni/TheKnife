/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.ristorante;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Ristorante {

	//ATTRIBUTI
	private String nome;
	private String indirizzo;
	private String location;
	private double prezzo;
	private String cucina;
	private double longitudine;
	private double latitudine;
	private String numeroTelefono;
	private String websiteUrl;
	private String premi;
	private String servizi;
	private boolean prenotazioneOnline;
	private boolean delivery;
	private String descrizione;
	private ArrayList<Recensione> ListaRecensioni;
	
	
	//COSTRUTTORE
	public Ristorante(String nome, String indirizzo, String location, double prezzo, String cucina, double longitudine,
					  double latitudine, String numeroTelefono, String websiteUrl, String premi, String servizi, boolean prenotazioneOnline,
					  boolean delivery, String descrizione){
		
		this.nome = nome;
        this.indirizzo = indirizzo;
        this.location = location;
        this.prezzo = prezzo;
        this.cucina = cucina;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.numeroTelefono = numeroTelefono;
        this.websiteUrl = websiteUrl;
        this.premi = premi;
        this.servizi = servizi;
        this.prenotazioneOnline = prenotazioneOnline;
        this.delivery = delivery;
        this.descrizione = descrizione;
        this.ListaRecensioni = new ArrayList<>();
	}
	
	//GETTER
	public String getNome() { 
		return nome; 
	}
	
    public String getIndirizzo() {
    	return indirizzo;
    }
    
    public String getLocation() {
    	return location; 
    }
    
    public double getPrezzo() { 
    	return prezzo; 
    }
    
    public String getCucina() {
    	return cucina; 
    }
    
    public double getLongitudine() { 
    	return longitudine;
    }
    
    public double getLatitudine() { 
    	return latitudine; 
    }
    
    public String getNumeroTelefono() { 
    	return numeroTelefono; 
    }
    
    public String getWebsiteUrl() { 
    	return websiteUrl; 
    }
    
    public String getPremi() { 
    	return premi; 
    }
    
    public String getServizi() {
    	return servizi;
    }
    
    public boolean isPrenotazioneOnline() { 
    	return prenotazioneOnline; 
    }
    
    public boolean isDelivery() { 
    	return delivery; 
    }
    
    public String getDescrizione() { 
    	return descrizione; 
    }
    
    public ArrayList<Recensione> getRecensioni(){
    	return ListaRecensioni;
    }
    
    public Recensione getRecensione(int index){
    	return ListaRecensioni.get(index);
    	}

	
    
    //SETTER
    
    public void setNome(String nome){
    	this.nome = nome;
    }
    
    public void setIndirizzo(String indirizzo){
    	this.indirizzo = indirizzo;
    }
    
    public void setLocation(String location){
    	this.location = location;
    }
    
    public void setPrezzo(String prezzo){
    	this.prezzo = prezzo;
    }
    
    public void setCucina(String cucina){
    	this.cucina = cucina;
    }
    
    public void setLongitudine(String longitudine){
    	this.longitudine = longitudine;
    }
    
    public void setLatitudine(String latitudine){
    	this.latitudine = latitudine;
    }
    
    public void setNumeroTelefono(String numeroTelefono){
    	this.numeroTelefono = numeroTelefono;
    }
    
    public void setWebsiteUrl(String websiteUrl){
    	this.websiteUrl = websiteUrl;
    }
    
    public void setPremi(String premi){
    	this.premi = premi;
    }
    
    public void setServizi(String servizi){
    	this.servizi = servizi;
    }
    
    public void setPrenotazioneOnline(String prenotazioneOnline){
    	this.prenotazioneOnline = prenotazioneOnline;
    }
    
    public void setDelivery(String delivery){
    	this.delivery = delivery;
    }
    
    public void setDescrizione(String descrizione){
    	this.descrizione = descrizione;
    }
    
    
    //Carica le recensioni da file
    public void caricaRecensione(String Path){
    	this.ListaRecensioni = leggiDaFile(Path);
    }
    
    
    
    @Override
    public String toString() {
        return String.format("%s - %s, %s (%s)\nTipo cucina: %s\nFascia prezzo: %.2f€\nStelle: %d ⭐",
                nome, indirizzo, citta, nazione, tipoCucina, fasciaPrezzo, stelle);
    }
    
    
    public String toCSV() {
        return String.join(";",
                nome, indirizzo, citta, nazione,
                String.valueOf(latitudine), String.valueOf(longitudine),
                String.valueOf(fasciaPrezzo), String.valueOf(delivery),
                String.valueOf(prenotazioneOnline), tipoCucina, numeroTelefono,
                websiteUrl, String.valueOf(premi), String.valueOf(stelle), descrizione);
    }
    
    public boolean match(String tipoCucinaFiltro, String cittaFiltro, double prezzoMax) {
        return (tipoCucinaFiltro == null || tipoCucina.equalsIgnoreCase(tipoCucinaFiltro)) &&
               (cittaFiltro == null || citta.equalsIgnoreCase(cittaFiltro)) &&
               fasciaPrezzo <= prezzoMax;
    }
    
    
    //Metodo per la lista dei ristoranti gestiti
    public Ristorante trovaRistorante(String nome) {
        for (Ristorante r : ristoranti) {
            if (r.getNome().equalsIgnoreCase(nome)) return r;
        }
        return null;
    }
    
    
}
