/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.recensione;

import java.time.LocalDate;
import java.util.Objects;
import theknife.logica.GestoreDate; 

/**
 * La classe Recensione rappresenta una valutazione e commento di un utente verso un ristorante.
 * Include informazioni sull'autore, il ristorante recensito, un voto in stelle (1-5),
 * un commento testuale, la data della recensione e un'eventuale risposta del gestore.
 * La data viene gestita internamente come oggetto LocalDate e formattata nel formato "dd/MM/aaaa"
 * per la visualizzazione tramite il {@link theknife.logica.GestoreDate#formatOrEmpty(LocalDate)}.
 */
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

    /**
     * Costruttore per una nuova recensione con data corrente.
     * La data viene impostata automaticamente alla data odierna.
     * 
     * @param username Nome utente dell'autore della recensione
     * @param nomeRistorante Nome del ristorante recensito
     * @param locationRistorante Location/area del ristorante recensito
     * @param stelle Valutazione in stelle (da 1 a 5)
     * @param commento Testo del commento/recensione
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = (locationRistorante == null) ? "" : locationRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = LocalDate.now();
        this.risposta = ""; // Correzione: inizializzata a stringa vuota invece di controllo su null
    }

    /**
     * Costruttore completo per una recensione con tutti i campi specificabili.
     * 
     * @param username Nome utente dell'autore della recensione
     * @param nomeRistorante Nome del ristorante recensito
     * @param locationRistorante Location/area del ristorante recensito
     * @param stelle Valutazione in stelle (da 1 a 5)
     * @param commento Testo del commento/recensione
     * @param data Data della recensione (come oggetto LocalDate)
     * @param risposta Eventuale risposta del gestore del ristorante
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public Recensione(String username, String nomeRistorante, String locationRistorante,
                      int stelle, String commento, LocalDate data, String risposta) {
        this.username = username;
        this.nomeRistorante = nomeRistorante;
        this.locationRistorante = (locationRistorante == null) ? "" : locationRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = data; // Correzione: uso del parametro data invece di impostare sempre now()
        this.risposta = (risposta == null) ? "" : risposta.trim();
    }

    // GETTER
    /** Restituisce l'autore della recensione */
    public String getAutore() { return username; }
    
    /** Restituisce il nome del ristorante recensito */
    public String getNomeRistorante() { return nomeRistorante; }
    
    /** Restituisce la location del ristorante recensito */
    public String getLocationRistorante() { return locationRistorante; }
    
    /** Restituisce il voto in stelle (1-5) */
    public int getStelle() { return stelle; }
    
    /** Restituisce il testo del commento/recensione */
    public String getDescrizione() { return commento; }
    
    /** Restituisce la data della recensione come oggetto LocalDate */
    public LocalDate getData() { return data; }
    
    /** Restituisce l'eventuale risposta del gestore del ristorante */
    public String getRisposta() { return risposta; }

    // SETTER
    /**
     * Imposta il voto in stelle della recensione
     * @param stelle Nuovo voto in stelle (da 1 a 5)
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public void setStelle(int stelle) { checkStelle(stelle); this.stelle = stelle; }
    
    /**
     * Imposta il testo del commento/recensione
     * @param descrizione Nuovo testo della recensione
     */
    public void setDescrizione(String descrizione) { this.commento = (descrizione == null) ? "" : descrizione; }
    
    /**
     * Imposta la risposta del gestore del ristorante
     * @param risposta Testo della risposta
     */
    public void setRisposta(String risposta) { this.risposta = (risposta == null) ? "" : risposta; }

    // METODI
    /**
     * Restituisce una rappresentazione completa della recensione in formato leggibile.
     * La data viene formattata nel formato "dd/MM/aaaa" tramite {@link theknife.logica.GestoreDate#formatOrEmpty(LocalDate)}.
     * 
     * @return Stringa formattata con tutti i dettagli della recensione
     */
    @Override
    public String toString() {
        return "Ristorante: " + nomeRistorante +
               (locationRistorante.isEmpty() ? "" : " (" + locationRistorante + ")") + "\n" +
               "Autore: " + username + " *Stelle*: " + stelle + "\n" +
               commento + "\n" +
               "Data: " + GestoreDate.formatOrEmpty(data) + "\n" +
               "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
    }

    /**
     * Restituisce una visualizzazione compatta della recensione.
     * La data viene formattata nel formato "dd/MM/aaaa" tramite {@link theknife.logica.GestoreDate#formatOrEmpty(LocalDate)}.
     * 
     * @return Stringa formattata con i dettagli principali della recensione
     */
    public String visualizzaRecensione() {
        String base = "Autore: " + username + " *Stelle*: " + stelle + "\n" +
                      commento + "\n" +
                      "Data: " + GestoreDate.formatOrEmpty(data);
        if (!risposta.isEmpty()) base += "\nRisposta del ristoratore: " + risposta;
        return base;
    }

    /**
     * Verifica se la recensione è considerata positiva (4 o 5 stelle)
     * @return true se la recensione ha 4 o 5 stelle, false altrimenti
     */
    public boolean isPositiva() { return stelle >= 4; }
    
    /**
     * Verifica se la recensione è recente (entro gli ultimi 30 giorni)
     * @return true se la recensione è stata fatta negli ultimi 30 giorni, false altrimenti
     */
    public boolean isRecente()  { return data != null && data.isAfter(LocalDate.now().minusDays(30)); }

    /**
     * Modifica completamente la recensione aggiornando stelle e descrizione.
     * La data della recensione viene aggiornata alla data corrente.
     * 
     * @param newStelle Nuovo voto in stelle (da 1 a 5)
     * @param newDescrizione Nuovo testo della recensione
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public void modificaRecensione(int newStelle, String newDescrizione) {
        checkStelle(newStelle);
        this.stelle = newStelle;
        this.commento = newDescrizione;
        this.data = LocalDate.now();
    }

    /** Elimina la risposta del gestore del ristorante */
    public void eliminaRisposta() { this.risposta = ""; }

    /**
     * Confronta questa recensione con un altro oggetto per verificarne l'uguaglianza.
     * Due recensioni sono considerate uguali se hanno stesso autore, stesso ristorante,
     * stessa data, stesso commento e stesso numero di stelle.
     * 
     * @param o Oggetto da confrontare
     * @return true se gli oggetti sono considerati uguali, false altrimenti
     */
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

    /**
     * Restituisce un codice hash per la recensione, coerente con il metodo equals()
     * @return Codice hash calcolato sui campi rilevanti per l'uguaglianza
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, nomeRistorante, locationRistorante, data, commento, stelle);
    }
}