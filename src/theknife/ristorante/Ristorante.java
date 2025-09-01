/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.ristorante;

import theknife.recensione.Recensione;
import java.util.ArrayList;

/**
 * La classe Ristorante rappresenta un ristorante nel sistema.
 * Un ristorante è caratterizzato da: informazioni base (nome, indirizzo, location), dettagli (cucina, prezzi),
 * coordinate geograficas, contatti, servizi offerti e un insieme di recensioni degli utenti.
 * 
 * @author Gabriele Scolaro
 */
public class Ristorante {

    // ATTRIBUTI
    private String nome;
    private String indirizzo;
    private String location;
    private String prezzo;
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
    private String proprietario;

    /**
     * Costruttore parametrico per creare un nuovo ristorante.
     * 
     * @param nome Nome del ristorante
     * @param indirizzo Indirizzo fisico del ristorante
     * @param location Area o città di ubicazione
     * @param prezzo Fascia di prezzo (es. "€€")
     * @param cucina Tipo di cucina offerta
     * @param longitudine Coordinata longitudinale per la mappa
     * @param latitudine Coordinata latitudinale per la mappa
     * @param numeroTelefono Numero di telefono per contatti
     * @param websiteUrl Sito web del ristorante
     * @param premi Premi o riconoscimenti ricevuti
     * @param servizi Servizi offerti (es. "Wi-Fi, Parcheggio")
     * @param prenotazioneOnline Indica se è disponibile la prenotazione online
     * @param delivery Indica se è disponibile il servizio di consegna
     */
    public Ristorante(String nome, String indirizzo, String location, String prezzo, String cucina, double longitudine,
                     double latitudine, String numeroTelefono, String websiteUrl, String premi, String servizi,
                     boolean prenotazioneOnline, boolean delivery) {
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
        this.listaRecensioni = new ArrayList<>();
    }

    // GETTER
    public String getNome() { return nome; }
    public String getIndirizzo() { return indirizzo; }
    public String getLocation() { return location; }
    public String getPrezzo() { return prezzo; }
    public String getCucina() { return cucina; }
    public double getLongitudine() { return longitudine; }
    public double getLatitudine() { return latitudine; }
    public String getNumeroTelefono() { return numeroTelefono; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getPremi() { return premi; }
    public String getServizi() { return servizi; }
    public boolean isPrenotazioneOnline() { return prenotazioneOnline; }
    public boolean isDelivery() { return delivery; }
    public ArrayList<Recensione> getRecensioni() { return listaRecensioni; }
    public Recensione getRecensione(int index) { return listaRecensioni.get(index); }
    public String getProprietario() { return proprietario; }

    // SETTER
    public void setNome(String nome) { this.nome = nome; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public void setLocation(String location) { this.location = location; }
    public void setPrezzo(String prezzo) { this.prezzo = prezzo; }
    public void setCucina(String cucina) { this.cucina = cucina; }
    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }
    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public void setPremi(String premi) { this.premi = premi; }
    public void setServizi(String servizi) { this.servizi = servizi; }
    public void setPrenotazioneOnline(boolean prenotazioneOnline) { this.prenotazioneOnline = prenotazioneOnline; }
    public void setDelivery(boolean delivery) { this.delivery = delivery; }
    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    // RECENSIONI
    /**
     * Aggiunge una recensione alla lista del ristorante
     * @param recensione Recensione da aggiungere (ignora se null)
     */
    public void aggiungiRecensione(Recensione recensione) {
        if (recensione != null) listaRecensioni.add(recensione);
    }

    /**
     * Rimuove una recensione specifica dalla lista
     * @param recensione Recensione da rimuovere
     */
    public void rimuoviRecensione(Recensione recensione) {
        listaRecensioni.remove(recensione);
    }

    /**
     * Rimuove una recensione in base ad autore e descrizione
     * @param username Autore della recensione
     * @param descrizione Testo della recensione
     */
    public void rimuoviRecensione(String username, String descrizione) {
        for (int i = listaRecensioni.size() - 1; i >= 0; i--) {
            Recensione r = listaRecensioni.get(i);
            if (r.getAutore().equals(username) && r.getDescrizione().equals(descrizione)) {
                listaRecensioni.remove(i);
            }
        }
    }

    /**
     * Modifica una recensione esistente aggiornando stelle e descrizione
     * @param username Autore della recensione
     * @param descrizione Nuovo testo della recensione
     * @param nuoveStelle Nuovo voto in stelle
     */
    public void modificaRecensione(String username, String descrizione, int nuoveStelle) {
        for (int i = 0; i < listaRecensioni.size(); i++) {
            Recensione r = listaRecensioni.get(i);
            if (r.getAutore().equals(username) && r.getDescrizione().equals(descrizione)) {
                listaRecensioni.get(i).setDescrizione(descrizione);
                listaRecensioni.get(i).setStelle(nuoveStelle);
            }
        }
    }

    /**
     * Verifica se esiste una recensione di un specifico utente
     * @param username Utente da verificare
     * @return true se esiste almeno una recensione dell'utente
     */
    public boolean esisteRecensioneDiUtente(String username) {
        for (Recensione r : listaRecensioni) {
            if (r.getAutore().equals(username)) return true;
        }
        return false;
    }

    /**
     * Cerca la recensione di un specifico utente
     * @param username Utente da cercare
     * @return La prima recensione trovata dell'utente, o null se non esiste
     */
    public Recensione trovaRecensioneDiUtente(String username) {
        for (Recensione r : listaRecensioni) {
            if (r.getAutore().equals(username)) return r;
        }
        return null;
    }

    /**
     * Calcola la media delle stelle delle recensioni
     * @return Media delle stelle (0.0 se non ci sono recensioni)
     */
    public Double mediaStelle() {
        if (listaRecensioni.isEmpty()) return 0.0;
        double somma = 0.0;
        for (Recensione r : listaRecensioni) somma += r.getStelle();
        return somma / listaRecensioni.size();
    }

    /** Rimuove tutte le recensioni dalla lista */
    public void svuotaRecensioni() {
        this.listaRecensioni.clear();
    }

    /**
     * Restituisce una rappresentazione testuale del ristorante
     * @return Stringa con i principali campi del ristorante
     */
    @Override
    public String toString() {
        return "Ristorante{" +
               "nome='" + nome + '\'' +
               ", indirizzo='" + indirizzo + '\'' +
               ", location='" + location + '\'' +
               ", prezzo='" + prezzo + '\'' +
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