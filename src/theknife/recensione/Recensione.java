/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.recensione;

import java.time.LocalDate;
import java.util.Objects;
import theknife.utente.GestoreDate; 

public class Recensione {

    // campi
    private final String username;
    private final String nomeRistorante;
    private final String locationRistorante;

    private int stelle;
    private String commento;
    private LocalDate data;
    private String risposta;

    // util interna per validare stelle
    private static void checkStelle(int v) {
        if (v < 1 || v > 5) throw new IllegalArgumentException("Le stelle devono essere tra 1 e 5.");
    }

    // costruttori
    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = (locationRistorante == null) ? "" : locationRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = LocalDate.now();
        this.risposta = (risposta == null) ? "" : risposta.trim();
    }

    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento, LocalDate data, String risposta) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = (locationRistorante == null) ? "" : locationRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = LocalDate.now();
        this.risposta = (risposta == null) ? "" : risposta.trim();
    }

    // GETTER/SETTER
    public String getAutore() { return username; }
    public String getNomeRistorante() { return nomeRistorante; }
    public String getLocationRistorante() { return locationRistorante; }

    public int getStelle() { return stelle; }
    public void setStelle(int stelle) { checkStelle(stelle); this.stelle = stelle; }

    public String getDescrizione() { return commento; }
    public void setDescrizione(String descrizione) { this.commento = (descrizione == null) ? "" : descrizione; }

    public LocalDate getData() { return data; }
    public String getRisposta() { return risposta; }
    public void setRisposta(String risposta) { this.risposta = (risposta == null) ? "" : risposta; }

    // METODI
    @Override
    public String toString() {
        return "Ristorante: " + nomeRistorante +
               (locationRistorante.isEmpty() ? "" : " (" + locationRistorante + ")") + "\n" +
               "Autore: " + username + " *Stelle*: " + stelle + "\n" +
               commento + "\n" +
               "Data: " + GestoreDate.formatOrEmpty(data) + "\n" +
               "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
    }

    public String visualizzaRecensione() {
        String base = "Autore: " + username + " *Stelle*: " + stelle + "\n" +
                      commento + "\n" +
                      "Data: " + GestoreDate.formatOrEmpty(data);
        if (!risposta.isEmpty()) base += "\nRisposta del ristoratore: " + risposta;
        return base;
    }

    public boolean isPositiva() { return stelle >= 4; }
    public boolean isRecente()  { return data != null && data.isAfter(LocalDate.now().minusDays(30)); }

    public void modificaRecensione(int newStelle, String newDescrizione) {
        checkStelle(newStelle);
        this.stelle = newStelle;
        this.commento = newDescrizione;
        this.data = LocalDate.now();
    }

    public void eliminaRisposta() { this.risposta = ""; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recensione)) return false;
        Recensione that = (Recensione) o;
        return stelle == that.stelle
                && Objects.equals(username, that.username)
                && Objects.equals(nomeRistorante, that.nomeRistorante)
                && Objects.equals(locationRistorante, that.locationRistorante)
                && Objects.equals(data, that.data)
                && Objects.equals(commento, that.commento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, nomeRistorante, locationRistorante, data, commento, stelle);
    }
}