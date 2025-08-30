/*
Ciani Flavio Angelo, 761581, VA
Scolaro Gabriele, 760123, VA
Gasparini Lorenzo, 759929, VA
*/

package theknife.logica;

import theknife.recensione.Recensione;
import theknife.ristorante.Ristorante;
import theknife.utente.Utente;
import java.util.*;

public class RecensioneService {
    private final DataContext dataContext;

    public RecensioneService(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    // metodi TODO sotto
    
    
    //aggiuge recensione
    public Recensione aggiungiRecensione(Utente autore, Ristorante ristorante, int stelle, String testo) {
        // Validazioni
        if (autore == null) throw new IllegalArgumentException("Utente non valido");
        if (ristorante == null) throw new IllegalArgumentException("Ristorante non valido");
        if (stelle < 1 || stelle > 5) throw new IllegalArgumentException("Stelle devono essere 1–5");

        // Crea recensione
        Recensione r = new Recensione(autore, ristorante, stelle, testo, new Date());

        // Aggiorna RAM
        ristorante.getRecensioni().add(r);
        dataContext.getGestoreRecensioni().getAll().add(r);

        return r;
    }

    
    //risponde alla recensione
    public void rispondiRecensione(Utente ristoratore, Recensione recensione, String risposta) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");
        if (ristoratore == null || !ristoratore.gestisce(recensione.getRistorante())) {
            throw new SecurityException("Non puoi rispondere a questo ristorante");
        }
        recensione.setRisposta(risposta);
    }
    
    //modifica recensione
    public void modificaRecensione(Utente autore, Recensione recensione, int nuoveStelle, String nuovoTesto) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");
        if (!recensione.getAutore().equals(autore)) {
            throw new SecurityException("Puoi modificare solo le tue recensioni");
        }
        if (nuoveStelle < 1 || nuoveStelle > 5) throw new IllegalArgumentException("Stelle devono essere 1–5");

        recensione.setStelle(nuoveStelle);
        recensione.setTesto(nuovoTesto);
        recensione.setDataUltimaModifica(new Date());
    }
    
    
    
    //elimina recensione
    public void eliminaRecensione(Utente utente, Recensione recensione) {
        if (recensione == null) throw new IllegalArgumentException("Recensione nulla");

        // Autore può eliminare la sua, ristoratore può rimuovere dal proprio ristorante
        if (!recensione.getAutore().equals(utente) &&
            !utente.gestisce(recensione.getRistorante())) {
            throw new SecurityException("Non puoi eliminare questa recensione");
        }

        // Rimuovi da RAM
        recensione.getRistorante().getRecensioni().remove(recensione);
        dataContext.getGestoreRecensioni().getAll().remove(recensione);
    }


    
}
