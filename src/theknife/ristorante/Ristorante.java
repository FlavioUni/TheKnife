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
	private int premi;
	private String servizi;
	private boolean prenotazioneOnline;
	private boolean delivery;
	private String descrizione;
	
	
	//COSTRUTTORE
	public Ristorante(String nome, String indirizzo, String location, double prezzo, String cucina, double longitudine,
					  double latitudine, String numeroTelefono, String websiteUrl, int premi, String servizi, boolean prenotazioneOnline,
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
    
    public int getPremi() { 
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
	
    
    //SETTER
    public void setStelle(int stelle) {
    	if(stelle>=1 && stelle<=5)
    		this.stelle = stelle;
    }
    public void setDescrizione(String descrizione) {
    	this.descrizione = descrizione;
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
    
    
    
    
    
}
