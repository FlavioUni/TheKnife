/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/
package theknife.logica;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;

/**
 * La classe RecensioneService fornisce i servizi principali per la gestione delle recensioni.
 * Comprende l’aggiunta, la modifica e la risposta di una recensione, applicando i controlli di sicurezza.
 * 
 * @author Gasparini Lorenzo
 * @author Ciani Flavio Angelo
 * @author Scolaro Gabriele
 */
public class RecensioneService {

    private final DataContext dataContext;

    /**
     * COSTRUTTORE parametrico della classe RecensioneService.
     * 
     * @param dataContext Oggetto che contiene tutti i dati dell'applicazione (utenti, ristoranti, recensioni)
     */
    public RecensioneService(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    /**
     * Aggiunge una recensione a un ristorante da parte di un utente.
     * Non sono permesse più recensioni dello stesso utente sullo stesso ristorante.
     * 
     * @param autore Utente che scrive la recensione
     * @param ristorante Ristorante recensito
     * @param stelle Numero di stelle (da 1 a 5)
     * @param testo Testo del commento
     * @return La recensione creata, oppure null se non è stato possibile aggiungerla
     * @throws IllegalArgumentException Se i parametri non sono validi
     * @throws IllegalStateException Se l’utente ha già recensito il ristorante
     */
    public Recensione aggiungiRecensione(Utente autore, Ristorante ristorante, int stelle, String testo) {
        if (autore == null) 
        	throw new IllegalArgumentException("Utente non valido");
        if (ristorante == null) 
        	throw new IllegalArgumentException("Ristorante non valido");
        if (stelle < 1 || stelle > 5) 
        	throw new IllegalArgumentException("Stelle devono essere tra 1 e 5");

        if (ristorante.trovaRecensioneDiUtente(autore.getUsername()) != null) {
            throw new IllegalStateException("Hai già recensito questo ristorante.");
        }

        Recensione r = new Recensione(
                autore.getUsername(),
                ristorante.getId(),
                stelle,
                testo);

        return dataContext.addRecensione(r) ? r : null;
    }

    /**
     * Permette al ristoratore di rispondere a una recensione di un proprio ristorante.
     * 
     * @param ristoratore Utente con ruolo RISTORATORE
     * @param ristorante Ristorante di cui è gestore
     * @param recensione Recensione a cui rispondere
     * @param risposta Testo della risposta
     * @throws IllegalArgumentException Se i parametri non sono validi
     * @throws SecurityException Se il ristoratore non gestisce il ristorante
     */
    public void rispondiRecensione(Utente ristoratore, Ristorante ristorante, Recensione recensione, String risposta) {
        if (ristoratore == null) throw new IllegalArgumentException("Ristoratore non valido");
        if (ristorante == null) throw new IllegalArgumentException("Ristorante non valido");
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");

        if (!ristoratore.gestisce(ristorante)) {
            throw new SecurityException("Non puoi rispondere a recensioni di ristoranti che non gestisci.");
        }
        recensione.setRisposta(risposta);
    }

    /**
     * Permette all’autore di modificare una propria recensione.
     * 
     * @param autore Utente autore della recensione
     * @param recensione Recensione da modificare
     * @param nuoveStelle Nuovo numero di stelle (1–5)
     * @param nuovoTesto Nuovo testo della recensione
     * @throws IllegalArgumentException Se i parametri non sono validi
     * @throws SecurityException Se l’utente non è l’autore della recensione
     */
    public void modificaRecensione(Utente autore, Recensione recensione, int nuoveStelle, String nuovoTesto) {
        if (autore == null) throw new IllegalArgumentException("Utente non valido");
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");

        if (!recensione.getAutore().equalsIgnoreCase(autore.getUsername())) {
            throw new SecurityException("Puoi modificare solo le tue recensioni.");
        }
        recensione.modificaRecensione(nuoveStelle, nuovoTesto);
    }

}