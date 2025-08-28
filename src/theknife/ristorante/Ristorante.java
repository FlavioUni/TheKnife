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
	private Double mediaStelle;
	
	
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
        this.mediaStelle = mediaStelle();
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
    
    public void setPrezzo(Double prezzo){
        if (prezzo < 0) throw new IllegalArgumentException("Il prezzo non può essere negativo");
    	this.prezzo = prezzo;
    }
    
    public void setCucina(String cucina){
    	this.cucina = cucina;
    }
    
    public void setLongitudine(Double longitudine){
    	this.longitudine = longitudine;
    }
    
    public void setLatitudine(Double latitudine){
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
    
    public void setPrenotazioneOnline(Boolean prenotazioneOnline){
    	this.prenotazioneOnline = prenotazioneOnline;
    }
    
    public void setDelivery(Boolean delivery){
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
        return "Ristorante{" +
               "nome='" + nome + '\'' +
               ", indirizzo='" + indirizzo + '\'' +
               ", location='" + location + '\'' +
               ", prezzo=" + prezzo +
               ", cucina='" + cucina + '\'' +
               ", longitudine=" + longitudine +
               ", latitudine=" + latitudine +
               ", numeroTelefono='" + numeroTelefono + '\'' +
               ", websiteUrl='" + websiteUrl + '\'' +
               ", premi='" + premi + '\'' +
               ", servizi='" + servizi + '\'' +
               ", prenotazioneOnline=" + prenotazioneOnline +
               ", delivery=" + delivery +
               ", descrizione='" + descrizione;
    }

    
    //metodi per aggiungere o rimuovere recensioni dalla lista
    public void aggiungiRecensione(Recensione recensione) {
        ListaRecensioni.add(recensione);
    }

    public void rimuoviRecensione(Recensione recensione) {
        ListaRecensioni.remove(recensione);
    }
    
    
    //Metodo per la lista dei ristoranti gestiti
    public Ristorante trovaRistorante(String nome) {
        for (Ristorante r : ristoranti) {
            if (r.getNome().equalsIgnoreCase(nome)) return r;
        }
        return null;
    }
    
    
    //Restituisce una stringa contenente tutte le recensioni del ristorante
    public String getRecensioniString(){
        String s = "";
        for(Recensione r : ListaRecensioni)
            s += r.visualizzaRecensione() + "\n";
        return s;
    }
    
    
    //Rimuove una recensione in base all'username e commento corrispondente
    public void RimuoviRecensione(String username, String commento){
        for(int i = ListaRecensioni.size() - 1; i >= 0; i--) {
            Recensione r = ListaRecensioni.get(i);
            if(r.getUsername().equals(username) && r.getCommento().equals(commento)) {
                ListaRecensioni.remove(i);
            }
        }
    }
    
    
    //Modifica una recensione esistente con lo stesso username, commento e voto
    public void ModificaRecensione(String username, String commento, int voto){
        int i=0;
        for(Recensione r : ListaRecensioni){
            if(r.getUsername().equals(username) && r.getCommento().equals(commento) && r.getVoto() == voto){
                ListaRecensioni.get(i).setCommento(commento);
                ListaRecensioni.get(i).setVoto(voto);
            }
            i++;
        }
    }

    
    //Aggiunge una recensione alla lista del ristorante
    public void AggiungiRecensione(Recensione recensione){
        ListaRecensioni.add(recensione);
        ContaRecensioni++;
    }


    
    public String toCSV() {
        return nome + ";" + indirizzo + ";" + location + ";" + prezzo + ";" + cucina + ";" +
               longitudine + ";" + latitudine + ";" + numeroTelefono + ";" + websiteUrl + ";" +
               premi + ";" + servizi + ";" + prenotazioneOnline + ";" + delivery + ";" + descrizione;
    }
    
    
}
