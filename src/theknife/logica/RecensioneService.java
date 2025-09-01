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
 * La classe RecensioneService rappresenta la logica applicativa vera e propria per quanto riguarda le recensioni.
 * Si occupa quindi di gestire le recensioni controllando regole di business, validazioni e aggiornamenti.
 */

public class RecensioneService {

    private final DataContext dataContext;

    /**
     * COSTRUTTORE parametrico che prende in ingresso un oggetto DataContext per accedere 
     * a tutti i dati che stanno in quella classe, ovvero a tutti i dati del progetto.
     * @param dataContext
     */
    public RecensioneService(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    /**
     * Aggiunge una recensione a un ristorante da parte di un utente
     * @param autore Autore della recensione
     * @param ristorante Ristorante su cui si scrive la recensione
     * @param stelle Valutazione in stelle
     * @param testo Testo del commento/recensione
     * @return
     */
    public Recensione aggiungiRecensione(Utente autore, Ristorante ristorante, int stelle, String testo) {
        if (autore == null) throw new IllegalArgumentException("Utente non valido");
        if (ristorante == null) throw new IllegalArgumentException("Ristorante non valido");
        if (stelle < 1 || stelle > 5) throw new IllegalArgumentException("Stelle devono essere 1–5");

        // no doppie recensioni sullo stesso ristorante
        if (ristorante.trovaRecensioneDiUtente(autore.getUsername()) != null) {
            throw new IllegalStateException("Hai già recensito questo ristorante.");
        }

        Recensione r = new Recensione(
                autore.getUsername(),
                ristorante.getNome(),
                ristorante.getLocation(),
                stelle,
                testo
        );

        // delega a DataContext che aggiorna liste e indici
        boolean ok = dataContext.addRecensione(r);
        return ok ? r : null;
    }

    /**
     * Il ristoratore risponde a una recensione del proprio ristorante
     * @param ristoratore Nome del proprietario del ristorante
     * @param ristorante Nome del ristorante
     * @param recensione Recensione alla quale si vuole rispondere
     * @param risposta Risposta alla recensione
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
     * L’autore modifica la propria recensione
     * @param autore  Autore della recensione
     * @param recensione Recensione a cui si fa riferimento per la modifica
     * @param nuoveStelle Cambio di valutazione in stelle
     * @param nuovoTesto Nuova descrizione alla recensione
     */
    public void modificaRecensione(Utente autore, Recensione recensione, int nuoveStelle, String nuovoTesto) {
        if (autore == null) throw new IllegalArgumentException("Utente non valido");
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");

        if (!recensione.getAutore().equalsIgnoreCase(autore.getUsername())) {
            throw new SecurityException("Puoi modificare solo le tue recensioni.");
        }
        recensione.modificaRecensione(nuoveStelle, nuovoTesto);
    }

    /**
     * L’utente elimina la propria recensione
     * @param utente Utente che vuole eliminare la recensione
     * @param ristorante Ristorante a cui si fa riferimento
     * @param recensione Recensione che si vuole eliminare
     */
    public void eliminaRecensione(Utente utente, Ristorante ristorante, Recensione recensione) {
        if (utente == null) throw new IllegalArgumentException("Utente non valido");
        if (ristorante == null) throw new IllegalArgumentException("Ristorante non valido");
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");

        boolean isAutore = recensione.getAutore().equalsIgnoreCase(utente.getUsername());
        boolean isGestore = utente.gestisce(ristorante);
        if (!isAutore && !isGestore) {
            throw new SecurityException("Non puoi eliminare questa recensione.");
        }

        /**
         *Delega a DataContext per tenere allineati liste & indici
         */
        dataContext.removeRecensione(recensione);
    }
}