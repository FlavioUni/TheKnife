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
 * La classe Recensione rappresenta una recensione di un utente verso un ristorante.
 * Include informazioni sull'autore, il ristorante recensito, un voto in stelle (1-5),
 * un commento testuale, la data della recensione e un'eventuale risposta del RISTORATORE.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class Recensione {

    // CAMPI
    private final String username;
    private final String idRistorante;

    private int stelle;
    private String commento;
    private LocalDate data;
    private String risposta;

    /**
     * COSTRUTTORE parametrico per una nuova recensione con data corrente e senza risposta.
     * 
     * @param username Nome utente dell'autore della recensione
     * @param idRistorante Identificativo unico del ristorante recensito
     * @param stelle Valutazione in stelle (da 1 a 5, con controllo)
     * @param commento Testo del commento/recensione
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public Recensione(String username, String idRistorante, int stelle, String commento) {
        this.username = username;
        this.idRistorante = idRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = LocalDate.now();
        this.risposta = "";
    }

    /**
     * COSTRUTTORE parametrico completo per una recensione con tutti i campi specificati (compresa risposta RISTORATORE).
     * 
     * @param username Nome utente dell'autore della recensione
     * @param idRistorante Identificativo unico del ristorante recensito
     * @param stelle Valutazione in stelle (da 1 a 5, con controllo)
     * @param commento Testo del commento/recensione
     * @param data Data della recensione
     * @param risposta Eventuale risposta del gestore del ristorante
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public Recensione(String username, String idRistorante, int stelle, String commento,
                      LocalDate data, String risposta) {
        this.username = username;
        this.idRistorante = idRistorante;
        checkStelle(stelle);
        this.stelle = stelle;
        this.commento = (commento == null) ? "" : commento.trim();
        this.data = data;
        this.risposta = (risposta == null) ? "" : risposta.trim();
    }

    // GETTER
    public String getAutore() {return username;}
    public String getIdRistorante() {return idRistorante;}
    public int getStelle() {return stelle;}
    public String getDescrizione() {return commento;}
    public LocalDate getData() { return data; }
    public String getRisposta() {return risposta;}

    // SETTER
    public void setDescrizione(String descrizione) {this.commento = (descrizione == null) ? "" : descrizione;}
    public void setRisposta(String risposta) {this.risposta = (risposta == null) ? "" : risposta;}
    
    /**
     * Imposta il voto in stelle della recensione.
     * @param stelle Nuovo voto in stelle (da 1 a 5)
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public void setStelle(int stelle) { checkStelle(stelle); this.stelle = stelle; }

    // METODI
    
    /**
     * Verifica che il numero di stelle sia nel range valido (1-5).
     * 
     * @param v Numero di stelle da validare
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    private static void checkStelle(int v) {
        if (v < 1 || v > 5) throw new IllegalArgumentException("Le stelle devono essere tra 1 e 5.");
    }

    /**
     * Restituisce una visualizzazione compatta della recensione.
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
     * Verifica se la recensione è considerata positiva (4 o 5 stelle).
     * 
     * @return true se la recensione ha 4 o 5 stelle, false altrimenti
     */
    public boolean isPositiva() { return stelle >= 4; }
    
    /**
     * Verifica se la recensione è recente (entro gli ultimi 30 giorni).
     * 
     * @return true se la recensione è stata fatta negli ultimi 30 giorni, false altrimenti
     */
    public boolean isRecente()  { return data != null && data.isAfter(LocalDate.now().minusDays(30)); }

    /**
     * Modifica completamente la recensione aggiornando stelle e descrizione.
     * 
     * @param newStelle Nuovo valore di stelle (da 1 a 5)
     * @param newDescrizione Nuovo testo della recensione
     * @throws IllegalArgumentException se le stelle non sono nel range 1-5
     */
    public void modificaRecensione(int newStelle, String newDescrizione) {
        checkStelle(newStelle);
        this.stelle = newStelle;
        this.commento = newDescrizione;
        this.data = LocalDate.now();
    }

    /** Elimina la risposta del gestore del ristorante. */
    public void eliminaRisposta() { this.risposta = ""; }
    
    /**
     * Restituisce una rappresentazione completa della recensione in formato leggibile.
     * @return Stringa formattata con tutti i dettagli della recensione
     */
    @Override
    public String toString() {
        return "Ristorante: " + idRistorante + "\n" +
               "Autore: " + username + " *Stelle*: " + stelle + "\n" +
               commento + "\n" +
               "Data: " + GestoreDate.formatOrEmpty(data) + "\n" +
               "Risposta del ristoratore: " + (risposta.isEmpty() ? "Nessuna" : risposta);
    }

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
                && Objects.equals(idRistorante, that.idRistorante)
                && Objects.equals(data, that.data)
                && Objects.equals(commento, that.commento);
    }

    /**
     * Restituisce un codice hash per la recensione, coerente con il metodo equals().
     * 
     * @return Codice hash calcolato sui campi rilevanti per l'uguaglianza
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, idRistorante, data, commento, stelle);
    }
}