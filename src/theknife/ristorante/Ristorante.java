/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.ristorante;

import theknife.recensione.Recensione;
import java.util.ArrayList;

/**
 * La classe Ristorante rappresenta un ristorante del sistema.
 * Contiene informazioni principali (nome, indirizzo, location), dettagli (cucina, prezzo),
 * coordinate geografiche, contatti, servizi e l’elenco delle recensioni degli utenti.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class Ristorante {

    // CAMPI
    private String id;
    private String nome;
    private String indirizzo;
    private String location;
    private String prezzoMedio;
    private String cucina;
    private double longitudine;
    private double latitudine;
    private String numeroTelefono;
    private String websiteUrl;
    private String premi;
    private String servizi;
    private boolean prenotazioneOnline;
    private boolean delivery;
    private ArrayList<Recensione> listaRecensioni;

    /**
     * COSTRUTTORE parametrico della classe Ristorante.
     * Genera automaticamente un ID univoco (vedi metodo generaIDUnivoco).
     * 
     * @param nome Nome del ristorante
     * @param indirizzo Indirizzo (via e numero civico)
     * @param location Città o area geografica
     * @param prezzoMedio Prezzo medio indicativo (es. "€€" oppure "25")
     * @param cucina Tipologia di cucina
     * @param longitudine Longitudine in gradi decimali
     * @param latitudine Latitudine in gradi decimali
     * @param numeroTelefono Numero di telefono
     * @param websiteUrl Sito web del ristorante
     * @param premi Premi o riconoscimenti
     * @param servizi Elenco servizi offerti (es. "Wi-Fi, Accesso disabili")
     * @param prenotazioneOnline true se è possibile prenotare online
     * @param delivery true se è disponibile la consegna a domicilio
     */
    public Ristorante(String nome, String indirizzo, String location, String prezzoMedio, String cucina,
                      double longitudine, double latitudine, String numeroTelefono, String websiteUrl,
                      String premi, String servizi, boolean prenotazioneOnline, boolean delivery) {
        this.id = generaIDUnivoco();
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.location = location;
        this.prezzoMedio = prezzoMedio;
        this.cucina = cucina;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.numeroTelefono = numeroTelefono;
        this.websiteUrl = websiteUrl;
        this.premi = premi;
        this.servizi = servizi;
        this.prenotazioneOnline = prenotazioneOnline;
        this.delivery = delivery;
        this.listaRecensioni = new ArrayList<>();
    }

    // GETTER
    public String getId() {return id;}
    public String getNome() {return nome;}
    public String getIndirizzo() {return indirizzo;}
    public String getLocation() {return location;}
    public String getPrezzoMedio() {return prezzoMedio;}
    public String getCucina() {return cucina;}
    public double getLongitudine() {return longitudine;}
    public double getLatitudine() {return latitudine;}
    public String getNumeroTelefono() {return numeroTelefono;}
    public String getWebsiteUrl() {return websiteUrl;}
    public String getPremi() {return premi;}
    public String getServizi() {return servizi;}
    public boolean isPrenotazioneOnline() {return prenotazioneOnline;}
    public boolean isDelivery() {return delivery;}
    public ArrayList<Recensione> getRecensioni() {return listaRecensioni;}

    // SETTER
    public void setId(String id) {this.id = id;}
    public void setNome(String nome) {this.nome = nome;}
    public void setIndirizzo(String indirizzo) {this.indirizzo = indirizzo;}
    public void setLocation(String location) {this.location = location;}
    public void setPrezzoMedio(String prezzo) {this.prezzoMedio = prezzo;}
    public void setCucina(String cucina) {this.cucina = cucina;}
    public void setLongitudine(double longitudine) {this.longitudine = longitudine;}
    public void setLatitudine(double latitudine) {this.latitudine = latitudine;}
    public void setNumeroTelefono(String numeroTelefono) {this.numeroTelefono = numeroTelefono;}
    public void setWebsiteUrl(String websiteUrl) {this.websiteUrl = websiteUrl;}
    public void setPremi(String premi) {this.premi = premi;}
    public void setServizi(String servizi) {this.servizi = servizi;}
    public void setPrenotazioneOnline(boolean prenotazioneOnline) {this.prenotazioneOnline = prenotazioneOnline;}
    public void setDelivery(boolean delivery) {this.delivery = delivery;}

    // METODI

    /**
     * Aggiunge una recensione alla lista, ignorando i valori null.
     * 
     * @param recensione Recensione da aggiungere
     */
    public void aggiungiRecensione(Recensione recensione) {
        if (recensione != null) listaRecensioni.add(recensione);
    }

    /**
     * Verifica se esiste già una recensione per un dato utente.
     *
     * @param username Username dell’autore
     * @return true se è presente almeno una recensione di quell’utente, altrimenti false
     */
    public boolean esisteRecensioneDiUtente(String username) {
        for (Recensione r : listaRecensioni) {
            if (r.getAutore().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce la prima recensione trovata per un dato utente.
     *
     * @param username Username dell’autore
     * @return Recensione dell’utente, oppure null se non trovata
     */
    public Recensione trovaRecensioneDiUtente(String username) {
        for (Recensione r : listaRecensioni) {
            if (r.getAutore().equals(username)) {
                return r;
            }
        }
        return null;
    }

    /**
     * Calcola la media delle stelle delle recensioni di un ristorante.
     *
     * @return Media in formato double oppure NaN se non ci sono recensioni
     */
    public Double mediaStelle() {
        if (listaRecensioni.isEmpty()) {
            return Double.NaN;
        }
        int somma = 0;
        for (Recensione r : listaRecensioni) {
            somma += r.getStelle();
        }
        return (double) somma / listaRecensioni.size();
    }

    /**
     * Genera un ID univoco per il ristorante, utile per associarlo ad un utente.
     * 
     * @return Stringa ID nel formato "RXXXXXXXX"
     */
    private static String generaIDUnivoco() {
        return "R" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Restituisce una rappresentazione leggibile del ristorante con i campi principali.
     * 
     * @return Stringa formattata con i dettagli del ristorante
     */
    @Override
    public String toString() {
        return "Ristorante{" +
               "id='" + id + '\'' +
               ", nome='" + nome + '\'' +
               ", indirizzo='" + indirizzo + '\'' +
               ", location='" + location + '\'' +
               ", prezzo='" + prezzoMedio + '\'' +
               ", cucina='" + cucina + '\'' +
               ", longitudine=" + longitudine +
               ", latitudine=" + latitudine +
               ", numeroTelefono='" + numeroTelefono + '\'' +
               ", websiteUrl='" + websiteUrl + '\'' +
               ", premi='" + premi + '\'' +
               ", servizi='" + servizi + '\'' +
               ", prenotazioneOnline=" + prenotazioneOnline +
               ", delivery=" + delivery +
               '}';
    }
}