/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.recensione;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Recensione {

    // campi
    private final String username;
    private final String nomeRistorante;
    private final String locationRistorante; // <— chiave insieme al nome

    private int stelle;
    private String commento;
    private LocalDate data;
    private String risposta;

    // COSTRUTTORE "nuovo" con location (data = oggi, risposta vuota)
    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = locationRistorante == null ? "" : locationRistorante;
        this.stelle = stelle;
        this.commento = commento;
        this.data = LocalDate.now();
        this.risposta = "";
    }

    // COSTRUTTORE "nuovo" completo
    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento, LocalDate data, String risposta) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = locationRistorante == null ? "" : locationRistorante;
        this.stelle = stelle;
        this.commento = commento;
        this.data = data;
        this.risposta = risposta == null ? "" : risposta;
    }

    // COSTRUTTORI DI COMPATIBILITÀ (vecchio formato senza location) — opzionali
    public Recensione(String username, String nomeRistorante, int stelle, String commento) {
        this(username, nomeRistorante, "", stelle, commento);
    }
    public Recensione(String username, String nomeRistorante, int stelle, String commento,
                      LocalDate data, String risposta) {
        this(username, nomeRistorante, "", stelle, commento, data, risposta);
    }

    // GETTER/SETTER
    public String getAutore() { return username; }
    public String getNomeRistorante() { return nomeRistorante; }
    public String getLocationRistorante() { return locationRistorante; }

    public int getStelle() { return stelle; }
    public void setStelle(int stelle) { this.stelle = stelle; }

    public String getDescrizione() { return commento; }
    public void setDescrizione(String descrizione) { this.commento = descrizione; }

    public LocalDate getData() { return data; }
    public String getRisposta() { return risposta; }
    public void setRisposta(String risposta) { this.risposta = risposta == null ? "" : risposta; }

    // METODI
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Ristorante: " + nomeRistorante +
               (locationRistorante.isEmpty() ? "" : " (" + locationRistorante + ")") + "\n" +
               "Autore: " + username + " *Stelle*: " + stelle + "\n" +
               commento + "\n" +
               "Data: " + data.format(fmt) + "\n" +
               "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
    }

    public String visualizzaRecensione() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String base = "Autore: " + username + " *Stelle*: " + stelle + "\n" +
                      commento + "\n" +
                      "Data: " + data.format(fmt);
        if (!risposta.isEmpty()) base += "\nRisposta del ristoratore: " + risposta;
        return base;
    }

    public boolean isPositiva() { return stelle >= 4; }
    public boolean isRecente()  { return data.isAfter(LocalDate.now().minusDays(30)); }

    public void modificaRecensione(int newStelle, String newDescrizione) {
        if (newStelle < 1 || newStelle > 5)
            throw new IllegalArgumentException("Le stelle devono essere tra 1 e 5.");
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