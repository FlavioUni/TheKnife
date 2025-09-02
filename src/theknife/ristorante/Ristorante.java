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
 * coordinate geografiche, contatti, servizi offerti e un insieme di recensioni degli utenti.
 */
public class Ristorante {

    // ATTRIBUTI
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
     * Costruttore completo: genera automaticamente un ID univoco.
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
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getIndirizzo() { return indirizzo; }
    public String getLocation() { return location; }
    public String getPrezzoMedio() { return prezzoMedio; }
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

    // SETTER
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }
    public void setLocation(String location) { this.location = location; }
    public void setPrezzoMedio(String prezzo) { this.prezzoMedio = prezzo; }
    public void setCucina(String cucina) { this.cucina = cucina; }
    public void setLongitudine(double longitudine) { this.longitudine = longitudine; }
    public void setLatitudine(double latitudine) { this.latitudine = latitudine; }
    public void setNumeroTelefono(String numeroTelefono) { this.numeroTelefono = numeroTelefono; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public void setPremi(String premi) { this.premi = premi; }
    public void setServizi(String servizi) { this.servizi = servizi; }
    public void setPrenotazioneOnline(boolean prenotazioneOnline) { this.prenotazioneOnline = prenotazioneOnline; }
    public void setDelivery(boolean delivery) { this.delivery = delivery; }

    // RECENSIONI
    public void aggiungiRecensione(Recensione recensione) {
        if (recensione != null) listaRecensioni.add(recensione);
    }

    public void rimuoviRecensione(Recensione recensione) {
        listaRecensioni.remove(recensione);
    }

    public void rimuoviRecensione(String username, String descrizione) {
        listaRecensioni.removeIf(r ->
            r.getAutore().equals(username) && r.getDescrizione().equals(descrizione)
        );
    }

    public void modificaRecensione(String username, String descrizione, int nuoveStelle) {
        for (Recensione r : listaRecensioni) {
            if (r.getAutore().equals(username) && r.getDescrizione().equals(descrizione)) {
                r.setDescrizione(descrizione);
                r.setStelle(nuoveStelle);
            }
        }
    }

    public boolean esisteRecensioneDiUtente(String username) {
        return listaRecensioni.stream()
                .anyMatch(r -> r.getAutore().equals(username));
    }

    public Recensione trovaRecensioneDiUtente(String username) {
        return listaRecensioni.stream()
                .filter(r -> r.getAutore().equals(username))
                .findFirst()
                .orElse(null);
    }

    public Double mediaStelle() {
        if (listaRecensioni.isEmpty()) return Double.NaN;
        return listaRecensioni.stream().mapToInt(Recensione::getStelle).average().orElse(Double.NaN);
    }

    public void svuotaRecensioni() {
        this.listaRecensioni.clear();
    }

    private static String generaIDUnivoco() {
        return "R" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

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